package com.thewu.eship.service.shipping;

import com.thewu.eship.dto.shipping.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for retrieving shipping rates from carriers.
 */
@Service
public class RatingService {

    /**
     * Get shipping rates from all carriers.
     * 
     * @param shipment The shipment details
     * @return List of rates sorted by price
     */
    public List<RateDTO> getRates(ShipmentDTO shipment) {
        List<RateDTO> rates = new ArrayList<>();

        // TODO: In production, integrate with real carrier APIs
        // For now, returning mock rates

        // UPS Ground
        rates.add(new RateDTO(
                CarrierType.UPS,
                "Ground",
                calculateMockRate(shipment, 1.5),
                "USD",
                5,
                "UPS-GROUND-" + UUID.randomUUID().toString().substring(0, 8)));

        // UPS 2nd Day Air
        rates.add(new RateDTO(
                CarrierType.UPS,
                "2nd Day Air",
                calculateMockRate(shipment, 2.8),
                "USD",
                2,
                "UPS-2DA-" + UUID.randomUUID().toString().substring(0, 8)));

        // FedEx Ground
        rates.add(new RateDTO(
                CarrierType.FEDEX,
                "Ground",
                calculateMockRate(shipment, 1.6),
                "USD",
                5,
                "FEDEX-GROUND-" + UUID.randomUUID().toString().substring(0, 8)));

        // FedEx Express
        rates.add(new RateDTO(
                CarrierType.FEDEX,
                "Express Saver",
                calculateMockRate(shipment, 3.2),
                "USD",
                3,
                "FEDEX-EXPRESS-" + UUID.randomUUID().toString().substring(0, 8)));

        // USPS Priority Mail
        rates.add(new RateDTO(
                CarrierType.USPS,
                "Priority Mail",
                calculateMockRate(shipment, 1.2),
                "USD",
                3,
                "USPS-PRIORITY-" + UUID.randomUUID().toString().substring(0, 8)));

        // Sort by rate (cheapest first)
        rates.sort((r1, r2) -> Double.compare(r1.getRate(), r2.getRate()));

        return rates;
    }

    /**
     * Calculate mock rate based on package weight and dimensions.
     */
    private double calculateMockRate(ShipmentDTO shipment, double multiplier) {
        PackageDTO pkg = shipment.getPackageInfo();
        double baseRate = pkg.getWeight() * multiplier;

        // Add dimensional weight factor
        double dimWeight = (pkg.getLength() * pkg.getWidth() * pkg.getHeight()) / 166;
        if (dimWeight > pkg.getWeight()) {
            baseRate = dimWeight * multiplier;
        }

        // Round to 2 decimal places
        return Math.round(baseRate * 100.0) / 100.0;
    }
}
