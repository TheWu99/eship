package com.thewu.eship.dto.shipping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Shipping rate from a carrier.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateDTO {
    
    private CarrierType carrier;
    
    private String service; // e.g., Ground, Express
    
    private Double rate; // shipping cost in USD
    
    private String currency = "USD";
    
    private Integer deliveryDays;
    
    private String carrierRateId;
}
