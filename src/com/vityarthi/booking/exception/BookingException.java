package com.vityarthi.booking.exception;

/**
 * Base checked exception for reservation domain operations.
 */
public class BookingException extends Exception {
    public BookingException(String message) {
        super(message);
    }

    public BookingException(String message, Throwable cause) {
        super(message, cause);
    }
}
