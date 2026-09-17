package com.vityarthi.booking.model;

/**
 * Concrete class representing campus system administrators.
 */
public class AdminUser extends User {
    private static final long serialVersionUID = 1L;

    public AdminUser(String userId, String fullName, String email, String department) {
        super(userId, fullName, email, department, UserRole.ADMIN);
    }

    @Override
    public boolean canApproveReservations() {
        return true;
    }
}
