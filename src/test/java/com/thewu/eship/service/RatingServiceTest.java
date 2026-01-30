package com.thewu.eship.service;

import com.thewu.eship.dto.shipping.*;
import com.thewu.eship.service.shipping.RatingService;
import com.thewu.eship.service.ups.UpsRatingService;
import com.thewu.eship.service.fedex.FedexRatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RatingService with mock data.
 * Tests all carriers: UPS, FedEx, DHL, USPS
 */
@SpringBootTest
public class RatingServiceTest {

    @Autowired
    private RatingService ratingService;

    private ShipmentDTO testShipment;

    @BeforeEach
    void setUp() {
        // Create test addresses
        AddressDTO from = new AddressDTO();
        from.setName("Sender Name");
        from.setStreet1("100 Main St");
        from.setCity("New York");
        from.setState("NY");
        from.setPostalCode("10001");
        from.setCountry("US");

        AddressDTO to = new AddressDTO();
        to.setName("Recipient Name");
        to.setStreet1("200 Market St");
        to.setCity("San Francisco");
        to.setState("CA");
        to.setPostalCode("94102");
        to.setCountry("US");

        // Create test package
        PackageDTO pkg = new PackageDTO();
        pkg.setWeight(10.0);
        pkg.setLength(15.0);
        pkg.setWidth(12.0);
        pkg.setHeight(10.0);
        pkg.setValue(200.0);
        pkg.setDescription("Test Package");

        // Create test shipment
        testShipment = new ShipmentDTO();
        testShipment.setFromAddress(from);
        testShipment.setToAddress(to);
        testShipment.setPackageInfo(pkg);
    }

    @Test
    @DisplayName("Test get rates returns multiple carriers")
    void testGetRatesMultipleCarriers() {
        List<RateDTO> rates = ratingService.getRates(testShipment);

        assertNotNull(rates, "Rates should not be null");
        assertFalse(rates.isEmpty(), "Should return at least one rate");
        
        // Check that we have rates from multiple carriers
        System.out.println("\n=== RATE COMPARISON TEST ===");
        System.out.println("Total rates received: " + rates.size());
        
        for (RateDTO rate : rates) {
            System.out.println(String.format("%s - %s: $%.2f (Transit: %d days)",
                rate.getCarrier(), rate.getService(), rate.getRate(), rate.getDeliveryDays()));
            
            assertNotNull(rate.getCarrier(), "Carrier should not be null");
            assertNotNull(rate.getService(), "Service should not be null");
            assertTrue(rate.getRate() > 0, "Rate should be positive");
            assertTrue(rate.getDeliveryDays() > 0, "Transit days should be positive");
        }
    }

    @Test
    @DisplayName("Test rates are sorted by price")
    void testRatesSortedByPrice() {
        List<RateDTO> rates = ratingService.getRates(testShipment);

        assertFalse(rates.isEmpty(), "Should have rates");

        // Verify rates are sorted (cheapest first)
        for (int i = 0; i < rates.size() - 1; i++) {
            assertTrue(rates.get(i).getRate() <= rates.get(i + 1).getRate(),
                "Rates should be sorted by price (cheapest first)");
        }

        System.out.println("\n=== SORTED RATES TEST ===");
        System.out.println("✓ Rates are correctly sorted by price");
        System.out.println("Cheapest: $" + rates.get(0).getRate());
        System.out.println("Most expensive: $" + rates.get(rates.size() - 1).getRate());
    }

    @Test
    @DisplayName("Test UPS rates with different services")
    void testUpsServiceVariety() {
        List<RateDTO> rates = ratingService.getRates(testShipment);
        
        long upsRates = rates.stream()
            .filter(r -> r.getCarrier() == CarrierType.UPS)
            .count();

        assertTrue(upsRates > 0, "Should have UPS rates");

        System.out.println("\n=== UPS SERVICE VARIETY TEST ===");
        System.out.println("UPS services available: " + upsRates);
        
        rates.stream()
            .filter(r -> r.getCarrier() == CarrierType.UPS)
            .forEach(r -> System.out.println("  - " + r.getService() + ": $" + r.getRate()));
    }

    @Test
    @DisplayName("Test FedEx rates with different services")
    void testFedexServiceVariety() {
        List<RateDTO> rates = ratingService.getRates(testShipment);
        
        long fedexRates = rates.stream()
            .filter(r -> r.getCarrier() == CarrierType.FEDEX)
            .count();

        assertTrue(fedexRates > 0, "Should have FedEx rates");

        System.out.println("\n=== FEDEX SERVICE VARIETY TEST ===");
        System.out.println("FedEx services available: " + fedexRates);
        
        rates.stream()
            .filter(r -> r.getCarrier() == CarrierType.FEDEX)
            .forEach(r -> System.out.println("  - " + r.getService() + ": $" + r.getRate()));
    }

