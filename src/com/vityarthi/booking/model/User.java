package com.vityarthi.booking.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Abstract base class representing a campus user.
 * Demonstrates Abstraction and Encapsulation.
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String userId;
    private final String fullName;
    private final String email;
    private final String department;
    private final UserRole role;

    public User(String userId, String fullName, String email, String department, UserRole role) {
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.fullName = Objects.requireNonNull(fullName, "Full name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.department = Objects.requireNonNull(department, "Department cannot be null");
        this.role = Objects.requireNonNull(role, "User role cannot be null");
    }

    public String getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getDepartment() {
        return department;
    }

    public UserRole getRole() {
        return role;
    }

    public int getMaxConcurrentBookings() {
        return role.getMaxConcurrentBookings();
    }

    public abstract boolean canApproveReservations();

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - Dept: %s", role, fullName, userId, department);
    }
}
