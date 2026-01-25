package com.thewu.eship.dto.shipping;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Shipping address model.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Street address is required")
    private String street1;

    private String street2;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Postal code is required")
    private String postalCode;

    private String country = "US";

    private String phone;

    @Email
    private String email;

    private AddressType addressType;

    private boolean validated = false;
}
