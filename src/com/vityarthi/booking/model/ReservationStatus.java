package com.vityarthi.booking.model;

/**
 * State machine representation of a reservation lifecycle.
 */
public enum ReservationStatus {
    PENDING,
    CONFIRMED,
    REJECTED,
    CANCELLED
}
