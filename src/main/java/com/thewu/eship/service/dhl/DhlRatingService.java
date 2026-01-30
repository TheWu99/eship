package com.thewu.eship.service.dhl;

import com.thewu.eship.config.DhlApiConfig;
import com.thewu.eship.dto.dhl.DhlRateRequest;
import com.thewu.eship.dto.dhl.DhlRateResponse;
import com.thewu.eship.dto.shipping.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DHL Rating Service - Integrates with DHL Rating API
 * Provides rate shopping for DHL Express services
 */
@Service
public class DhlRatingService {

    private static final Logger log = LoggerFactory.getLogger(DhlRatingService.class);

    @Autowired
    private DhlApiConfig dhlConfig;

    @Autowired
    private DhlOAuthService oauthService;

    @Autowired
    @Qualifier("dhlRestTemplate")
    private RestTemplate restTemplate;

    private static final DateTimeFormatter DHL_DATE_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss 'GMT'XXX");

    /**
     * Shop rates for all available DHL services
     */
    public List<RateDTO> shopRates(AddressDTO shipFrom, AddressDTO shipTo, List<PackageDTO> packages) {
        return getRates(shipFrom, shipTo, packages, null);
    }

    /**
     * Get rate for a specific DHL service
     */
    public RateDTO getSingleRate(AddressDTO shipFrom, AddressDTO shipTo, List<PackageDTO> packages,
            String serviceCode) {
        List<RateDTO> rates = getRates(shipFrom, shipTo, packages, serviceCode);
        return rates.isEmpty() ? null : rates.get(0);
    }

