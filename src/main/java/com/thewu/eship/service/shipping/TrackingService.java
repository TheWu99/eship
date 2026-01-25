package com.thewu.eship.service.shipping;

import com.thewu.eship.dto.shipping.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for tracking shipments.
 */
@Service
public class TrackingService {
    
    /**
     * Get tracking information for a shipment.
     * 
     * @param trackingNumber The tracking number
     * @param carrier Optional carrier filter
     * @return Tracking information
     */
    public Optional<ShipmentTrackingDTO> getTracking(String trackingNumber, CarrierType carrier) {
        // Auto-detect carrier if not provided
        if (carrier == null) {
            carrier = detectCarrier(trackingNumber);
        }
        
        // TODO: In production, integrate with real carrier tracking APIs
        // For now, returning mock tracking data
        
        ShipmentTrackingDTO tracking = new ShipmentTrackingDTO();
        tracking.setTrackingNumber(trackingNumber);
        tracking.setCarrier(carrier);
        tracking.setCurrentStatus(TrackingState.IN_TRANSIT);
        
        List<TrackingEventDTO> events = new ArrayList<>();
        
        // Mock events
        events.add(createEvent(
            LocalDateTime.now().minusDays(3),
            TrackingState.PRE_TRANSIT,
            "Shipping label created",
            "Origin facility"
        ));
        
        events.add(createEvent(
            LocalDateTime.now().minusDays(2),
            TrackingState.IN_TRANSIT,
            "Package picked up",
            "Origin facility"
        ));
        
        events.add(createEvent(
            LocalDateTime.now().minusDays(1),
            TrackingState.IN_TRANSIT,
            "In transit to destination",
            "Sort facility"
        ));
        
        events.add(createEvent(
            LocalDateTime.now(),
            TrackingState.OUT_FOR_DELIVERY,
            "Out for delivery",
            "Destination city"
        ));
        
        tracking.setEvents(events);
        tracking.setEstimatedDelivery(LocalDateTime.now().plusDays(1));
        
        return Optional.of(tracking);
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
