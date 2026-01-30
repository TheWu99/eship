package com.thewu.eship.controller.shipping;

import com.thewu.eship.dto.shipping.ShipmentDTO;
import com.thewu.eship.service.shipping.RateComparisonService;
import com.thewu.eship.service.shipping.RatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for comparing shipping rates across carriers.
 */
@RestController
@RequestMapping("/api/v1/rates/compare")
@CrossOrigin(origins = "*")
public class RateComparisonController {

    @Autowired
    private RatingService ratingService;

    /**
     * Compare shipping rates from all carriers (UPS, FedEx, DHL).
     * Provides comprehensive analysis including cheapest, fastest, and best value
     * options.
     * 
     * @param shipment The shipment details
     * @return Comparison result with recommendations
     */
    @PostMapping
    public ResponseEntity<RateComparisonService.RateComparisonResult> compareAllCarriers(
            @Valid @RequestBody ShipmentDTO shipment) {
        try {
            RateComparisonService.RateComparisonResult comparison = ratingService.compareAllCarriers(shipment);
            return ResponseEntity.ok(comparison);
        } catch (Exception e) {
            throw new RuntimeException("Error comparing rates: " + e.getMessage(), e);
        }
    }

    /**
     * Compare shipping rates between UPS and FedEx only.
     * 
     * @param shipment The shipment details
     * @return Comparison result
     */
    @PostMapping("/ups-fedex")
    public ResponseEntity<RateComparisonService.RateComparisonResult> compareUpsVsFedex(
            @Valid @RequestBody ShipmentDTO shipment) {
        try {
            RateComparisonService.RateComparisonResult comparison = ratingService.compareUpsVsFedex(shipment);
            return ResponseEntity.ok(comparison);
        } catch (Exception e) {
            throw new RuntimeException("Error comparing UPS vs FedEx rates: " + e.getMessage(), e);
        }
    }
}
