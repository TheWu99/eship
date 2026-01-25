package com.thewu.eship.service.ups;

import com.thewu.eship.config.UpsApiConfig;
import com.thewu.eship.dto.shipping.*;
import com.thewu.eship.dto.ups.UpsRateRequest;
import com.thewu.eship.dto.ups.UpsRateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * UPS Rating Service - Integrates with UPS Rating API
 * Provides rate shopping and single rate calculation
 */
@Service
public class UpsRatingService {

    private static final Logger log = LoggerFactory.getLogger(UpsRatingService.class);

    @Autowired
    private UpsApiConfig upsConfig;

    @Autowired
    private UpsOAuthService oauthService;

    @Autowired
    @Qualifier("upsRestTemplate")
    private RestTemplate restTemplate;

    /**
     * Shop rates for all available UPS services
     */
    public List<RateDTO> shopRates(AddressDTO shipFrom, AddressDTO shipTo, List<PackageDTO> packages) {
        String requestOption = "Shop"; // Get rates for all services
        return getRates(shipFrom, shipTo, packages, null, requestOption);
    }

    /**
     * Get rate for a specific UPS service
     */
    public RateDTO getSingleRate(AddressDTO shipFrom, AddressDTO shipTo, List<PackageDTO> packages,
            String serviceCode) {
        String requestOption = "Rate"; // Get rate for specific service
        List<RateDTO> rates = getRates(shipFrom, shipTo, packages, serviceCode, requestOption);
        return rates.isEmpty() ? null : rates.get(0);
    }

