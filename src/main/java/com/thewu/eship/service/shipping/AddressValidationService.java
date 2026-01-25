package com.thewu.eship.service.shipping;

import com.thewu.eship.dto.shipping.*;
import org.springframework.stereotype.Service;

/**
 * Service for address validation and classification.
 */
@Service
public class AddressValidationService {
    
    /**
     * Validate and standardize an address.
     * 
     * @param address The address to validate
     * @return Validated address and validation status
     */
    public AddressValidationResponse validateAddress(AddressDTO address) {
        // TODO: In production, integrate with USPS, Google, or other address validation APIs
        // For now, implementing basic validation
        
        boolean isValid = true;
        
        // Basic validation checks
        if (address.getStreet1() == null || address.getStreet1().trim().isEmpty()) {
            isValid = false;
        }
        if (address.getCity() == null || address.getCity().trim().isEmpty()) {
            isValid = false;
        }
        if (address.getState() == null || address.getState().trim().isEmpty()) {
            isValid = false;
        }
        if (address.getPostalCode() == null || address.getPostalCode().trim().isEmpty()) {
            isValid = false;
        }
        
        // Standardize state code to uppercase
        if (address.getState() != null) {
            address.setState(address.getState().toUpperCase());
        }
        
        // Standardize postal code format for US
        if ("US".equals(address.getCountry()) && address.getPostalCode() != null) {
            String postal = address.getPostalCode().replaceAll("[^0-9]", "");
            if (postal.length() == 5) {
                address.setPostalCode(postal);
            } else if (postal.length() == 9) {
                address.setPostalCode(postal.substring(0, 5) + "-" + postal.substring(5));
            }
        }
        
        address.setValidated(isValid);
        
        // Detect address type
        AddressType addressType = detectAddressType(address);
        address.setAddressType(addressType);
        
        AddressValidationResponse response = new AddressValidationResponse();
        response.setAddress(address);
        response.setValid(isValid);
        response.setAddressType(addressType);
        
        return response;
    }
    
    /**
     * Detect whether an address is residential or commercial.
     * 
     * @param address The address to classify
     * @return Address type
     */
    public AddressType detectAddressType(AddressDTO address) {
        // TODO: In production, use carrier APIs or commercial databases
        // For now, using simple heuristics
        
        String name = address.getName() != null ? address.getName().toLowerCase() : "";
        String street = address.getStreet1() != null ? address.getStreet1().toLowerCase() : "";
        String street2 = address.getStreet2() != null ? address.getStreet2().toLowerCase() : "";
        
        // Commercial indicators
        String[] commercialKeywords = {
            "inc", "llc", "corp", "company", "co.", "ltd",
            "warehouse", "office", "suite", "floor", "building",
            "plaza", "center", "mall", "store", "shop"
        };
        
        for (String keyword : commercialKeywords) {
            if (name.contains(keyword) || street.contains(keyword) || street2.contains(keyword)) {
                return AddressType.COMMERCIAL;
            }
        }
        
        // Residential indicators
        String[] residentialKeywords = {
            "apt", "apartment", "unit", "#", "condo"
        };
        
        for (String keyword : residentialKeywords) {
            if (street.contains(keyword) || street2.contains(keyword)) {
                return AddressType.RESIDENTIAL;
            }
        }
        
        // Default to residential if uncertain
        return AddressType.RESIDENTIAL;
    }
}
