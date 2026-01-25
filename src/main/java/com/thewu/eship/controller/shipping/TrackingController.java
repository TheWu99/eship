package com.thewu.eship.controller.shipping;

import com.thewu.eship.dto.shipping.CarrierType;
import com.thewu.eship.dto.shipping.ShipmentTrackingDTO;
import com.thewu.eship.service.shipping.TrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for shipment tracking.
 */
@RestController
@RequestMapping("/api/v1/tracking")
@CrossOrigin(origins = "*")
public class TrackingController {
    
    @Autowired
    private TrackingService trackingService;
    
    /**
     * Get tracking information for a shipment.
     * 
     * @param trackingNumber The tracking number
     * @param carrier Optional carrier filter
     * @return Tracking information
     */
    @GetMapping("/{trackingNumber}")
    public ResponseEntity<ShipmentTrackingDTO> getTrackingInfo(
            @PathVariable String trackingNumber,
            @RequestParam(required = false) CarrierType carrier) {
        
        try {
            return trackingService.getTracking(trackingNumber, carrier)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving tracking: " + e.getMessage(), e);
        }
    }
}
