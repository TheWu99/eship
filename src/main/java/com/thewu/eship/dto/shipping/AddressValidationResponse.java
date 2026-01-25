package com.thewu.eship.dto.shipping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for address validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressValidationResponse {
    
    private AddressDTO address;
    
    private boolean valid;
    
    private AddressType addressType;
}
