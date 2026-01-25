package com.thewu.eship.controller.shipping;

import com.thewu.eship.dto.shipping.CarrierType;
import com.thewu.eship.dto.shipping.LabelDTO;
import com.thewu.eship.dto.shipping.LabelFormat;
import com.thewu.eship.dto.shipping.ShipmentDTO;
import com.thewu.eship.service.shipping.LabelGenerationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for label generation.
 */
@RestController
@RequestMapping("/api/v1/labels")
@CrossOrigin(origins = "*")
public class LabelsController {
    
    @Autowired
    private LabelGenerationService labelService;
    
    /**
     * Generate a shipping label.
     * 
     * @param shipment The shipment details
     * @return Generated label
     */
    @PostMapping
    public ResponseEntity<LabelDTO> generateShippingLabel(@Valid @RequestBody ShipmentDTO shipment) {
        try {
            // Use shipment's preferred carrier or default to UPS
            CarrierType carrier = shipment.getCarrier() != null ? shipment.getCarrier() : CarrierType.UPS;
            
            // Generate tracking number
            String trackingNumber = labelService.generateTrackingNumber(carrier);
            
            // Get label format from shipment or default to PDF
            LabelFormat format = shipment.getLabelFormat() != null ? shipment.getLabelFormat() : LabelFormat.PDF;
            
            LabelDTO label = labelService.generateLabel(shipment, trackingNumber, carrier, format);
            
            return ResponseEntity.ok(label);
        } catch (Exception e) {
            throw new RuntimeException("Error generating label: " + e.getMessage(), e);
        }
    }
}
