package com.thewu.eship.service;

import com.thewu.eship.dto.shipping.*;
import com.thewu.eship.service.shipping.LabelGenerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LabelGenerationService with mock data.
 * Tests label generation for all carriers and formats.
 */
@SpringBootTest
public class LabelGenerationServiceTest {

    @Autowired
    private LabelGenerationService labelService;

    private ShipmentDTO testShipment;

    @BeforeEach
    void setUp() {
        // Create test addresses
        AddressDTO from = new AddressDTO();
        from.setName("Acme Corp");
        from.setStreet1("100 Main St");
        from.setCity("New York");
        from.setState("NY");
        from.setPostalCode("10001");
        from.setCountry("US");
        from.setPhone("212-555-0001");

        AddressDTO to = new AddressDTO();
        to.setName("John Customer");
        to.setStreet1("200 Market St");
        to.setCity("San Francisco");
        to.setState("CA");
        to.setPostalCode("94102");
        to.setCountry("US");
        to.setPhone("415-555-0002");

        // Create test package
        PackageDTO pkg = new PackageDTO();
        pkg.setWeight(5.0);
        pkg.setLength(12.0);
        pkg.setWidth(10.0);
        pkg.setHeight(8.0);
        pkg.setValue(150.0);
        pkg.setDescription("Electronics");

        // Create test shipment
        testShipment = new ShipmentDTO();
        testShipment.setFromAddress(from);
        testShipment.setToAddress(to);
        testShipment.setPackageInfo(pkg);
    }

    @Test
    @DisplayName("Test generate UPS label in PDF format")
    void testGenerateUpsLabelPdf() {
        String trackingNumber = labelService.generateTrackingNumber(CarrierType.UPS);
        
        LabelDTO label = labelService.generateLabel(
            testShipment, 
            trackingNumber, 
            CarrierType.UPS, 
            LabelFormat.PDF
        );

        assertNotNull(label, "Label should not be null");
        assertEquals(trackingNumber, label.getTrackingNumber());
        assertEquals(CarrierType.UPS, label.getCarrier());
        assertEquals(LabelFormat.PDF, label.getFormat());
        assertNotNull(label.getContent(), "Label data should not be null");
        assertTrue(label.getContent().length() > 0, "Label data should not be empty");

        System.out.println("\n=== UPS PDF LABEL TEST ===");
        System.out.println("Tracking #: " + label.getTrackingNumber());
        System.out.println("Carrier: " + label.getCarrier());
        System.out.println("Format: " + label.getFormat());
        System.out.println("Label Data Length: " + label.getContent().length() + " characters");
        System.out.println("Created: " + label.getCreatedAt());
    }

    @Test
    @DisplayName("Test generate FedEx label in PDF format")
    void testGenerateFedexLabelPdf() {
        String trackingNumber = labelService.generateTrackingNumber(CarrierType.FEDEX);
        
        LabelDTO label = labelService.generateLabel(
            testShipment, 
            trackingNumber, 
            CarrierType.FEDEX, 
            LabelFormat.PDF
        );

        assertNotNull(label);
        assertEquals(trackingNumber, label.getTrackingNumber());
        assertEquals(CarrierType.FEDEX, label.getCarrier());
        assertEquals(LabelFormat.PDF, label.getFormat());

        System.out.println("\n=== FEDEX PDF LABEL TEST ===");
        System.out.println("Tracking #: " + label.getTrackingNumber());
        System.out.println("Carrier: " + label.getCarrier());
        System.out.println("Format: " + label.getFormat());
        System.out.println("Label Data Length: " + label.getContent().length() + " characters");
    }

    @Test
    @DisplayName("Test generate DHL label in PDF format")
    void testGenerateDhlLabelPdf() {
        String trackingNumber = labelService.generateTrackingNumber(CarrierType.DHL);
        
        LabelDTO label = labelService.generateLabel(
            testShipment, 
            trackingNumber, 
            CarrierType.DHL, 
            LabelFormat.PDF
        );

        assertNotNull(label);
        assertEquals(trackingNumber, label.getTrackingNumber());
        assertEquals(CarrierType.DHL, label.getCarrier());
        assertEquals(LabelFormat.PDF, label.getFormat());

        System.out.println("\n=== DHL PDF LABEL TEST ===");
        System.out.println("Tracking #: " + label.getTrackingNumber());
        System.out.println("Carrier: " + label.getCarrier());
        System.out.println("Format: " + label.getFormat());
        System.out.println("Label Data Length: " + label.getContent().length() + " characters");
    }

