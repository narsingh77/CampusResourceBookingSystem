package com.vityarthi.booking.util;

import com.vityarthi.booking.exception.BookingException;
import com.vityarthi.booking.model.*;
import com.vityarthi.booking.service.BookingService;
import com.vityarthi.booking.service.ResourceService;
import com.vityarthi.booking.service.UserService;

import java.time.LocalDateTime;

/**
 * Seeds realistic campus data for users, facilities, and initial bookings.
 */
public class DemoDataSeeder {

    public static void seed(UserService userService, ResourceService resourceService, BookingService bookingService) {
        // 1. Seed Users
        AdminUser admin = new AdminUser("admin01", "Dr. Rajesh Sharma", "admin.sharma@vityarthi.ac.in", "Campus Administration");
        FacultyUser faculty1 = new FacultyUser("fac_cse_01", "Prof. Ananya Sen", "ananya.sen@vityarthi.ac.in", "Computer Science", "Associate Professor");
        FacultyUser faculty2 = new FacultyUser("fac_ece_02", "Prof. Vikram Malhotra", "vikram.m@vityarthi.ac.in", "Electronics & Comm", "Professor");
        StudentClubUser club1 = new StudentClubUser("club_gdsc", "Rohan Mehta", "rohan.gdsc@vityarthi.ac.in", "CSE", "Google Developer Club", "Prof. Ananya Sen");
        StudentClubUser club2 = new StudentClubUser("club_robotics", "Pooja Verma", "pooja.robotics@vityarthi.ac.in", "Mechanical", "Robotics & AI Club", "Prof. Vikram Malhotra");

        userService.registerUser(admin);
        userService.registerUser(faculty1);
        userService.registerUser(faculty2);
        userService.registerUser(club1);
        userService.registerUser(club2);

        // 2. Seed Facilities & Equipment
        Auditorium audi1 = new Auditorium("AUD-101", "Sir Visvesvaraya Grand Auditorium", "Main Tech Block", 800, true, true);
        Auditorium audi2 = new Auditorium("AUD-102", "Kalam Mini Auditorium", "Academic Block 2", 250, true, true);
        ComputerLab lab1 = new ComputerLab("LAB-CS-301", "Advanced AI & Cloud Computing Lab", "IT Tower 3rd Floor", 60, 60, "Ubuntu Linux 24.04", true);
        ComputerLab lab2 = new ComputerLab("LAB-CS-204", "General Software Engineering Lab", "IT Tower 2nd Floor", 75, 75, "Windows 11 Enterprise", false);
        ConferenceRoom conf1 = new ConferenceRoom("CONF-DEAN-1", "Senate Boardroom", "Administrative Wing", 30, true, true);

        resourceService.addResource(audi1);
        resourceService.addResource(audi2);
        resourceService.addResource(lab1);
        resourceService.addResource(lab2);
        resourceService.addResource(conf1);

        // 3. Seed Initial Demo Bookings
        try {
            LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
            TimeSlot slot1 = new TimeSlot(tomorrow, tomorrow.plusHours(2));
            bookingService.bookResource("fac_cse_01", "AUD-101", slot1, "Guest Lecture on Distributed Systems");

            LocalDateTime dayAfter = LocalDateTime.now().plusDays(2).withHour(14).withMinute(0).withSecond(0).withNano(0);
            TimeSlot slot2 = new TimeSlot(dayAfter, dayAfter.plusHours(3));
            bookingService.bookResource("club_gdsc", "LAB-CS-301", slot2, "Hands-on Cloud Kubernetes Hackathon");

        } catch (BookingException e) {
            System.err.println("Seeding notice: " + e.getMessage());
        }
    }
}
