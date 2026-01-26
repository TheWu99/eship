package com.thewu.eship.service.ups;

import com.thewu.eship.config.UpsApiConfig;
import com.thewu.eship.dto.shipping.ShipmentTrackingDTO;
import com.thewu.eship.dto.shipping.TrackingEventDTO;
import com.thewu.eship.dto.shipping.TrackingState;
import com.thewu.eship.dto.ups.UpsTrackingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * UPS Tracking Service - Integrates with UPS Tracking API
 * Provides shipment tracking and status updates
 */
@Service
public class UpsTrackingService {

    private static final Logger log = LoggerFactory.getLogger(UpsTrackingService.class);

    @Autowired
    private UpsApiConfig upsConfig;

    @Autowired
    private UpsOAuthService oauthService;

    @Autowired
    @Qualifier("upsRestTemplate")
    private RestTemplate restTemplate;

    /**
     * Track a shipment by tracking number
     */
    public ShipmentTrackingDTO trackShipment(String trackingNumber) {
        try {
            // Create HTTP headers with OAuth token
            HttpHeaders headers = oauthService.createUpsHeaders(UUID.randomUUID().toString());
            HttpEntity<Void> request = new HttpEntity<>(headers);

            // Call UPS Tracking API
            String endpoint = String.format("%s/track/v1/details/%s?locale=en_US",
                    upsConfig.getBaseUrl(), trackingNumber);
            log.info("Calling UPS Tracking API: {}", endpoint);

            ResponseEntity<UpsTrackingResponse> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.GET,
                    request,
                    UpsTrackingResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return convertUpsTrackingToDTO(trackingNumber, response.getBody());
            } else {
                log.error("Unexpected response from UPS Tracking API: {}", response.getStatusCode());
                return createErrorTracking(trackingNumber, "Unable to retrieve tracking information");
            }

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("Tracking number not found: {}", trackingNumber);
                return createErrorTracking(trackingNumber, "Tracking number not found");
            }
            log.error("UPS Tracking API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to track shipment with UPS: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error calling UPS Tracking API", e);
            throw new RuntimeException("Error tracking shipment with UPS: " + e.getMessage(), e);
        }
    }

    /**
     * Convert UPS tracking response to our DTO
     */
    private ShipmentTrackingDTO convertUpsTrackingToDTO(String trackingNumber, UpsTrackingResponse upsResponse) {
        ShipmentTrackingDTO tracking = new ShipmentTrackingDTO();
        tracking.setTrackingNumber(trackingNumber);
        tracking.setCarrier(CarrierType.UPS);

        if (upsResponse.getTrackResponse() != null &&
                upsResponse.getTrackResponse().getShipment() != null &&
                !upsResponse.getTrackResponse().getShipment().isEmpty()) {

            UpsTrackingResponse.Shipment shipment = upsResponse.getTrackResponse().getShipment().get(0);

            if (shipment.getPackages() != null && !shipment.getPackages().isEmpty()) {
                UpsTrackingResponse.Package pkg = shipment.getPackages().get(0);

                // Set current status
                if (pkg.getCurrentStatus() != null) {
                    tracking.setCurrentStatus(mapUpsStatusToState(pkg.getCurrentStatus().getCode()));
                // Set delivery information
                if (pkg.getDeliveryDate() != null && !pkg.getDeliveryDate().isEmpty()) {
                    UpsTrackingResponse.DeliveryDate deliveryDate = pkg.getDeliveryDate().get(0);
                    try {
                        String dateStr = deliveryDate.getDate();
                        if (dateStr != null && dateStr.length() >= 8) {
                            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd");
                            java.time.LocalDate date = java.time.LocalDate.parse(dateStr, formatter);
                            tracking.setEstimatedDelivery(date.atStartOfDay());
                        }
                    } catch (Exception e) {
                        log.warn("Failed to parse delivery date: {}", deliveryDate.getDate());
                    }
                List<TrackingEventDTO> events = new ArrayList<>();
                if (pkg.getActivity() != null) {
                    for (UpsTrackingResponse.Activity activity : pkg.getActivity()) {
                        TrackingEventDTO event = new TrackingEventDTO();

                        if (activity.getStatus() != null) {
                            event.setStatus(mapUpsStatusToState(activity.getStatus().getCode()));
                            event.setMessage(activity.getStatus().getDescription());
                        if (activity.getDate() != null && activity.getTime() != null) {
                            try {
                                String dateTimeStr = activity.getDate() + " " + activity.getTime();
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
                                LocalDateTime timestamp = LocalDateTime.parse(dateTimeStr, formatter);
                                event.setTimestamp(timestamp);
                            } catch (Exception e) {
                                log.warn("Failed to parse activity date/time: {} {}", activity.getDate(),
                                        activity.getTime());
                            }
                        }

                        // Set location
                        if (activity.getLocation() != null && activity.getLocation().getAddress() != null) {
                            UpsTrackingResponse.Address addr = activity.getLocation().getAddress();
                            String location = String.format("%s, %s %s, %s",
                                    addr.getCity() != null ? addr.getCity() : "",
                                    addr.getStateProvince() != null ? addr.getStateProvince() : "",
                                    addr.getPostalCode() != null ? addr.getPostalCode() : "",
                                    addr.getCountry() != null ? addr.getCountry() : "");
                            event.setLocation(location.trim());
                        }

                        events.add(event);
                    }
                }
                tracking.setEvents(events);
            }
        }
        
        return tracking;
    }
    
    /**
     * Map UPS status codes to our internal TrackingState enum
     */
    private TrackingState mapUpsStatusToState(String upsStatusCode) {
        if (upsStatusCode == null)
            return TrackingState.UNKNOWN;

        // UPS status codes mapping
        return switch (upsStatusCode.toUpperCase()) {
            case "MP" -> TrackingState.MANIFEST; // Manifest Pickup
            case "I" -> TrackingState.IN_TRANSIT; // In Transit
            case "X" -> TrackingState.EXCEPTION; // Exception
            case "D" -> TrackingState.DELIVERED; // Delivered
            case "P" -> TrackingState.IN_TRANSIT; // Pickup
            case "M" -> TrackingState.MANIFEST; // Billing Information Received
            case "RS" -> TrackingState.RETURNED; // Returned to Sender
            default -> TrackingState.UNKNOWN;
        };
    }

    /**
     * Create error tracking response
     */
    private ShipmentTrackingDTO createErrorTracking(String trackingNumber, String errorMessage) {
        ShipmentTrackingDTO tracking = new ShipmentTrackingDTO();
        tracking.setTrackingNumber(trackingNumber);
        tracking.setCarrier(CarrierType.UPS);
        tracking.setCurrentStatus(TrackingState.UNKNOWN);
        tracking.setEvents(new ArrayList<>());
        return tracking;
    }
}
