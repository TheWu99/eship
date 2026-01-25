package com.thewu.eship.dto.shipping;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Customs declaration form.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomsFormDTO {

    private String contentsType = "merchandise";

    private String contentsExplanation;

    private boolean customsCertify = true;

    @NotBlank(message = "Customs signer is required")
    private String customsSigner;

    @NotEmpty(message = "At least one customs item is required")
    private List<CustomsItemDTO> items;

    private String eelPfc; // EEI/PFC code

    private String invoiceNumber;
}
