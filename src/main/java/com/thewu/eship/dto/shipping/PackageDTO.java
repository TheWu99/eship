package com.thewu.eship.dto.shipping;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Package dimensions and weight.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageDTO {
    
    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private Double weight; // in pounds
    
    @NotNull(message = "Length is required")
    @Positive(message = "Length must be positive")
    private Double length; // in inches
    
    @NotNull(message = "Width is required")
    @Positive(message = "Width must be positive")
    private Double width; // in inches
    
    @NotNull(message = "Height is required")
    @Positive(message = "Height must be positive")
    private Double height; // in inches
    
    @Positive
    private Double value; // declared value in USD
    
    private String description;
}