    @Test
    @DisplayName("Test DHL rates available")
    void testDhlRates() {
        List<RateDTO> rates = ratingService.getRates(testShipment);
        
        long dhlRates = rates.stream()
            .filter(r -> r.getCarrier() == CarrierType.DHL)
            .count();

        assertTrue(dhlRates > 0, "Should have DHL rates");

        System.out.println("\n=== DHL RATES TEST ===");
        System.out.println("DHL services available: " + dhlRates);
        
        rates.stream()
            .filter(r -> r.getCarrier() == CarrierType.DHL)
            .forEach(r -> System.out.println("  - " + r.getService() + ": $" + r.getRate()));
    }

    @Test
    @DisplayName("Test international shipment rates")
    void testInternationalRates() {
        // Change destination to international
        testShipment.getToAddress().setCountry("CA");
        testShipment.getToAddress().setCity("Toronto");
        testShipment.getToAddress().setState("ON");
        testShipment.getToAddress().setPostalCode("M5H 2N2");

        List<RateDTO> rates = ratingService.getRates(testShipment);

        assertNotNull(rates, "Should have international rates");
        assertFalse(rates.isEmpty(), "Should return at least one international rate");

        System.out.println("\n=== INTERNATIONAL RATES TEST ===");
        System.out.println("International rates to Canada: " + rates.size());
        
        rates.forEach(r -> System.out.println(String.format("%s %s: $%.2f",
            r.getCarrier(), r.getService(), r.getRate())));
    }

    @Test
    @DisplayName("Test heavy package rates")
    void testHeavyPackageRates() {
        // Set heavy weight
        testShipment.getPackageInfo().setWeight(100.0);
        testShipment.getPackageInfo().setLength(36.0);
        testShipment.getPackageInfo().setWidth(30.0);
        testShipment.getPackageInfo().setHeight(24.0);

        List<RateDTO> rates = ratingService.getRates(testShipment);

        assertNotNull(rates, "Should have rates for heavy package");
        
        System.out.println("\n=== HEAVY PACKAGE RATES TEST ===");
        System.out.println("Rates for 100 lb package:");
        
        rates.forEach(r -> System.out.println(String.format("%s %s: $%.2f",
            r.getCarrier(), r.getService(), r.getRate())));
    }

    @Test
    @DisplayName("Test lightweight package rates")
    void testLightweightPackageRates() {
        // Set very light weight
        testShipment.getPackageInfo().setWeight(1.0);
        testShipment.getPackageInfo().setLength(6.0);
        testShipment.getPackageInfo().setWidth(4.0);
        testShipment.getPackageInfo().setHeight(2.0);

        List<RateDTO> rates = ratingService.getRates(testShipment);

        assertNotNull(rates, "Should have rates for lightweight package");
        
        System.out.println("\n=== LIGHTWEIGHT PACKAGE RATES TEST ===");
        System.out.println("Rates for 1 lb package:");
        
        rates.forEach(r -> System.out.println(String.format("%s %s: $%.2f",
            r.getCarrier(), r.getService(), r.getRate())));
    }

    @Test
    @DisplayName("Test rate comparison between carriers")
    void testCarrierComparison() {
        List<RateDTO> rates = ratingService.getRates(testShipment);

        System.out.println("\n=== CARRIER COMPARISON TEST ===");
        System.out.println("Comparing rates across all carriers:");
        
        // Group by carrier and find cheapest for each
        rates.stream()
            .collect(java.util.stream.Collectors.groupingBy(RateDTO::getCarrier))
            .forEach((carrier, carrierRates) -> {
                RateDTO cheapest = carrierRates.stream()
                    .min(java.util.Comparator.comparing(RateDTO::getRate))
                    .orElse(null);
                
                if (cheapest != null) {
                    System.out.println(String.format("%s cheapest: %s at $%.2f",
                        carrier, cheapest.getService(), cheapest.getRate()));
                }
            });
    }

    @Test
    @DisplayName("Test all rates have valid currency")
    void testRateCurrency() {
        List<RateDTO> rates = ratingService.getRates(testShipment);

        for (RateDTO rate : rates) {
            assertEquals("USD", rate.getCurrency(), "All rates should be in USD");
        }

        System.out.println("\n=== CURRENCY VALIDATION TEST ===");
        System.out.println("✓ All " + rates.size() + " rates are in USD");
    }

    @Test
    @DisplayName("Test all rates have valid transit days")
    void testTransitDays() {
        List<RateDTO> rates = ratingService.getRates(testShipment);

        for (RateDTO rate : rates) {
            assertTrue(rate.getDeliveryDays() > 0, "Transit days should be positive");
            assertTrue(rate.getDeliveryDays() <= 30, "Transit days should be reasonable");
        }

        System.out.println("\n=== TRANSIT DAYS VALIDATION TEST ===");
        System.out.println("✓ All rates have valid transit times");
    }
}
