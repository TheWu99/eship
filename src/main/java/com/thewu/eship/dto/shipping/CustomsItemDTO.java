package com.thewu.eship.dto.shipping;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Item for customs declaration.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomsItemDTO {

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull
    @Positive
    private Double value; // value per unit in USD

    @NotNull
    @Positive
    private Double weight; // weight per unit in pounds

    private String hsCode; // Harmonized System code

    private String originCountry = "US";
}
