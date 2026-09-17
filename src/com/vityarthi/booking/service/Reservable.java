package com.vityarthi.booking.service;

import com.vityarthi.booking.exception.BookingException;
import com.vityarthi.booking.model.Reservation;
import com.vityarthi.booking.model.TimeSlot;

import java.util.List;

/**
 * Interface defining contract for reservation actions.
 * Demonstrates Interface Abstraction.
 */
public interface Reservable {
    Reservation bookResource(String userId, String resourceId, TimeSlot slot, String purpose) 
            throws BookingException;

    boolean cancelReservation(String reservationId, String userId) 
            throws BookingException;

    List<Reservation> getReservationsForResource(String resourceId);

    List<Reservation> getReservationsForUser(String userId);

    boolean isAvailable(String resourceId, TimeSlot slot);
}
