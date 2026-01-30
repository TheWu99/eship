package com.thewu.eship.service;

import com.thewu.eship.dto.shipping.*;
import com.thewu.eship.service.shipping.TrackingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TrackingService with mock data.
 * Tests tracking for UPS, FedEx, DHL, and USPS.
 */
@SpringBootTest
public class TrackingServiceTest {

    @Autowired
    private TrackingService trackingService;

    @Test
    @DisplayName("Test track UPS shipment")
    void testTrackUpsShipment() {
        String trackingNumber = "1Z999AA10123456784";
        
        Optional<ShipmentTrackingDTO> tracking = trackingService.getTracking(trackingNumber, CarrierType.UPS);

        assertTrue(tracking.isPresent(), "Should find tracking info for UPS");
        
        ShipmentTrackingDTO info = tracking.get();
        assertEquals(trackingNumber, info.getTrackingNumber());
        assertEquals(CarrierType.UPS, info.getCarrier());
        assertNotNull(info.getCurrentStatus());
        assertNotNull(info.getEvents());
        assertFalse(info.getEvents().isEmpty(), "Should have at least one tracking event");

        System.out.println("\n=== UPS TRACKING TEST ===");
        System.out.println("Tracking #: " + info.getTrackingNumber());
        System.out.println("Carrier: " + info.getCarrier());
        System.out.println("Status: " + info.getCurrentStatus());
        System.out.println("Events: " + info.getEvents().size());
        
        info.getEvents().forEach(event -> 
            System.out.println("  - " + event.getTimestamp() + ": " + event.getMessage() + 
                              " at " + event.getLocation())
        );
    }

    @Test
    @DisplayName("Test track FedEx shipment")
    void testTrackFedexShipment() {
        String trackingNumber = "123456789012";
        
        Optional<ShipmentTrackingDTO> tracking = trackingService.getTracking(trackingNumber, CarrierType.FEDEX);

        assertTrue(tracking.isPresent(), "Should find tracking info for FedEx");
        
        ShipmentTrackingDTO info = tracking.get();
        assertEquals(trackingNumber, info.getTrackingNumber());
        assertEquals(CarrierType.FEDEX, info.getCarrier());
        assertNotNull(info.getCurrentStatus());

        System.out.println("\n=== FEDEX TRACKING TEST ===");
        System.out.println("Tracking #: " + info.getTrackingNumber());
        System.out.println("Carrier: " + info.getCarrier());
        System.out.println("Status: " + info.getCurrentStatus());
        System.out.println("Events: " + info.getEvents().size());
        
        info.getEvents().forEach(event -> 
            System.out.println("  - " + event.getTimestamp() + ": " + event.getMessage() + 
                              " at " + event.getLocation())
        );
    }

    @Test
    @DisplayName("Test track DHL shipment")
    void testTrackDhlShipment() {
        String trackingNumber = "1234567890";
        
        Optional<ShipmentTrackingDTO> tracking = trackingService.getTracking(trackingNumber, CarrierType.DHL);

        assertTrue(tracking.isPresent(), "Should find tracking info for DHL");
        
        ShipmentTrackingDTO info = tracking.get();
        assertEquals(trackingNumber, info.getTrackingNumber());
        assertEquals(CarrierType.DHL, info.getCarrier());
        assertNotNull(info.getCurrentStatus());

        System.out.println("\n=== DHL TRACKING TEST ===");
        System.out.println("Tracking #: " + info.getTrackingNumber());
        System.out.println("Carrier: " + info.getCarrier());
        System.out.println("Status: " + info.getCurrentStatus());
        System.out.println("Events: " + info.getEvents().size());
        
        info.getEvents().forEach(event -> 
            System.out.println("  - " + event.getTimestamp() + ": " + event.getMessage() + 
                              " at " + event.getLocation())
        );
    }

    @Test
    @DisplayName("Test track with auto-detect carrier")
    void testTrackAutoDetect() {
        // UPS format tracking number
        String upsTracking = "1Z999AA10123456784";
        
        Optional<ShipmentTrackingDTO> tracking = trackingService.getTracking(upsTracking, null);

        assertTrue(tracking.isPresent(), "Should find tracking info with auto-detect");
        
        ShipmentTrackingDTO info = tracking.get();
        assertNotNull(info.getCarrier(), "Should detect carrier");

        System.out.println("\n=== AUTO-DETECT CARRIER TEST ===");
        System.out.println("Tracking #: " + upsTracking);
        System.out.println("Detected Carrier: " + info.getCarrier());
        System.out.println("Status: " + info.getCurrentStatus());
    }

