package com.thewu.eship.controller.shipping;

import com.thewu.eship.dto.shipping.AddressDTO;
import com.thewu.eship.dto.shipping.AddressType;
import com.thewu.eship.dto.shipping.AddressValidationResponse;
import com.thewu.eship.service.shipping.AddressValidationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for address validation and classification.
 */
@RestController
@RequestMapping("/api/v1/address")
@CrossOrigin(origins = "*")
public class AddressController {

    @Autowired
    private AddressValidationService addressService;

    /**
     * Validate and standardize an address.
     * 
     * @param address The address to validate
     * @return Validated address with status
     */
    @PostMapping("/validate")
    public ResponseEntity<AddressValidationResponse> validateAddress(@Valid @RequestBody AddressDTO address) {
        try {
            AddressValidationResponse response = addressService.validateAddress(address);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error validating address: " + e.getMessage(), e);
        }
    }

    /**
     * Classify an address as residential or commercial.
     * 
     * @param address The address to classify
     * @return Address type and confidence
     */
    @PostMapping("/classify")
    public ResponseEntity<Map<String, Object>> classifyAddress(@Valid @RequestBody AddressDTO address) {
        try {
            AddressType addressType = addressService.detectAddressType(address);

            Map<String, Object> response = new HashMap<>();
            response.put("address_type", addressType);
            response.put("confidence", "medium"); // In production, this would be based on data sources

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error classifying address: " + e.getMessage(), e);
        }
    }
}
