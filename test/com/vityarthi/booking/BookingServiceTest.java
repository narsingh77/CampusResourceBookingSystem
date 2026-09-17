package com.vityarthi.booking;

import com.vityarthi.booking.exception.*;
import com.vityarthi.booking.model.*;
import com.vityarthi.booking.service.BookingService;
import com.vityarthi.booking.service.ResourceService;
import com.vityarthi.booking.service.UserService;

import java.time.LocalDateTime;

public class BookingServiceTest {

    public static void runAllTests() {
        System.out.println("\n=======================================================");
        System.out.println(" RUNNING UNIT TESTS: BookingServiceTest");
        System.out.println("=======================================================");

        testSuccessfulBooking();
        testDoubleBookingThrowsConflictException();
        testQuotaExceededEnforcement();
        testCancellationAndSlotFreed();

        System.out.println(">> BookingServiceTest: ALL 4 SUITES PASSED (100% OK)");
    }

    private static void testSuccessfulBooking() {
        System.out.print("Test 1: Valid Booking Creation ... ");
        UserService us = new UserService();
        ResourceService rs = new ResourceService();
        BookingService bs = new BookingService(us, rs);

        us.registerUser(new FacultyUser("f1", "Dr. A", "a@vityarthi.ac.in", "CSE", "Prof"));
        rs.addResource(new Auditorium("AUD-1", "Audi One", "Block A", 500, true, true));

        LocalDateTime now = LocalDateTime.now().plusDays(10);
        TimeSlot slot = new TimeSlot(now, now.plusHours(2));

        try {
            Reservation r = bs.bookResource("f1", "AUD-1", slot, "Annual Lecture");
            if (r == null || r.getStatus() != ReservationStatus.CONFIRMED) {
                throw new RuntimeException("Assertion failed: Status must be CONFIRMED");
            }
            System.out.println("PASSED");
        } catch (Exception e) {
            throw new RuntimeException("Test 1 Failed: " + e.getMessage());
        }
    }

    private static void testDoubleBookingThrowsConflictException() {
        System.out.print("Test 2: Double Booking Conflict Detection ... ");
        UserService us = new UserService();
        ResourceService rs = new ResourceService();
        BookingService bs = new BookingService(us, rs);

        us.registerUser(new StudentClubUser("c1", "Club 1", "c1@vityarthi.ac.in", "CSE", "Club Alpha", "Advisor"));
        us.registerUser(new StudentClubUser("c2", "Club 2", "c2@vityarthi.ac.in", "ECE", "Club Beta", "Advisor"));
        rs.addResource(new ComputerLab("LAB-1", "Lab One", "Block B", 40, 40, "Linux", false));

        LocalDateTime now = LocalDateTime.now().plusDays(11);
        TimeSlot slot1 = new TimeSlot(now, now.plusHours(2));
        TimeSlot overlappingSlot = new TimeSlot(now.plusHours(1), now.plusHours(3)); // 1 hour overlap

        try {
            bs.bookResource("c1", "LAB-1", slot1, "Session 1");
        } catch (Exception e) {
            throw new RuntimeException("Initial booking failed: " + e.getMessage());
        }

        try {
            bs.bookResource("c2", "LAB-1", overlappingSlot, "Session 2");
            throw new RuntimeException("Test 2 Failed: SlotConflictException was expected but not thrown!");
        } catch (SlotConflictException e) {
            System.out.println("PASSED (Correctly caught SlotConflictException)");
        } catch (Exception e) {
            throw new RuntimeException("Wrong exception thrown: " + e.getClass().getName());
        }
    }

    private static void testQuotaExceededEnforcement() {
        System.out.print("Test 3: Quota Limit Enforcement (Max 3 for Clubs) ... ");
        UserService us = new UserService();
        ResourceService rs = new ResourceService();
        BookingService bs = new BookingService(us, rs);

        us.registerUser(new StudentClubUser("c_quota", "Quota Club", "q@vityarthi.ac.in", "IT", "Quota Club", "Adv"));
        rs.addResource(new ConferenceRoom("CONF-1", "Room 1", "Admin", 20, true, true));

        LocalDateTime base = LocalDateTime.now().plusDays(15);
        try {
            bs.bookResource("c_quota", "CONF-1", new TimeSlot(base, base.plusHours(1)), "Slot 1");
            bs.bookResource("c_quota", "CONF-1", new TimeSlot(base.plusHours(2), base.plusHours(3)), "Slot 2");
            bs.bookResource("c_quota", "CONF-1", new TimeSlot(base.plusHours(4), base.plusHours(5)), "Slot 3");
        } catch (Exception e) {
            throw new RuntimeException("Setup for Test 3 failed: " + e.getMessage());
        }

        try {
            bs.bookResource("c_quota", "CONF-1", new TimeSlot(base.plusHours(6), base.plusHours(7)), "Slot 4");
            throw new RuntimeException("Test 3 Failed: QuotaExceededException was expected but not thrown!");
        } catch (QuotaExceededException e) {
            System.out.println("PASSED (Correctly caught QuotaExceededException)");
        } catch (Exception e) {
            throw new RuntimeException("Wrong exception thrown: " + e.getClass().getName());
        }
    }

    private static void testCancellationAndSlotFreed() {
        System.out.print("Test 4: Cancellation and Slot Re-availability ... ");
        UserService us = new UserService();
        ResourceService rs = new ResourceService();
        BookingService bs = new BookingService(us, rs);

        us.registerUser(new FacultyUser("f_cancel", "Dr. Cancel", "c@v.in", "CSE", "Prof"));
        rs.addResource(new Auditorium("AUD-FREE", "Free Audi", "Block C", 300, true, true));

        LocalDateTime now = LocalDateTime.now().plusDays(20);
        TimeSlot slot = new TimeSlot(now, now.plusHours(2));

        try {
            Reservation r = bs.bookResource("f_cancel", "AUD-FREE", slot, "Temp Booking");
            if (bs.isAvailable("AUD-FREE", slot)) {
                throw new RuntimeException("Assertion failed: Slot should not be available while reserved");
            }

            bs.cancelReservation(r.getReservationId(), "f_cancel");
            if (!bs.isAvailable("AUD-FREE", slot)) {
                throw new RuntimeException("Assertion failed: Slot should be available after cancellation");
            }
            System.out.println("PASSED");
        } catch (Exception e) {
            throw new RuntimeException("Test 4 Failed: " + e.getMessage());
        }
    }
}