    @Test
    @DisplayName("Test generate label in ZPL format")
    void testGenerateLabelZpl() {
        String trackingNumber = labelService.generateTrackingNumber(CarrierType.UPS);
        
        LabelDTO label = labelService.generateLabel(
            testShipment, 
            trackingNumber, 
            CarrierType.UPS, 
            LabelFormat.ZPL
        );

        assertNotNull(label);
        assertEquals(LabelFormat.ZPL, label.getFormat());
        assertNotNull(label.getContent(), "ZPL content should not be null");
        assertFalse(label.getContent().isEmpty(), "ZPL content should not be empty");
        // Mock ZPL format verification
        assertTrue(label.getContent().length() > 0, "ZPL should have content");

        System.out.println("\n=== ZPL FORMAT LABEL TEST ===");
        System.out.println("Tracking #: " + label.getTrackingNumber());
        System.out.println("Format: " + label.getFormat());
        System.out.println("ZPL Preview (first 200 chars):");
        System.out.println(label.getContent().substring(0, Math.min(200, label.getContent().length())));
    }

    @Test
    @DisplayName("Test generate label in PNG format")
    void testGenerateLabelPng() {
        String trackingNumber = labelService.generateTrackingNumber(CarrierType.FEDEX);
        
        LabelDTO label = labelService.generateLabel(
            testShipment, 
            trackingNumber, 
            CarrierType.FEDEX, 
            LabelFormat.PNG
        );

        assertNotNull(label);
        assertEquals(LabelFormat.PNG, label.getFormat());
        assertNotNull(label.getContent());

        System.out.println("\n=== PNG FORMAT LABEL TEST ===");
        System.out.println("Tracking #: " + label.getTrackingNumber());
        System.out.println("Format: " + label.getFormat());
        System.out.println("Image Data Length: " + label.getContent().length() + " characters");
    }

    @Test
    @DisplayName("Test tracking number generation for all carriers")
    void testTrackingNumberGeneration() {
        System.out.println("\n=== TRACKING NUMBER GENERATION TEST ===");
        
        for (CarrierType carrier : Arrays.asList(CarrierType.UPS, CarrierType.FEDEX, CarrierType.DHL, CarrierType.USPS)) {
            String trackingNumber = labelService.generateTrackingNumber(carrier);
            
            assertNotNull(trackingNumber, "Tracking number should not be null for " + carrier);
            assertFalse(trackingNumber.isEmpty(), "Tracking number should not be empty for " + carrier);
            
            System.out.println(carrier + " Tracking #: " + trackingNumber);
            
            // Verify format patterns
            switch (carrier) {
                case UPS:
                    assertTrue(trackingNumber.startsWith("1Z"), "UPS tracking should start with 1Z");
                    break;
                case FEDEX:
                    // FedEx tracking can be alphanumeric in mock service
                    assertTrue(trackingNumber.length() >= 12, "FedEx tracking should be at least 12 characters");
                    break;
                case DHL:
                    // DHL tracking uses UUID substring which may contain letters
                    assertTrue(trackingNumber.length() >= 10, "DHL tracking should be at least 10 characters");
                    break;
                case USPS:
                    // USPS tracking uses "92" prefix + UUID substring which may contain letters
                    assertTrue(trackingNumber.startsWith("92"), "USPS tracking should start with 92");
                    assertTrue(trackingNumber.length() >= 20, "USPS tracking should be at least 20 characters");
                    break;
            }
        }
    }

