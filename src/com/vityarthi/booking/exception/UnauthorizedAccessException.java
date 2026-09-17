package com.vityarthi.booking.exception;

/**
 * Thrown when user lacks privileges to perform an action.
 */
public class UnauthorizedAccessException extends BookingException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
