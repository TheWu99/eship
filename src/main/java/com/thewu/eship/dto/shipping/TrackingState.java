package com.thewu.eship.dto.shipping;

/**
 * Standardized tracking states across all carriers.
 */
public enum TrackingState {
    PRE_TRANSIT,
    IN_TRANSIT,
    OUT_FOR_DELIVERY,
    DELIVERED,
    RETURNED,
    FAILED,
    CANCELLED,
    EXCEPTION,
    MANIFEST,
    UNKNOWN
}
