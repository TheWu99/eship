package com.thewu.eship.dto.shipping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Complete tracking information for a shipment.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentTrackingDTO {

    private String trackingNumber;

    private CarrierType carrier;

    private TrackingState currentStatus;

    private List<TrackingEventDTO> events = new ArrayList<>();

    private LocalDateTime estimatedDelivery;

    private LocalDateTime actualDelivery;
}
