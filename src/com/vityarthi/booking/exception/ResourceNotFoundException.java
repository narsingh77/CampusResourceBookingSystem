package com.vityarthi.booking.exception;

/**
 * Thrown when requested facility or resource is not found in registry.
 */
public class ResourceNotFoundException extends BookingException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
