package com.thewu.eship.service.fedex;

import com.thewu.eship.config.FedexApiConfig;
import com.thewu.eship.dto.fedex.FedexRateRequest;
import com.thewu.eship.dto.fedex.FedexRateResponse;
import com.thewu.eship.dto.shipping.AddressDTO;
import com.thewu.eship.dto.shipping.CarrierType;
import com.thewu.eship.dto.shipping.PackageDTO;
import com.thewu.eship.dto.shipping.RateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service for FedEx Rate API integration.
 * Provides rate shopping functionality using FedEx Rate Quotes API.
 */
@Service
public class FedexRatingService {

    private static final Logger log = LoggerFactory.getLogger(FedexRatingService.class);

    @Autowired
    private FedexApiConfig config;

    @Autowired
    private FedexOAuthService oauthService;

    @Autowired
    @Qualifier("fedexRestTemplate")
    private RestTemplate restTemplate;

    /**
     * Get rates from all available FedEx services
     */
    public List<RateDTO> shopRates(AddressDTO origin, AddressDTO destination, List<PackageDTO> packages) {
        try {
            log.info("Fetching FedEx rates for shipment from {} to {}", origin.getCity(), destination.getCity());

            FedexRateRequest request = buildFedexRateRequest(origin, destination, packages);

            HttpHeaders headers = oauthService.createFedexHeaders();
            HttpEntity<FedexRateRequest> entity = new HttpEntity<>(request, headers);

            String url = config.getBaseUrl() + "/rate/v1/rates/quotes";

            ResponseEntity<FedexRateResponse> response = restTemplate.postForEntity(
                    url,
                    entity,
                    FedexRateResponse.class);

            if (response.getBody() != null && response.getBody().getOutput() != null) {
                return convertFedexRatesToDTOs(response.getBody());
            }

            log.warn("No rates returned from FedEx API");
            return new ArrayList<>();

        } catch (Exception e) {
            log.error("Error fetching FedEx rates", e);
            throw new RuntimeException("Failed to get FedEx rates", e);
        }
    }

    /**
     * Get rate for a specific FedEx service
     */
    public RateDTO getSingleRate(AddressDTO origin, AddressDTO destination, List<PackageDTO> packages,
            String serviceCode) {
        try {
            log.info("Fetching FedEx rate for service: {}", serviceCode);

            FedexRateRequest request = buildFedexRateRequest(origin, destination, packages);
            request.getRequestedShipment().setServiceType(serviceCode);

            HttpHeaders headers = oauthService.createFedexHeaders();
            HttpEntity<FedexRateRequest> entity = new HttpEntity<>(request, headers);

            String url = config.getBaseUrl() + "/rate/v1/rates/quotes";

            ResponseEntity<FedexRateResponse> response = restTemplate.postForEntity(
                    url,
                    entity,
                    FedexRateResponse.class);

            if (response.getBody() != null && response.getBody().getOutput() != null) {
                List<RateDTO> rates = convertFedexRatesToDTOs(response.getBody());
                return rates.isEmpty() ? null : rates.get(0);
            }

            return null;

        } catch (Exception e) {
            log.error("Error fetching FedEx rate for service: {}", serviceCode, e);
            throw new RuntimeException("Failed to get FedEx rate", e);
        }
    }

    /**
     * Build FedEx rate request from application DTOs
     */
    private FedexRateRequest buildFedexRateRequest(AddressDTO origin, AddressDTO destination,
            List<PackageDTO> packages) {
        FedexRateRequest request = new FedexRateRequest();

        // Account number
        FedexRateRequest.AccountNumber accountNumber = new FedexRateRequest.AccountNumber();
        accountNumber.setValue(config.getAccountNumber());
        request.setAccountNumber(accountNumber);

        // Requested shipment
        FedexRateRequest.RequestedShipment shipment = new FedexRateRequest.RequestedShipment();

        // Shipper (origin)
        FedexRateRequest.Party shipper = new FedexRateRequest.Party();
        shipper.setAddress(convertToFedexAddress(origin));
        shipment.setShipper(shipper);

        // Recipient (destination)
        FedexRateRequest.Party recipient = new FedexRateRequest.Party();
        recipient.setAddress(convertToFedexAddress(destination));
        shipment.setRecipient(recipient);

        // Pickup type and rate request type
        shipment.setPickupType("DROPOFF_AT_FEDEX_LOCATION");
        shipment.setRateRequestType(Arrays.asList("ACCOUNT", "LIST"));

        // Packages
        List<FedexRateRequest.RequestedPackageLineItem> lineItems = new ArrayList<>();
        for (PackageDTO pkg : packages) {
            FedexRateRequest.RequestedPackageLineItem lineItem = new FedexRateRequest.RequestedPackageLineItem();

            // Weight
            FedexRateRequest.Weight weight = new FedexRateRequest.Weight();
            weight.setUnits("LB");
            weight.setValue(pkg.getWeight());
            lineItem.setWeight(weight);

            // Dimensions
            if (pkg.getLength() != null && pkg.getWidth() != null && pkg.getHeight() != null) {
                FedexRateRequest.Dimensions dimensions = new FedexRateRequest.Dimensions();
                dimensions.setLength(pkg.getLength().intValue());
                dimensions.setWidth(pkg.getWidth().intValue());
                dimensions.setHeight(pkg.getHeight().intValue());
                dimensions.setUnits("IN");
                lineItem.setDimensions(dimensions);
            }

            lineItems.add(lineItem);
        }
        shipment.setRequestedPackageLineItems(lineItems);

        request.setRequestedShipment(shipment);
        return request;
    }

