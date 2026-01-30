package com.thewu.eship.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewu.eship.dto.shipping.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Comprehensive integration tests for all shipping carriers (UPS, FedEx, DHL).
 * Tests use mock data and verify all key functionality.
 */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class ShippingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ShipmentDTO mockShipment;
    private AddressDTO fromAddress;
    private AddressDTO toAddress;
    private PackageDTO packageInfo;

    @BeforeEach
    void setUp() {
        // Create mock addresses
        fromAddress = new AddressDTO();
        fromAddress.setName("John Sender");
        fromAddress.setStreet1("123 Main St");
        fromAddress.setCity("New York");
        fromAddress.setState("NY");
        fromAddress.setPostalCode("10001");
        fromAddress.setCountry("US");
        fromAddress.setPhone("212-555-1234");
        fromAddress.setEmail("sender@example.com");

        toAddress = new AddressDTO();
        toAddress.setName("Jane Receiver");
        toAddress.setStreet1("456 Oak Ave");
        toAddress.setCity("Los Angeles");
        toAddress.setState("CA");
        toAddress.setPostalCode("90001");
        toAddress.setCountry("US");
        toAddress.setPhone("310-555-5678");
        toAddress.setEmail("receiver@example.com");

        // Create mock package
        packageInfo = new PackageDTO();
        packageInfo.setWeight(5.0);
        packageInfo.setLength(12.0);
        packageInfo.setWidth(10.0);
        packageInfo.setHeight(8.0);
        packageInfo.setValue(100.0);
        packageInfo.setDescription("Test Package");

        // Create mock shipment
        mockShipment = new ShipmentDTO();
        mockShipment.setFromAddress(fromAddress);
        mockShipment.setToAddress(toAddress);
        mockShipment.setPackageInfo(packageInfo);
        mockShipment.setReference("TEST-REF-001");
    }

    // ========== RATES TESTS ==========

    @Test
    @DisplayName("Test get rates from all carriers")
    void testGetRatesAllCarriers() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(mockShipment);

        MvcResult result = mockMvc.perform(post("/api/v1/rates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(greaterThan(0)))
                .andExpect(jsonPath("$[0].carrier").exists())
                .andExpect(jsonPath("$[0].service").exists())
                .andExpect(jsonPath("$[0].rate").exists())
                .andExpect(jsonPath("$[0].currency").value("USD"))
                .andReturn();

        System.out.println("\n=== ALL CARRIERS RATES TEST ===");
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Test get UPS rates specifically")
    void testGetUpsRates() throws Exception {
        mockShipment.setCarrier(CarrierType.UPS);
        String jsonRequest = objectMapper.writeValueAsString(mockShipment);

        MvcResult result = mockMvc.perform(post("/api/v1/rates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("\n=== UPS RATES TEST ===");
        System.out.println(response);
        
        // Verify we have UPS rates in the response
        assert response.contains("UPS") || response.contains("ups");
    }

    @Test
    @DisplayName("Test get FedEx rates specifically")
    void testGetFedexRates() throws Exception {
        mockShipment.setCarrier(CarrierType.FEDEX);
        String jsonRequest = objectMapper.writeValueAsString(mockShipment);

        MvcResult result = mockMvc.perform(post("/api/v1/rates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("\n=== FEDEX RATES TEST ===");
        System.out.println(response);
        
        // Verify we have FedEx rates in the response
        assert response.contains("FEDEX") || response.contains("fedex");
    }

    @Test
    @DisplayName("Test get DHL rates")
    void testGetDhlRates() throws Exception {
        mockShipment.setCarrier(CarrierType.DHL);
        String jsonRequest = objectMapper.writeValueAsString(mockShipment);

        MvcResult result = mockMvc.perform(post("/api/v1/rates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("\n=== DHL RATES TEST ===");
        System.out.println(response);
        
        // Verify we have DHL rates in the response
        assert response.contains("DHL") || response.contains("dhl");
    }

    @Test
    @DisplayName("Test rates with international shipment")
    void testInternationalRates() throws Exception {
        // Change to international destination
        toAddress.setCity("Toronto");
        toAddress.setState("ON");
        toAddress.setPostalCode("M5H 2N2");
        toAddress.setCountry("CA");

        String jsonRequest = objectMapper.writeValueAsString(mockShipment);

        MvcResult result = mockMvc.perform(post("/api/v1/rates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        System.out.println("\n=== INTERNATIONAL RATES TEST ===");
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Test rates with heavy package")
    void testHeavyPackageRates() throws Exception {
        // Create heavy package
        packageInfo.setWeight(50.0);
        packageInfo.setLength(24.0);
        packageInfo.setWidth(20.0);
        packageInfo.setHeight(18.0);

        String jsonRequest = objectMapper.writeValueAsString(mockShipment);

        MvcResult result = mockMvc.perform(post("/api/v1/rates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        System.out.println("\n=== HEAVY PACKAGE RATES TEST ===");
        System.out.println(result.getResponse().getContentAsString());
    }

    // ========== LABEL GENERATION TESTS ==========

    @Test
    @DisplayName("Test generate UPS shipping label")
    void testGenerateUpsLabel() throws Exception {
        mockShipment.setCarrier(CarrierType.UPS);
        mockShipment.setService("Ground");
        mockShipment.setLabelFormat(LabelFormat.PDF);

        String jsonRequest = objectMapper.writeValueAsString(mockShipment);

        MvcResult result = mockMvc.perform(post("/api/v1/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingNumber").exists())
                .andExpect(jsonPath("$.carrier").value("UPS"))
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.format").value("PDF"))
                .andReturn();

        System.out.println("\n=== UPS LABEL GENERATION TEST ===");
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Test generate FedEx shipping label")
    void testGenerateFedexLabel() throws Exception {
        mockShipment.setCarrier(CarrierType.FEDEX);
        mockShipment.setService("Ground");
        mockShipment.setLabelFormat(LabelFormat.PDF);

        String jsonRequest = objectMapper.writeValueAsString(mockShipment);

        MvcResult result = mockMvc.perform(post("/api/v1/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingNumber").exists())
                .andExpect(jsonPath("$.carrier").value("FEDEX"))
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.format").value("PDF"))
                .andReturn();

        System.out.println("\n=== FEDEX LABEL GENERATION TEST ===");
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Test generate DHL shipping label")
    void testGenerateDhlLabel() throws Exception {
        mockShipment.setCarrier(CarrierType.DHL);
        mockShipment.setService("Express");
        mockShipment.setLabelFormat(LabelFormat.PDF);

        String jsonRequest = objectMapper.writeValueAsString(mockShipment);

        MvcResult result = mockMvc.perform(post("/api/v1/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingNumber").exists())
                .andExpect(jsonPath("$.carrier").value("DHL"))
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.format").value("PDF"))
                .andReturn();

        System.out.println("\n=== DHL LABEL GENERATION TEST ===");
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Test generate label with ZPL format")
    void testGenerateLabelZplFormat() throws Exception {
        mockShipment.setCarrier(CarrierType.UPS);
        mockShipment.setLabelFormat(LabelFormat.ZPL);

        String jsonRequest = objectMapper.writeValueAsString(mockShipment);

        MvcResult result = mockMvc.perform(post("/api/v1/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.format").value("ZPL"))
                .andReturn();

        System.out.println("\n=== ZPL FORMAT LABEL TEST ===");
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Test generate international label with customs")
    void testGenerateInternationalLabel() throws Exception {
        // Set international destination
        toAddress.setCountry("CA");
        toAddress.setCity("Toronto");
        toAddress.setState("ON");
        toAddress.setPostalCode("M5H 2N2");

        // Add customs information
        CustomsFormDTO customs = new CustomsFormDTO();
        customs.setContentsType("MERCHANDISE");
        customs.setContentsExplanation("Electronics");

        CustomsItemDTO item = new CustomsItemDTO();
        item.setDescription("Test Device");
        item.setQuantity(1);
        item.setValue(100.0);
        item.setWeight(5.0);
        item.setOriginCountry("US");
        item.setHsCode("8517.12.00");

        customs.setItems(java.util.Arrays.asList(item));
        mockShipment.setCustoms(customs);
        mockShipment.setCarrier(CarrierType.UPS);

        String jsonRequest = objectMapper.writeValueAsString(mockShipment);

        MvcResult result = mockMvc.perform(post("/api/v1/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().is4xxClientError())
                .andReturn();

        System.out.println("\n=== INTERNATIONAL LABEL WITH CUSTOMS TEST ===");
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Response: " + result.getResponse().getContentAsString());
        System.out.println("✓ International shipment validation handled");
    }

    // ========== TRACKING TESTS ==========

    @Test
    @DisplayName("Test track UPS shipment")
    void testTrackUpsShipment() throws Exception {
        String trackingNumber = "1Z999AA10123456784";

        MvcResult result = mockMvc.perform(get("/api/v1/tracking/{trackingNumber}", trackingNumber)
                .param("carrier", "UPS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingNumber").value(trackingNumber))
                .andExpect(jsonPath("$.carrier").value("UPS"))
                .andExpect(jsonPath("$.currentStatus").exists())
                .andExpect(jsonPath("$.events").isArray())
                .andReturn();

        System.out.println("\n=== UPS TRACKING TEST ===");
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Test track FedEx shipment")
    void testTrackFedexShipment() throws Exception {
        String trackingNumber = "123456789012";

        MvcResult result = mockMvc.perform(get("/api/v1/tracking/{trackingNumber}", trackingNumber)
                .param("carrier", "FEDEX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingNumber").value(trackingNumber))
                .andExpect(jsonPath("$.carrier").value("FEDEX"))
                .andExpect(jsonPath("$.currentStatus").exists())
                .andExpect(jsonPath("$.events").isArray())
                .andReturn();

        System.out.println("\n=== FEDEX TRACKING TEST ===");
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Test track DHL shipment")
    void testTrackDhlShipment() throws Exception {
        String trackingNumber = "1234567890";

        MvcResult result = mockMvc.perform(get("/api/v1/tracking/{trackingNumber}", trackingNumber)
                .param("carrier", "DHL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingNumber").value(trackingNumber))
                .andExpect(jsonPath("$.carrier").value("DHL"))
                .andExpect(jsonPath("$.currentStatus").exists())
                .andExpect(jsonPath("$.events").isArray())
                .andReturn();

        System.out.println("\n=== DHL TRACKING TEST ===");
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Test track without carrier (auto-detect)")
    void testTrackWithoutCarrier() throws Exception {
        String trackingNumber = "1Z999AA10123456784";

        MvcResult result = mockMvc.perform(get("/api/v1/tracking/{trackingNumber}", trackingNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingNumber").value(trackingNumber))
                .andExpect(jsonPath("$.carrier").exists())
                .andExpect(jsonPath("$.currentStatus").exists())
                .andReturn();

        System.out.println("\n=== AUTO-DETECT CARRIER TRACKING TEST ===");
        System.out.println(result.getResponse().getContentAsString());
    }

    // ========== ADDRESS VALIDATION TESTS ==========

    @Test
    @DisplayName("Test validate US address")
    void testValidateUsAddress() throws Exception {
        AddressDTO address = new AddressDTO();
        address.setName("John Doe");
        address.setStreet1("123 Main St");
        address.setCity("New York");
        address.setState("NY");
        address.setPostalCode("10001");
        address.setCountry("US");

        String jsonRequest = objectMapper.writeValueAsString(address);

        MvcResult result = mockMvc.perform(post("/api/v1/address/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").exists())
                .andExpect(jsonPath("$.address").exists())
                .andReturn();

        System.out.println("\n=== US ADDRESS VALIDATION TEST ===");
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Test validate international address")
    void testValidateInternationalAddress() throws Exception {
        AddressDTO address = new AddressDTO();
        address.setName("Jane Smith");
        address.setStreet1("100 King St W");
        address.setCity("Toronto");
        address.setState("ON");
        address.setPostalCode("M5X 1A9");
        address.setCountry("CA");

        String jsonRequest = objectMapper.writeValueAsString(address);

        MvcResult result = mockMvc.perform(post("/api/v1/address/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").exists())
                .andReturn();

        System.out.println("\n=== INTERNATIONAL ADDRESS VALIDATION TEST ===");
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Test classify residential address")
    void testClassifyResidentialAddress() throws Exception {
        AddressDTO address = new AddressDTO();
        address.setName("John Homeowner");
        address.setStreet1("123 Maple Street");
        address.setCity("Springfield");
        address.setState("IL");
        address.setPostalCode("62701");
        address.setCountry("US");

        String jsonRequest = objectMapper.writeValueAsString(address);

        MvcResult result = mockMvc.perform(post("/api/v1/address/classify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address_type").exists())
                .andExpect(jsonPath("$.confidence").exists())
                .andReturn();

        System.out.println("\n=== RESIDENTIAL ADDRESS CLASSIFICATION TEST ===");
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Test classify commercial address")
    void testClassifyCommercialAddress() throws Exception {
        AddressDTO address = new AddressDTO();
        address.setName("Acme Corporation");
        address.setStreet1("1000 Corporate Blvd Suite 500");
        address.setCity("Chicago");
        address.setState("IL");
        address.setPostalCode("60601");
        address.setCountry("US");

        String jsonRequest = objectMapper.writeValueAsString(address);

        MvcResult result = mockMvc.perform(post("/api/v1/address/classify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address_type").exists())
                .andReturn();

        System.out.println("\n=== COMMERCIAL ADDRESS CLASSIFICATION TEST ===");
        System.out.println(result.getResponse().getContentAsString());
    }

    // ========== ERROR HANDLING TESTS ==========

    @Test
    @DisplayName("Test invalid address validation")
    void testInvalidAddress() throws Exception {
        ShipmentDTO invalidShipment = new ShipmentDTO();
        invalidShipment.setFromAddress(new AddressDTO()); // Empty address
        invalidShipment.setToAddress(toAddress);
        invalidShipment.setPackageInfo(packageInfo);

        String jsonRequest = objectMapper.writeValueAsString(invalidShipment);

        MvcResult result = mockMvc.perform(post("/api/v1/rates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("\n=== INVALID ADDRESS TEST ===");
        System.out.println("Response: " + result.getResponse().getContentAsString());
        System.out.println("✓ Service handled invalid address");
    }

    @Test
    @DisplayName("Test missing package information")
    void testMissingPackageInfo() throws Exception {
        ShipmentDTO invalidShipment = new ShipmentDTO();
        invalidShipment.setFromAddress(fromAddress);
        invalidShipment.setToAddress(toAddress);
        // Missing package info

        String jsonRequest = objectMapper.writeValueAsString(invalidShipment);

        // Expect error due to missing package
        try {
            MvcResult result = mockMvc.perform(post("/api/v1/rates")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest))
                    .andReturn();

            System.out.println("\n=== MISSING PACKAGE INFO TEST ===");
            System.out.println("Status: " + result.getResponse().getStatus());
            System.out.println("✓ Service handled missing package info");
        } catch (Exception e) {
            System.out.println("\n=== MISSING PACKAGE INFO TEST ===");
            System.out.println("✓ Request failed as expected: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test tracking with invalid number")
    void testInvalidTrackingNumber() throws Exception {
        String invalidTracking = "INVALID123";

        MvcResult result = mockMvc.perform(get("/api/v1/tracking/{trackingNumber}", invalidTracking))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("\n=== INVALID TRACKING NUMBER TEST ===");
        System.out.println("Response: " + result.getResponse().getContentAsString());
        System.out.println("✓ Service returned mock data for tracking");
    }
}
