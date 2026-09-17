package com.vityarthi.booking.model;

/**
 * Concrete class representing student club coordinators.
 */
public class StudentClubUser extends User {
    private static final long serialVersionUID = 1L;
    private final String clubName;
    private final String facultyAdvisorName;

    public StudentClubUser(String userId, String fullName, String email, String department, 
                           String clubName, String facultyAdvisorName) {
        super(userId, fullName, email, department, UserRole.STUDENT_CLUB);
        this.clubName = clubName;
        this.facultyAdvisorName = facultyAdvisorName;
    }

    public String getClubName() {
        return clubName;
    }

    public String getFacultyAdvisorName() {
        return facultyAdvisorName;
    }

    @Override
    public boolean canApproveReservations() {
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " | Club: " + clubName + " (Advisor: " + facultyAdvisorName + ")";
    }
}
