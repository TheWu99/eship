package com.thewu.eship.service.shipping;

import com.thewu.eship.dto.shipping.*;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Service for customs and international shipping documentation.
 */
@Service
public class CustomsService {
    
    /**
     * Generate customs form for international shipments.
     * 
     * @param shipment The shipment with customs information
     * @return Base64 encoded customs form
     */
    public String generateCustomsForm(ShipmentDTO shipment) {
        if (shipment.getCustoms() == null) {
            throw new IllegalArgumentException("Customs information is required for international shipments");
        }
        
        CustomsFormDTO customs = shipment.getCustoms();
        
        // TODO: In production, generate actual customs forms (CN22, CN23, etc.)
        StringBuilder form = new StringBuilder();
        form.append("CUSTOMS DECLARATION FORM\n\n");
        form.append("From: ").append(shipment.getFromAddress().getCountry()).append("\n");
        form.append("To: ").append(shipment.getToAddress().getCountry()).append("\n\n");
        form.append("Contents Type: ").append(customs.getContentsType()).append("\n");
        
        if (customs.getContentsExplanation() != null) {
            form.append("Explanation: ").append(customs.getContentsExplanation()).append("\n");
        }
        
        form.append("\nITEMS:\n");
        double totalValue = 0;
        double totalWeight = 0;
        
        for (CustomsItemDTO item : customs.getItems()) {
            form.append(String.format(
                "- %s (Qty: %d, Unit Value: $%.2f, Weight: %.2f lbs)\n",
                item.getDescription(),
                item.getQuantity(),
                item.getValue(),
                item.getWeight()
            ));
            
            if (item.getHsCode() != null) {
                form.append("  HS Code: ").append(item.getHsCode()).append("\n");
            }
            
            totalValue += item.getValue() * item.getQuantity();
            totalWeight += item.getWeight() * item.getQuantity();
        }
        
        form.append(String.format("\nTotal Value: $%.2f USD\n", totalValue));
        form.append(String.format("Total Weight: %.2f lbs\n\n", totalWeight));
        
        if (customs.getInvoiceNumber() != null) {
            form.append("Invoice Number: ").append(customs.getInvoiceNumber()).append("\n");
        }
        
        if (customs.getEelPfc() != null) {
            form.append("EEI/PFC: ").append(customs.getEelPfc()).append("\n");
        }
        
        form.append("\nCertification: ").append(customs.isCustomsCertify() ? "Yes" : "No").append("\n");
        form.append("Signer: ").append(customs.getCustomsSigner()).append("\n");
        
        return Base64.getEncoder().encodeToString(form.toString().getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Calculate total customs value.
     */
    public double calculateTotalValue(CustomsFormDTO customs) {
        return customs.getItems().stream()
            .mapToDouble(item -> item.getValue() * item.getQuantity())
            .sum();
    }
    
    /**
     * Check if shipment requires customs documentation.
     */
    public boolean requiresCustoms(ShipmentDTO shipment) {
        String fromCountry = shipment.getFromAddress().getCountry();
        String toCountry = shipment.getToAddress().getCountry();
        
        return !fromCountry.equals(toCountry);
    }
}
