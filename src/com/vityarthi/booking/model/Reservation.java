package com.vityarthi.booking.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Entity representing a booking record for a resource by a user.
 */
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String reservationId;
    private final String userId;
    private final String resourceId;
    private final TimeSlot timeSlot;
    private final String purpose;
    private final int priorityWeight;
    private ReservationStatus status;
    private final LocalDateTime requestedAt;
    private String adminRemarks;

    public Reservation(String reservationId, String userId, String resourceId, 
                       TimeSlot timeSlot, String purpose, int priorityWeight) {
        this.reservationId = Objects.requireNonNull(reservationId, "Reservation ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.resourceId = Objects.requireNonNull(resourceId, "Resource ID cannot be null");
        this.timeSlot = Objects.requireNonNull(timeSlot, "Time slot cannot be null");
        this.purpose = Objects.requireNonNull(purpose, "Purpose cannot be null");
        this.priorityWeight = priorityWeight;
        this.status = ReservationStatus.CONFIRMED;
        this.requestedAt = LocalDateTime.now();
        this.adminRemarks = "System Auto-Approved";
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public String getPurpose() {
        return purpose;
    }

    public int getPriorityWeight() {
        return priorityWeight;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public String getAdminRemarks() {
        return adminRemarks;
    }

    public void setAdminRemarks(String adminRemarks) {
        this.adminRemarks = adminRemarks;
    }

    public boolean isActive() {
        return this.status == ReservationStatus.CONFIRMED || this.status == ReservationStatus.PENDING;
    }

    @Override
    public String toString() {
        return String.format("ResID: %s | User: %s | Resource: %s | Status: %s | Slot: %s | Purpose: %s",
                reservationId, userId, resourceId, status, timeSlot, purpose);
    }
}
