package com.thewu.eship.controller;

import com.thewu.eship.dto.shipping.*;
import com.thewu.eship.service.shipping.RateComparisonService;
import com.thewu.eship.service.shipping.RatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for shipping rate operations and carrier comparison.
 */
@RestController
@RequestMapping("/api/v1/rates")
public class RateController {

    private static final Logger log = LoggerFactory.getLogger(RateController.class);

    @Autowired
    private RatingService ratingService;

    @Autowired
    private RateComparisonService comparisonService;

    /**
     * Get all available rates for a shipment
     * 
     * POST /api/v1/rates
     */
    @PostMapping
    public ResponseEntity<List<RateDTO>> getRates(@RequestBody ShipmentDTO shipment) {
        log.info("Getting rates for shipment from {} to {}",
                shipment.getFromAddress().getCity(),
                shipment.getToAddress().getCity());

        List<RateDTO> rates = ratingService.getRates(shipment);
        return ResponseEntity.ok(rates);
    }

    /**
     * Get cheapest rate across all carriers
     * 
     * POST /api/v1/rates/cheapest
     */
    @PostMapping("/cheapest")
    public ResponseEntity<RateDTO> getCheapestRate(@RequestBody ShipmentDTO shipment) {
        log.info("Finding cheapest rate for shipment");

        List<RateDTO> rates = ratingService.getRates(shipment);
        RateDTO cheapest = comparisonService.findCheapestRate(rates);

        if (cheapest == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(cheapest);
    }

    /**
     * Get fastest delivery option across all carriers
     * 
     * POST /api/v1/rates/fastest
     */
    @PostMapping("/fastest")
    public ResponseEntity<RateDTO> getFastestRate(@RequestBody ShipmentDTO shipment) {
        log.info("Finding fastest rate for shipment");

        List<RateDTO> rates = ratingService.getRates(shipment);
        RateDTO fastest = comparisonService.findFastestRate(rates);

        if (fastest == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(fastest);
    }

    /**
     * Get best value (balance of cost and speed)
     * 
     * POST /api/v1/rates/best-value
     */
    @PostMapping("/best-value")
    public ResponseEntity<RateDTO> getBestValue(@RequestBody ShipmentDTO shipment) {
        log.info("Finding best value rate for shipment");

        List<RateDTO> rates = ratingService.getRates(shipment);
        RateDTO bestValue = comparisonService.findBestValue(rates);

        if (bestValue == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(bestValue);
    }

    /**
     * Get rates grouped by carrier
     * 
     * POST /api/v1/rates/by-carrier
     */
    @PostMapping("/by-carrier")
    public ResponseEntity<Map<CarrierType, List<RateDTO>>> getRatesByCarrier(
            @RequestBody ShipmentDTO shipment) {
        log.info("Getting rates grouped by carrier");

        List<RateDTO> rates = ratingService.getRates(shipment);
        Map<CarrierType, List<RateDTO>> groupedRates = comparisonService.groupByCarrier(rates);

        return ResponseEntity.ok(groupedRates);
    }
}
