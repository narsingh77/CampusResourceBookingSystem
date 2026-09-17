package com.vityarthi.booking.model;

/**
 * Enumeration representing user access roles, priority weighting, and booking quotas.
 */
public enum UserRole {
    ADMIN(100, Integer.MAX_VALUE),
    FACULTY(2, 10),
    STUDENT_CLUB(1, 3);

    private final int priorityLevel;
    private final int maxConcurrentBookings;

    UserRole(int priorityLevel, int maxConcurrentBookings) {
        this.priorityLevel = priorityLevel;
        this.maxConcurrentBookings = maxConcurrentBookings;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }

    public int getMaxConcurrentBookings() {
        return maxConcurrentBookings;
    }
}