    /**
     * Internal method to get rates from UPS API
     */
    private List<RateDTO> getRates(AddressDTO shipFrom, AddressDTO shipTo, List<PackageDTO> packages,
            String serviceCode, String requestOption) {
        try {
            // Build UPS API request
            UpsRateRequest upsRequest = buildUpsRateRequest(shipFrom, shipTo, packages, serviceCode, requestOption);

            // Create HTTP headers with OAuth token
            HttpHeaders headers = oauthService.createUpsHeaders(UUID.randomUUID().toString());
            HttpEntity<UpsRateRequest> request = new HttpEntity<>(upsRequest, headers);

            // Call UPS Rating API
            String endpoint = String.format("%s/rating/v2409/%s", upsConfig.getBaseUrl(), requestOption);
            log.info("Calling UPS Rating API: {} with option: {}", endpoint, requestOption);

            ResponseEntity<UpsRateResponse> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    request,
                    UpsRateResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return convertUpsRatesToDTOs(response.getBody());
            } else {
                log.error("Unexpected response from UPS API: {}", response.getStatusCode());
                return Collections.emptyList();
            }

        } catch (HttpClientErrorException e) {
            log.error("UPS API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to get rates from UPS: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error calling UPS Rating API", e);
            throw new RuntimeException("Error getting rates from UPS: " + e.getMessage(), e);
        }
    }

    /**
     * Build UPS API rate request from our DTOs
     */
    private UpsRateRequest buildUpsRateRequest(AddressDTO shipFrom, AddressDTO shipTo,
            List<PackageDTO> packages, String serviceCode, String requestOption) {
        UpsRateRequest upsRequest = new UpsRateRequest();
        UpsRateRequest.RateRequestContainer container = new UpsRateRequest.RateRequestContainer();

        // Build Request
        UpsRateRequest.Request request = new UpsRateRequest.Request();
        request.setRequestOption(requestOption);

        UpsRateRequest.TransactionReference tranRef = new UpsRateRequest.TransactionReference();
        tranRef.setCustomerContext("eship rating request");
        request.setTransactionReference(tranRef);

        container.setRequest(request);

        // Build Shipment
        UpsRateRequest.Shipment shipment = new UpsRateRequest.Shipment();

        // Set Shipper
        UpsRateRequest.Shipper shipper = new UpsRateRequest.Shipper();
        shipper.setName(shipFrom.getName() != null ? shipFrom.getName() : "Shipper");
        shipper.setShipperNumber(upsConfig.getAccountNumber());
        shipper.setAddress(convertToUpsAddress(shipFrom));
        shipment.setShipper(shipper);

        // Set ShipFrom
        shipment.setShipFrom(convertToUpsAddress(shipFrom));

        // Set ShipTo
        shipment.setShipTo(convertToUpsAddress(shipTo));

        // Set Service (if specific service requested)
        if (serviceCode != null && !serviceCode.isEmpty()) {
            UpsRateRequest.Service service = new UpsRateRequest.Service();
            service.setCode(mapServiceCodeToUps(serviceCode));
            service.setDescription(getServiceDescription(serviceCode));
            shipment.setService(service);
        }

        // Set Packages
        List<UpsRateRequest.Package> upsPackages = packages.stream()
                .map(this::convertToUpsPackage)
                .collect(Collectors.toList());
        shipment.setPackages(upsPackages);

        container.setShipment(shipment);
        upsRequest.setRateRequest(container);

        return upsRequest;
    }

    /**
     * Convert our AddressDTO to UPS Address format
     */
    private UpsRateRequest.Address convertToUpsAddress(AddressDTO address) {
        UpsRateRequest.Address upsAddress = new UpsRateRequest.Address();
        upsAddress.setName(address.getName());

        List<String> addressLines = new ArrayList<>();
        if (address.getStreet1() != null)
            addressLines.add(address.getStreet1());
        if (address.getStreet2() != null)
            addressLines.add(address.getStreet2());
        upsAddress.setAddressLine(addressLines);

        upsAddress.setCity(address.getCity());
        upsAddress.setStateProvinceCode(address.getState());
        upsAddress.setPostalCode(address.getZipCode());
        upsAddress.setCountryCode(address.getCountry() != null ? address.getCountry() : "US");

        return upsAddress;
    }

    /**
     * Convert our PackageDTO to UPS Package format
     */
    private UpsRateRequest.Package convertToUpsPackage(PackageDTO pkg) {
        UpsRateRequest.Package upsPackage = new UpsRateRequest.Package();

        // Set packaging type
        UpsRateRequest.PackagingType packagingType = new UpsRateRequest.PackagingType();
        packagingType.setCode("02"); // Customer Supplied Package
        packagingType.setDescription("Package");
        upsPackage.setPackagingType(packagingType);

        // Set dimensions if provided
        if (pkg.getLength() != null && pkg.getWidth() != null && pkg.getHeight() != null) {
            UpsRateRequest.Dimensions dimensions = new UpsRateRequest.Dimensions();

            UpsRateRequest.UnitOfMeasurement dimUnit = new UpsRateRequest.UnitOfMeasurement();
            dimUnit.setCode("IN"); // Inches
            dimUnit.setDescription("Inches");
            dimensions.setUnitOfMeasurement(dimUnit);

            dimensions.setLength(String.valueOf(pkg.getLength()));
            dimensions.setWidth(String.valueOf(pkg.getWidth()));
            dimensions.setHeight(String.valueOf(pkg.getHeight()));

            upsPackage.setDimensions(dimensions);
        }

        // Set weight
        UpsRateRequest.Weight weight = new UpsRateRequest.Weight();
        UpsRateRequest.UnitOfMeasurement weightUnit = new UpsRateRequest.UnitOfMeasurement();
        weightUnit.setCode("LBS"); // Pounds
        weightUnit.setDescription("Pounds");
        weight.setUnitOfMeasurement(weightUnit);
        weight.setWeight(String.valueOf(pkg.getWeight()));

        upsPackage.setPackageWeight(weight);

        return upsPackage;
    }

    /**
     * Convert UPS API response to our RateDTO list
     */
    private List<RateDTO> convertUpsRatesToDTOs(UpsRateResponse upsResponse) {
        List<RateDTO> rates = new ArrayList<>();

        if (upsResponse.getRateResponse() != null &&
                upsResponse.getRateResponse().getRatedShipment() != null) {

            for (UpsRateResponse.RatedShipment ratedShipment : upsResponse.getRateResponse().getRatedShipment()) {
                RateDTO rate = new RateDTO();
                rate.setCarrier(CarrierType.UPS);

                // Set service
                if (ratedShipment.getService() != null) {
                    rate.setService(mapUpsServiceCodeToInternal(ratedShipment.getService().getCode()));
                    rate.setServiceDescription(ratedShipment.getService().getDescription());
                }

                // Set pricing
                if (ratedShipment.getTotalCharges() != null) {
                    rate.setTotalCost(new BigDecimal(ratedShipment.getTotalCharges().getMonetaryValue()));
                    rate.setCurrency(ratedShipment.getTotalCharges().getCurrencyCode());
                }

                if (ratedShipment.getBaseServiceCharge() != null) {
                    rate.setBaseRate(new BigDecimal(ratedShipment.getBaseServiceCharge().getMonetaryValue()));
                }

                // Set delivery time
                if (ratedShipment.getTimeInTransit() != null &&
                        ratedShipment.getTimeInTransit().getServiceSummary() != null &&
                        ratedShipment.getTimeInTransit().getServiceSummary().getEstimatedArrival() != null) {

                    String transitDays = ratedShipment.getTimeInTransit().getServiceSummary()
                            .getEstimatedArrival().getBusinessDaysInTransit();
                    if (transitDays != null) {
                        rate.setDeliveryDays(Integer.parseInt(transitDays));
                    }

                    UpsRateResponse.Arrival arrival = ratedShipment.getTimeInTransit().getServiceSummary()
                            .getEstimatedArrival().getArrival();
                    if (arrival != null && arrival.getDate() != null) {
                        rate.setEstimatedDeliveryDate(arrival.getDate());
                    }
                }

                // Set guaranteed delivery if available
                if (ratedShipment.getGuaranteedDelivery() != null) {
                    rate.setGuaranteedDelivery(true);
                }

                rates.add(rate);
            }
        }

        return rates;
    }

    /**
     * Map our internal service codes to UPS service codes
     */
    private String mapServiceCodeToUps(String serviceCode) {
        // Common UPS service codes:
        // 01 = Next Day Air, 02 = 2nd Day Air, 03 = Ground
        // 12 = 3 Day Select, 13 = Next Day Air Saver, 14 = Next Day Air Early
        // 59 = 2nd Day Air A.M., 65 = UPS Saver (Domestic)
        return switch (serviceCode.toUpperCase()) {
            case "GROUND" -> "03";
            case "EXPRESS" -> "01";
            case "2DAY" -> "02";
            case "3DAY" -> "12";
            case "NEXT_DAY" -> "01";
            case "NEXT_DAY_SAVER" -> "13";
            case "NEXT_DAY_EARLY" -> "14";
            case "2DAY_AM" -> "59";
            default -> "03"; // Default to Ground
        };
    }

    /**
     * Map UPS service codes to our internal codes
     */
    private String mapUpsServiceCodeToInternal(String upsCode) {
        return switch (upsCode) {
            case "01", "14" -> "NEXT_DAY";
            case "02", "59" -> "2DAY";
            case "03" -> "GROUND";
            case "12" -> "3DAY";
            case "13" -> "NEXT_DAY_SAVER";
            default -> "GROUND";
        };
    }

    /**
     * Get service description
     */
    private String getServiceDescription(String serviceCode) {
        return switch (serviceCode.toUpperCase()) {
            case "GROUND" -> "UPS Ground";
            case "EXPRESS", "NEXT_DAY" -> "UPS Next Day Air";
            case "2DAY" -> "UPS 2nd Day Air";
            case "3DAY" -> "UPS 3 Day Select";
            case "NEXT_DAY_SAVER" -> "UPS Next Day Air Saver";
            case "NEXT_DAY_EARLY" -> "UPS Next Day Air Early";
            case "2DAY_AM" -> "UPS 2nd Day Air A.M.";
            default -> "UPS Ground";
        };
    }
}