    /**
     * Convert application AddressDTO to FedEx Address
     */
    private FedexRateRequest.Address convertToFedexAddress(AddressDTO address) {
        FedexRateRequest.Address fedexAddress = new FedexRateRequest.Address();

        List<String> streetLines = new ArrayList<>();
        if (address.getStreet1() != null) {
            streetLines.add(address.getStreet1());
        }
        if (address.getStreet2() != null) {
            streetLines.add(address.getStreet2());
        }
        fedexAddress.setStreetLines(streetLines);

        fedexAddress.setCity(address.getCity());
        fedexAddress.setStateOrProvinceCode(address.getState());
        fedexAddress.setPostalCode(address.getPostalCode());
        fedexAddress.setCountryCode(address.getCountry() != null ? address.getCountry() : "US");
        fedexAddress.setResidential(false); // Default to commercial

        return fedexAddress;
    }

    /**
     * Convert FedEx rate response to application RateDTOs
     */
    private List<RateDTO> convertFedexRatesToDTOs(FedexRateResponse response) {
        List<RateDTO> rates = new ArrayList<>();

        if (response.getOutput() == null || response.getOutput().getRateReplyDetails() == null) {
            return rates;
        }

        for (FedexRateResponse.RateReplyDetail detail : response.getOutput().getRateReplyDetails()) {
            if (detail.getRatedShipmentDetails() == null || detail.getRatedShipmentDetails().isEmpty()) {
                continue;
            }

            // Use account rates if available, otherwise list rates
            FedexRateResponse.RatedShipmentDetail ratedDetail = detail.getRatedShipmentDetails().get(0);

            RateDTO rate = new RateDTO();
            rate.setCarrier(CarrierType.FEDEX);
            rate.setService(detail.getServiceName() != null ? detail.getServiceName() : detail.getServiceType());

            // Price
            Double totalCharge = ratedDetail.getTotalNetFedExCharge();
            if (totalCharge == null) {
                totalCharge = ratedDetail.getTotalNetCharge();
            }
            if (totalCharge == null) {
                totalCharge = ratedDetail.getTotalBaseCharge();
            }
            rate.setRate(totalCharge != null ? totalCharge : 0.0);

            // Currency
            rate.setCurrency(ratedDetail.getCurrency() != null ? ratedDetail.getCurrency() : "USD");

            // Transit time
            if (detail.getOperationalDetail() != null && detail.getOperationalDetail().getTransitTime() != null) {
                String transitTime = detail.getOperationalDetail().getTransitTime();
                rate.setDeliveryDays(parseTransitTimeToDays(transitTime));
            }

            // Carrier rate ID
            rate.setCarrierRateId("FEDEX-" + detail.getServiceType());

            rates.add(rate);
        }

        log.info("Converted {} FedEx rates to DTOs", rates.size());
        return rates;
    }

    /**
     * Parse FedEx transit time string to number of days
     */
    private Integer parseTransitTimeToDays(String transitTime) {
        if (transitTime == null) {
            return null;
        }

        switch (transitTime.toUpperCase()) {
            case "ONE_DAY":
                return 1;
            case "TWO_DAYS":
                return 2;
            case "THREE_DAYS":
                return 3;
            case "FOUR_DAYS":
                return 4;
            case "FIVE_DAYS":
                return 5;
            case "SIX_DAYS":
                return 6;
            case "SEVEN_DAYS":
                return 7;
            case "EIGHT_DAYS":
                return 8;
            case "NINE_DAYS":
                return 9;
            case "TEN_DAYS":
                return 10;
            default:
                return null;
        }
    }
}
