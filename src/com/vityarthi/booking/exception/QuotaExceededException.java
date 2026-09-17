package com.vityarthi.booking.exception;

/**
 * Thrown when a user exceeds their allowed quota of active bookings.
 */
public class QuotaExceededException extends BookingException {
    public QuotaExceededException(String message) {
        super(message);
    }
}