    /**
     * Internal method to get rates from DHL API
     */
    private List<RateDTO> getRates(AddressDTO shipFrom, AddressDTO shipTo, List<PackageDTO> packages,
            String serviceCode) {
        try {
            // Build DHL API request
            DhlRateRequest dhlRequest = buildDhlRateRequest(shipFrom, shipTo, packages, serviceCode);

            // Create HTTP headers with OAuth token
            HttpHeaders headers = oauthService.createDhlHeaders();
            HttpEntity<DhlRateRequest> request = new HttpEntity<>(dhlRequest, headers);

            // Call DHL Rating API
            String endpoint = String.format("%s/rates", dhlConfig.getBaseUrl());
            log.info("Calling DHL Rating API: {}", endpoint);

            ResponseEntity<DhlRateResponse> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    request,
                    DhlRateResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return convertDhlRatesToDTOs(response.getBody());
            } else {
                log.error("Unexpected response from DHL API: {}", response.getStatusCode());
                return Collections.emptyList();
            }

        } catch (HttpClientErrorException e) {
            log.error("DHL API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to get rates from DHL: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error calling DHL Rating API", e);
            throw new RuntimeException("Error getting rates from DHL: " + e.getMessage(), e);
        }
    }

    /**
     * Build DHL API rate request from our DTOs
     */
    private DhlRateRequest buildDhlRateRequest(AddressDTO shipFrom, AddressDTO shipTo,
            List<PackageDTO> packages, String serviceCode) {
        DhlRateRequest dhlRequest = new DhlRateRequest();

        // Set customer details
        DhlRateRequest.CustomerDetails customerDetails = new DhlRateRequest.CustomerDetails();

        // Set shipper details
        DhlRateRequest.ShipperDetails shipperDetails = new DhlRateRequest.ShipperDetails();
        shipperDetails.setPostalCode(shipFrom.getPostalCode());
        shipperDetails.setCityName(shipFrom.getCity());
        shipperDetails.setCountryCode(shipFrom.getCountry());
        shipperDetails.setProvinceCode(shipFrom.getState());
        shipperDetails.setAddressLine1(shipFrom.getStreet1());
        if (shipFrom.getStreet2() != null && !shipFrom.getStreet2().isEmpty()) {
            shipperDetails.setAddressLine2(shipFrom.getStreet2());
        }
        customerDetails.setShipperDetails(shipperDetails);

        // Set receiver details
        DhlRateRequest.ReceiverDetails receiverDetails = new DhlRateRequest.ReceiverDetails();
        receiverDetails.setPostalCode(shipTo.getPostalCode());
        receiverDetails.setCityName(shipTo.getCity());
        receiverDetails.setCountryCode(shipTo.getCountry());
        receiverDetails.setProvinceCode(shipTo.getState());
        receiverDetails.setAddressLine1(shipTo.getStreet1());
        if (shipTo.getStreet2() != null && !shipTo.getStreet2().isEmpty()) {
            receiverDetails.setAddressLine2(shipTo.getStreet2());
        }
        customerDetails.setReceiverDetails(receiverDetails);

        dhlRequest.setCustomerDetails(customerDetails);

        // Set account information
        if (dhlConfig.getAccountNumber() != null && !dhlConfig.getAccountNumber().isEmpty()) {
            DhlRateRequest.Account account = new DhlRateRequest.Account();
            account.setTypeCode("shipper");
            account.setNumber(dhlConfig.getAccountNumber());
            dhlRequest.setAccounts(Collections.singletonList(account));
        }

        // Set product code if specified
        if (serviceCode != null && !serviceCode.isEmpty()) {
            dhlRequest.setProductCode(mapServiceCodeToDhl(serviceCode));
        }

        // Set planned shipping date (next business day)
        LocalDateTime plannedShipDate = LocalDateTime.now(ZoneId.of("GMT"))
                .plusDays(1)
                .withHour(10)
                .withMinute(0)
                .withSecond(0);
        dhlRequest.setPlannedShippingDateAndTime(
                plannedShipDate.format(DHL_DATE_FORMATTER));

        // Set unit of measurement (metric or imperial)
        dhlRequest.setUnitOfMeasurement("imperial"); // Use imperial for US

        // Set customs declarable
        dhlRequest.setIsCustomsDeclarable(isInternational(shipFrom.getCountry(), shipTo.getCountry()));

        // Request estimated delivery date
        DhlRateRequest.EstimatedDeliveryDate edd = new DhlRateRequest.EstimatedDeliveryDate();
        edd.setIsRequested(true);
        edd.setTypeCode("QDDC");
        dhlRequest.setEstimatedDeliveryDate(edd);

        // Set packages
        List<DhlRateRequest.Package> dhlPackages = packages.stream()
                .map(this::convertToDhlPackage)
                .collect(Collectors.toList());
        dhlRequest.setPackages(dhlPackages);

        return dhlRequest;
    }

    /**
     * Convert our PackageDTO to DHL Package format
     */
    private DhlRateRequest.Package convertToDhlPackage(PackageDTO pkg) {
        DhlRateRequest.Package dhlPackage = new DhlRateRequest.Package();

        // Set package type (3BX = customer box)
        dhlPackage.setTypeCode("3BX");

        // Set weight (convert to pounds if needed)
        dhlPackage.setWeight(pkg.getWeight());

        // Set dimensions
        DhlRateRequest.Dimensions dimensions = new DhlRateRequest.Dimensions();
        dimensions.setLength(pkg.getLength());
        dimensions.setWidth(pkg.getWidth());
        dimensions.setHeight(pkg.getHeight());
        dhlPackage.setDimensions(dimensions);

        return dhlPackage;
    }

    /**
     * Convert DHL API response to our RateDTO list
     */
    private List<RateDTO> convertDhlRatesToDTOs(DhlRateResponse response) {
        if (response == null || response.getProducts() == null) {
            return Collections.emptyList();
        }

        List<RateDTO> rates = new ArrayList<>();

        for (DhlRateResponse.Product product : response.getProducts()) {
            try {
                RateDTO rate = new RateDTO();
                rate.setCarrier(CarrierType.DHL);
                rate.setService(product.getProductName());

                // Get total price
                if (product.getTotalPrice() != null && !product.getTotalPrice().isEmpty()) {
                    DhlRateResponse.Price price = product.getTotalPrice().get(0);
                    rate.setRate(price.getPrice());
                    rate.setCurrency(price.getPriceCurrency());
                }

                // Get delivery days
                if (product.getDeliveryCapabilities() != null) {
                    Integer transitDays = product.getDeliveryCapabilities().getTotalTransitDays();
                    rate.setDeliveryDays(transitDays);
                }

                // Set carrier rate ID
                rate.setCarrierRateId("DHL-" + product.getProductCode() + "-" +
                        UUID.randomUUID().toString().substring(0, 8));

                rates.add(rate);

            } catch (Exception e) {
                log.warn("Error converting DHL product to rate: {}", e.getMessage());
            }
        }

        log.info("Converted {} DHL rates", rates.size());
        return rates;
    }

    /**
     * Map our generic service codes to DHL product codes
     */
    private String mapServiceCodeToDhl(String serviceCode) {
        if (serviceCode == null) {
            return null;
        }

        Map<String, String> serviceMapping = new HashMap<>();
        serviceMapping.put("EXPRESS", "P");
        serviceMapping.put("EXPRESS_WORLDWIDE", "P");
        serviceMapping.put("EXPRESS_12:00", "Y");
        serviceMapping.put("EXPRESS_10:30", "X");
        serviceMapping.put("EXPRESS_ENVELOPE", "D");
        serviceMapping.put("ECONOMY", "U");
        serviceMapping.put("ECONOMY_SELECT", "W");

        return serviceMapping.getOrDefault(serviceCode.toUpperCase(), "P");
    }

    /**
     * Get service description for a service code
     */
    private String getServiceDescription(String serviceCode) {
        Map<String, String> descriptions = new HashMap<>();
        descriptions.put("P", "DHL Express Worldwide");
        descriptions.put("Y", "DHL Express 12:00");
        descriptions.put("X", "DHL Express 10:30");
        descriptions.put("D", "DHL Express Envelope");
        descriptions.put("U", "DHL Express Economy");
        descriptions.put("W", "DHL Economy Select");
        return descriptions.getOrDefault(serviceCode, "DHL Service");
    }

    /**
     * Check if shipment is international
     */
    private boolean isInternational(String fromCountry, String toCountry) {
        return !fromCountry.equalsIgnoreCase(toCountry);
    }
}
