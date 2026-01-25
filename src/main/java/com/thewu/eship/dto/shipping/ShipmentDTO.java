package com.thewu.eship.dto.shipping;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Complete shipment request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentDTO {
    
    private String id;
    
    @NotNull(message = "From address is required")
    @Valid
    private AddressDTO fromAddress;
    
    @NotNull(message = "To address is required")
    @Valid
    private AddressDTO toAddress;
    
    @NotNull(message = "Package information is required")
    @Valid
    private PackageDTO packageInfo;
    
    private CarrierType carrier;
    
    private String service;
    
    private LabelFormat labelFormat = LabelFormat.PDF;
    
    @Valid
    private CustomsFormDTO customs;
    
    private String reference; // customer reference number
    
    private LocalDateTime createdAt = LocalDateTime.now();
}
