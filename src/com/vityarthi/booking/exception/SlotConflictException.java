package com.vityarthi.booking.exception;

/**
 * Thrown when attempting to book a slot that clashes with an existing confirmed reservation.
 */
public class SlotConflictException extends BookingException {
    public SlotConflictException(String message) {
        super(message);
    }
}
