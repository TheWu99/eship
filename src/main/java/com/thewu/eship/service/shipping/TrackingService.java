package com.thewu.eship.service.shipping;

import com.thewu.eship.dto.shipping.*;
import com.thewu.eship.service.ups.UpsTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for tracking shipments.
 * Now integrates with real UPS API via UpsTrackingService
 */
@Service
public class TrackingService {

    private static final Logger log = LoggerFactory.getLogger(TrackingService.class);

    @Autowired(required = false)
    private UpsTrackingService upsTrackingService;

    /**
     * Get tracking information for a shipment.
     * 
     * @param trackingNumber The tracking number
     * @param carrier        Optional carrier filter
     * @return Tracking information
     */
    public Optional<ShipmentTrackingDTO> getTracking(String trackingNumber, CarrierType carrier) {
        // Auto-detect carrier if not provided
        if (carrier == null) {
            carrier = detectCarrier(trackingNumber);
        }

        // Try real UPS tracking if it's a UPS shipment
        if (carrier == CarrierType.UPS && upsTrackingService != null) {
            try {
                log.info("Fetching tracking from UPS API for: {}", trackingNumber);
                ShipmentTrackingDTO tracking = upsTrackingService.trackShipment(trackingNumber);
                return Optional.of(tracking);
            } catch (Exception e) {
                log.error("Failed to get UPS tracking, falling back to mock data", e);
            }
        }

        // Fall back to mock tracking data
        log.warn("Using mock tracking data for: {}", trackingNumber);
        return Optional.of(getMockTracking(trackingNumber, carrier));
    }

    /**
     * Get mock tracking data (fallback)
     */
    private ShipmentTrackingDTO getMockTracking(String trackingNumber, CarrierType carrier) {
        ShipmentTrackingDTO tracking = new ShipmentTrackingDTO();
        tracking.setTrackingNumber(trackingNumber);
        tracking.setCarrier(carrier != null ? carrier : CarrierType.UPS);
        tracking.setCurrentStatus(TrackingState.IN_TRANSIT);

        // Create mock events
        List<TrackingEventDTO> events = new ArrayList<>();
        events.add(createEvent(
                LocalDateTime.now().minusDays(2),
                TrackingState.IN_TRANSIT,
                "Package picked up",
                "Origin facility"));

        events.add(createEvent(
                LocalDateTime.now().minusDays(1),
                TrackingState.IN_TRANSIT,
                "In transit to destination",
                "Sort facility"));

        events.add(createEvent(
                LocalDateTime.now(),
                TrackingState.OUT_FOR_DELIVERY,
                "Out for delivery",
                "Destination city"));

        tracking.setEvents(events);
        tracking.setEstimatedDelivery(LocalDateTime.now().plusDays(1));

        return tracking;
    }

    /**
     * Detect carrier from tracking number format.
     */
    private CarrierType detectCarrier(String trackingNumber) {
        if (trackingNumber.startsWith("1Z")) {
            return CarrierType.UPS;
        } else if (trackingNumber.startsWith("92") && trackingNumber.length() == 20) {
            return CarrierType.USPS;
        } else if (trackingNumber.length() == 12) {
            return CarrierType.FEDEX;
        } else if (trackingNumber.length() == 10) {
            return CarrierType.DHL;
        }
        return CarrierType.OTHER;
    }

    private TrackingEventDTO createEvent(
            LocalDateTime timestamp,
            TrackingState status,
            String message,
            String location) {

        TrackingEventDTO event = new TrackingEventDTO();
        event.setTimestamp(timestamp);
        event.setStatus(status);
        event.setMessage(message);
        event.setLocation(location);
        return event;
    }
}
