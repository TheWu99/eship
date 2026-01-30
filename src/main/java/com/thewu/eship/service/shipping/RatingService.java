package com.thewu.eship.service.shipping;

import com.thewu.eship.dto.shipping.*;
import com.thewu.eship.service.ups.UpsRatingService;
import com.thewu.eship.service.fedex.FedexRatingService;
import com.thewu.eship.service.dhl.DhlRatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for retrieving shipping rates from carriers.
 * Integrates with UPS, FedEx, and DHL APIs, with fallback to mock data.
 */
@Service
public class RatingService {

    private static final Logger log = LoggerFactory.getLogger(RatingService.class);

    @Autowired(required = false)
    private UpsRatingService upsRatingService;

    @Autowired(required = false)
    private FedexRatingService fedexRatingService;

    @Autowired(required = false)
    private DhlRatingService dhlRatingService;

    @Autowired
    private RateComparisonService comparisonService;

    /**
     * Get shipping rates from all carriers.
     * 
     * @param shipment The shipment details
     * @return List of rates sorted by price
     */
    public List<RateDTO> getRates(ShipmentDTO shipment) {
        List<RateDTO> rates = new ArrayList<>();

        // Get rates from UPS if service is available
        if (upsRatingService != null && shipment.getFromAddress() != null &&
                shipment.getToAddress() != null && shipment.getPackageInfo() != null) {
            try {
                log.info("Fetching rates from UPS API for shipment");
                List<RateDTO> upsRates = upsRatingService.shopRates(
                        shipment.getFromAddress(),
                        shipment.getToAddress(),
                        java.util.Arrays.asList(shipment.getPackageInfo()));
                rates.addAll(upsRates);
                log.info("Retrieved {} rates from UPS", upsRates.size());
            } catch (Exception e) {
                log.error("Failed to get UPS rates, falling back to mock data", e);
                rates.addAll(getMockUpsRates(shipment));
            }
        } else {
            log.warn("UPS service not available or shipment data incomplete, using mock rates");
            rates.addAll(getMockUpsRates(shipment));
        }

        // Get rates from FedEx if service is available
        if (fedexRatingService != null && shipment.getFromAddress() != null &&
                shipment.getToAddress() != null && shipment.getPackageInfo() != null) {
            try {
                log.info("Fetching rates from FedEx API for shipment");
                List<RateDTO> fedexRates = fedexRatingService.shopRates(
                        shipment.getFromAddress(),
                        shipment.getToAddress(),
                        java.util.Arrays.asList(shipment.getPackageInfo()));
                rates.addAll(fedexRates);
                log.info("Retrieved {} rates from FedEx", fedexRates.size());
            } catch (Exception e) {
                log.error("Failed to get FedEx rates, falling back to mock data", e);
                rates.addAll(getMockFedexRates(shipment));
            }
        } else {
            log.warn("FedEx service not available or shipment data incomplete, using mock rates");
            rates.addAll(getMockFedexRates(shipment));
        }

        // Get rates from DHL if service is available
        if (dhlRatingService != null && shipment.getFromAddress() != null &&
                shipment.getToAddress() != null && shipment.getPackageInfo() != null) {
            try {
                log.info("Fetching rates from DHL API for shipment");
                List<RateDTO> dhlRates = dhlRatingService.shopRates(
                        shipment.getFromAddress(),
                        shipment.getToAddress(),
                        java.util.Arrays.asList(shipment.getPackageInfo()));
                rates.addAll(dhlRates);
                log.info("Retrieved {} rates from DHL", dhlRates.size());
            } catch (Exception e) {
                log.error("Failed to get DHL rates, falling back to mock data", e);
                rates.addAll(getMockDhlRates(shipment));
            }
        } else {
            log.warn("DHL service not available or shipment data incomplete, using mock rates");
            rates.addAll(getMockDhlRates(shipment));
        }

        // Add mock rates for other carriers (USPS)
        rates.addAll(getMockOtherCarrierRates(shipment));

        // Sort by rate (cheapest first)
        rates.sort((r1, r2) -> Double.compare(r1.getRate(), r2.getRate()));

        return rates;
    }

    /**
     * Compare UPS vs FedEx rates for a shipment
     */
    public RateComparisonService.RateComparisonResult compareUpsVsFedex(ShipmentDTO shipment) {
        List<RateDTO> allRates = getRates(shipment);
        return comparisonService.compareUpsVsFedex(allRates);
    }

    /**
     * Compare rates from all carriers (UPS, FedEx, DHL)
     */
    public RateComparisonService.RateComparisonResult compareAllCarriers(ShipmentDTO shipment) {
        List<RateDTO> allRates = getRates(shipment);
        return comparisonService.compareAllCarriers(allRates);
    }

    /**
     * Get mock UPS rates (fallback when API is not available)
     */
    private List<RateDTO> getMockUpsRates(ShipmentDTO shipment) {
        List<RateDTO> rates = new ArrayList<>();

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

        return rates;
    }

    /**
     * Get mock FedEx rates (fallback when API is not available)
     */
    private List<RateDTO> getMockFedexRates(ShipmentDTO shipment) {
        List<RateDTO> rates = new ArrayList<>();

        // FedEx Ground
        rates.add(new RateDTO(
                CarrierType.FEDEX,
                "Ground",
                calculateMockRate(shipment, 1.6),
                "USD",
                5,
                "FEDEX-GROUND-" + UUID.randomUUID().toString().substring(0, 8)));

        // FedEx Express Saver
        rates.add(new RateDTO(
                CarrierType.FEDEX,
                "Express Saver",
                calculateMockRate(shipment, 3.2),
                "USD",
                3,
                "FEDEX-EXPRESS-" + UUID.randomUUID().toString().substring(0, 8)));

        // FedEx 2Day
        rates.add(new RateDTO(
                CarrierType.FEDEX,
                "2Day",
                calculateMockRate(shipment, 2.9),
                "USD",
                2,
                "FEDEX-2DAY-" + UUID.randomUUID().toString().substring(0, 8)));

        // FedEx Standard Overnight
        rates.add(new RateDTO(
                CarrierType.FEDEX,
                "Standard Overnight",
                calculateMockRate(shipment, 4.5),
                "USD",
                1,
                "FEDEX-OVERNIGHT-" + UUID.randomUUID().toString().substring(0, 8)));

        return rates;
    }

    /**
     * Get mock rates for other carriers
     */
    private List<RateDTO> getMockOtherCarrierRates(ShipmentDTO shipment) {
        List<RateDTO> rates = new ArrayList<>();

        // USPS Priority Mail
        rates.add(new RateDTO(
                CarrierType.USPS,
                "Priority Mail",
                calculateMockRate(shipment, 1.2),
                "USD",
                3,
                "USPS-PRIORITY-" + UUID.randomUUID().toString().substring(0, 8)));

        return rates;
    }

    /**
     * Get mock DHL rates (fallback when API is not available)
     */
    private List<RateDTO> getMockDhlRates(ShipmentDTO shipment) {
        List<RateDTO> rates = new ArrayList<>();

        // DHL Express Worldwide
        rates.add(new RateDTO(
                CarrierType.DHL,
                "Express Worldwide",
                calculateMockRate(shipment, 3.5),
                "USD",
                2,
                "DHL-EXPRESS-" + UUID.randomUUID().toString().substring(0, 8)));

        // DHL Economy Select
        rates.add(new RateDTO(
                CarrierType.DHL,
                "Economy Select",
                calculateMockRate(shipment, 2.0),
                "USD",
                5,
                "DHL-ECONOMY-" + UUID.randomUUID().toString().substring(0, 8)));

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
