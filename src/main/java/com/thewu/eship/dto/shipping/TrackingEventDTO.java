package com.thewu.eship.dto.shipping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Individual tracking event.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingEventDTO {

    private LocalDateTime timestamp;

    private TrackingState status;

    private String message;

    private String location;

    private String carrierStatus; // original carrier status code
}
