package com.thewu.eship.controller.shipping;

import com.thewu.eship.dto.shipping.ShipmentDTO;
import com.thewu.eship.service.shipping.CustomsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for customs and international shipping.
 */
@RestController
@RequestMapping("/api/v1/customs")
@CrossOrigin(origins = "*")
public class CustomsController {

    @Autowired
    private CustomsService customsService;

    /**
     * Generate customs form for international shipment.
     * 
     * @param shipment The shipment with customs information
     * @return Base64 encoded customs form
     */
    @PostMapping("/form")
    public ResponseEntity<Map<String, String>> generateCustomsForm(@Valid @RequestBody ShipmentDTO shipment) {
        try {
            if (!customsService.requiresCustoms(shipment)) {
                throw new IllegalArgumentException("Customs documentation not required for domestic shipments");
            }

            String customsForm = customsService.generateCustomsForm(shipment);

            Map<String, String> response = new HashMap<>();
            response.put("customs_form", customsForm);
            response.put("format", "base64");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error generating customs form: " + e.getMessage(), e);
        }
    }

    /**
     * Check if shipment requires customs documentation.
     * 
     * @param shipment The shipment to check
     * @return Whether customs is required
     */
    @PostMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkCustomsRequirement(@Valid @RequestBody ShipmentDTO shipment) {
        try {
            boolean required = customsService.requiresCustoms(shipment);

            Map<String, Boolean> response = new HashMap<>();
            response.put("customs_required", required);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error checking customs requirement: " + e.getMessage(), e);
        }
    }
}
