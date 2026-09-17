package com.vityarthi.booking.model;

/**
 * Concrete class representing university faculty members.
 */
public class FacultyUser extends User {
    private static final long serialVersionUID = 1L;
    private final String designation;

    public FacultyUser(String userId, String fullName, String email, String department, String designation) {
        super(userId, fullName, email, department, UserRole.FACULTY);
        this.designation = designation;
    }

    public String getDesignation() {
        return designation;
    }

    @Override
    public boolean canApproveReservations() {
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " | Designation: " + designation;
    }
}
