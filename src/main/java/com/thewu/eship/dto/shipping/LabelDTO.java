package com.thewu.eship.dto.shipping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Shipping label.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabelDTO {
    
    private String trackingNumber;
    
    private CarrierType carrier;
    
    private LabelFormat format;
    
    private String content; // Base64 encoded label content
    
    private LocalDateTime createdAt = LocalDateTime.now();
}