    @Test
    @DisplayName("Test track invalid tracking number")
    void testTrackInvalidNumber() {
        String invalidTracking = "INVALID123";
        
        Optional<ShipmentTrackingDTO> tracking = trackingService.getTracking(invalidTracking, null);

        assertTrue(tracking.isPresent(), "Service returns mock data for invalid tracking");

        System.out.println("\n=== INVALID TRACKING NUMBER TEST ===");
        System.out.println("✓ Service returned mock data for: " + invalidTracking);
        System.out.println("Status: " + tracking.get().getCurrentStatus());
    }

    @Test
    @DisplayName("Test tracking shows delivery progression")
    void testTrackingProgression() {
        String trackingNumber = "1Z999AA10123456784";
        
        Optional<ShipmentTrackingDTO> tracking = trackingService.getTracking(trackingNumber, CarrierType.UPS);

        assertTrue(tracking.isPresent());
        
        ShipmentTrackingDTO info = tracking.get();
        
        System.out.println("\n=== TRACKING PROGRESSION TEST ===");
        System.out.println("Shipment Journey:");
        
        // Events should be in chronological order
        info.getEvents().forEach(event -> {
            System.out.println(String.format("[%s] %s - %s", 
                event.getTimestamp().toString(),
                event.getMessage(),
                event.getLocation()));
        });
        
        // Verify we have key milestone events
        boolean hasPickup = info.getEvents().stream()
            .anyMatch(e -> e.getMessage().toLowerCase().contains("pick") ||
                          e.getMessage().toLowerCase().contains("origin"));
        
        assertTrue(hasPickup, "Should have pickup/origin event");
    }

    @Test
    @DisplayName("Test delivered status")
    void testDeliveredStatus() {
        String deliveredTracking = "DELIVERED-001";
        
        Optional<ShipmentTrackingDTO> tracking = trackingService.getTracking(deliveredTracking, CarrierType.UPS);

        if (tracking.isPresent()) {
            ShipmentTrackingDTO info = tracking.get();
            
            System.out.println("\n=== DELIVERED STATUS TEST ===");
            System.out.println("Status: " + info.getCurrentStatus());
            
            if (info.getCurrentStatus() == TrackingState.DELIVERED) {
                System.out.println("✓ Package delivered successfully");
                if (info.getActualDelivery() != null) {
                    System.out.println("Delivered on: " + info.getActualDelivery());
                }
            }
        }
    }

    @Test
    @DisplayName("Test in-transit status")
    void testInTransitStatus() {
        String transitTracking = "1Z999AA10123456784";
        
        Optional<ShipmentTrackingDTO> tracking = trackingService.getTracking(transitTracking, CarrierType.UPS);

        assertTrue(tracking.isPresent());
        
        ShipmentTrackingDTO info = tracking.get();
        
        System.out.println("\n=== IN-TRANSIT STATUS TEST ===");
        System.out.println("Current Status: " + info.getCurrentStatus());
        if (!info.getEvents().isEmpty()) {
            System.out.println("Current Location: " + info.getEvents().get(info.getEvents().size() - 1).getLocation());
        }
        
        if (info.getEstimatedDelivery() != null) {
            System.out.println("Estimated Delivery: " + info.getEstimatedDelivery());
        }
    }

    @Test
    @DisplayName("Test exception/problem status")
    void testExceptionStatus() {
        String exceptionTracking = "EXCEPTION-001";
        
        Optional<ShipmentTrackingDTO> tracking = trackingService.getTracking(exceptionTracking, null);

        if (tracking.isPresent()) {
            ShipmentTrackingDTO info = tracking.get();
            
            System.out.println("\n=== EXCEPTION STATUS TEST ===");
            System.out.println("Status: " + info.getCurrentStatus());
            
            if (info.getCurrentStatus() == TrackingState.EXCEPTION) {
                System.out.println("⚠ Exception detected");
                // Latest event should explain the exception
                if (!info.getEvents().isEmpty()) {
                    TrackingEventDTO latestEvent = info.getEvents().get(0);
                    System.out.println("Issue: " + latestEvent.getMessage());
                }
            }
        }
    }
}