    @Test
    @DisplayName("Test international label with customs")
    void testInternationalLabelWithCustoms() {
        // Change to international destination
        testShipment.getToAddress().setCountry("CA");
        testShipment.getToAddress().setCity("Toronto");
        testShipment.getToAddress().setState("ON");
        testShipment.getToAddress().setPostalCode("M5H 2N2");

        // Add customs information
        CustomsFormDTO customs = new CustomsFormDTO();
        customs.setContentsType("MERCHANDISE");
        customs.setContentsExplanation("Electronics");
        
        CustomsItemDTO item = new CustomsItemDTO();
        item.setDescription("Smartphone");
        item.setQuantity(1);
        item.setValue(150.0);
        item.setWeight(5.0);
        item.setOriginCountry("US");
        item.setHsCode("8517.12.00");
        
        customs.setItems(Arrays.asList(item));
        testShipment.setCustoms(customs);

        String trackingNumber = labelService.generateTrackingNumber(CarrierType.UPS);
        
        LabelDTO label = labelService.generateLabel(
            testShipment, 
            trackingNumber, 
            CarrierType.UPS, 
            LabelFormat.PDF
        );

        assertNotNull(label);
        assertEquals(trackingNumber, label.getTrackingNumber());

        System.out.println("\n=== INTERNATIONAL LABEL WITH CUSTOMS TEST ===");
        System.out.println("Tracking #: " + label.getTrackingNumber());
        System.out.println("Destination: " + testShipment.getToAddress().getCountry());
        System.out.println("Customs Value: $" + item.getValue());
        System.out.println("Harmonized Code: " + item.getHsCode());
        System.out.println("✓ International label generated successfully");
    }

    @Test
    @DisplayName("Test multiple labels generation")
    void testMultipleLabelsGeneration() {
        System.out.println("\n=== BATCH LABEL GENERATION TEST ===");
        
        for (int i = 1; i <= 5; i++) {
            String trackingNumber = labelService.generateTrackingNumber(CarrierType.UPS);
            
            LabelDTO label = labelService.generateLabel(
                testShipment, 
                trackingNumber, 
                CarrierType.UPS, 
                LabelFormat.PDF
            );

            assertNotNull(label);
            System.out.println("Label " + i + " - Tracking #: " + label.getTrackingNumber());
        }
        
        System.out.println("✓ Successfully generated 5 labels");
    }

    @Test
    @DisplayName("Test label contains essential information")
    void testLabelContainsEssentialInfo() {
        String trackingNumber = labelService.generateTrackingNumber(CarrierType.UPS);
        
        LabelDTO label = labelService.generateLabel(
            testShipment, 
            trackingNumber, 
            CarrierType.UPS, 
            LabelFormat.PDF
        );

        assertNotNull(label.getTrackingNumber(), "Should have tracking number");
        assertNotNull(label.getCarrier(), "Should have carrier");
        assertNotNull(label.getFormat(), "Should have format");
        assertNotNull(label.getContent(), "Should have label data");
        assertNotNull(label.getCreatedAt(), "Should have creation timestamp");

        System.out.println("\n=== LABEL CONTENT VALIDATION TEST ===");
        System.out.println("✓ Tracking Number: " + label.getTrackingNumber());
        System.out.println("✓ Carrier: " + label.getCarrier());
        System.out.println("✓ Format: " + label.getFormat());
        System.out.println("✓ Label Data: Present (" + label.getContent().length() + " chars)");
        System.out.println("✓ Timestamp: " + label.getCreatedAt());
    }

    @Test
    @DisplayName("Test all carriers can generate labels")
    void testAllCarriersLabelGeneration() {
        System.out.println("\n=== ALL CARRIERS LABEL GENERATION TEST ===");
        
        for (CarrierType carrier : Arrays.asList(CarrierType.UPS, CarrierType.FEDEX, CarrierType.DHL)) {
            String trackingNumber = labelService.generateTrackingNumber(carrier);
            
            LabelDTO label = labelService.generateLabel(
                testShipment, 
                trackingNumber, 
                carrier, 
                LabelFormat.PDF
            );

            assertNotNull(label, carrier + " should generate label");
            assertEquals(carrier, label.getCarrier());
            
            System.out.println(carrier + " ✓ Label generated: " + label.getTrackingNumber());
        }
    }
}
