package com.thewu.eship.controller.shipping;

import com.thewu.eship.dto.shipping.RateDTO;
import com.thewu.eship.dto.shipping.ShipmentDTO;
import com.thewu.eship.service.shipping.RatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for shipping rates.
 */
@RestController
@RequestMapping("/api/v1/rates")
@CrossOrigin(origins = "*")
public class RatesController {
    
    @Autowired
    private RatingService ratingService;
    
    /**
     * Get shipping rates from all carriers.
     * 
     * @param shipment The shipment details
     * @return List of rates sorted by price
     */
    @PostMapping
    public ResponseEntity<List<RateDTO>> getShippingRates(@Valid @RequestBody ShipmentDTO shipment) {
        try {
            List<RateDTO> rates = ratingService.getRates(shipment);
            return ResponseEntity.ok(rates);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving rates: " + e.getMessage(), e);
        }
    }
}
