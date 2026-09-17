package com.vityarthi.booking;

import com.vityarthi.booking.model.*;
import com.vityarthi.booking.service.BookingService;
import com.vityarthi.booking.service.ResourceService;
import com.vityarthi.booking.service.UserService;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrencyBookingTest {

    public static void runAllTests() {
        System.out.println("\n=======================================================");
        System.out.println(" RUNNING CONCURRENCY TEST: Multi-Threaded Contention");
        System.out.println("=======================================================");

        testConcurrentBookingRaceCondition();
        System.out.println(">> ConcurrencyBookingTest: PASSED (Thread-Safe Isolation Confirmed)");
    }

    private static void testConcurrentBookingRaceCondition() {
        System.out.print("Test: 2 Threads simultaneously booking exact same slot ... ");

        UserService us = new UserService();
        ResourceService rs = new ResourceService();
        BookingService bs = new BookingService(us, rs);

        us.registerUser(new StudentClubUser("clubA", "Club A", "a@v.in", "CSE", "Alpha", "Adv"));
        us.registerUser(new StudentClubUser("clubB", "Club B", "b@v.in", "ECE", "Beta", "Adv"));
        rs.addResource(new Auditorium("AUD-RACE", "Race Audi", "Main Block", 400, true, true));

        LocalDateTime slotStart = LocalDateTime.now().plusDays(30);
        TimeSlot sharedSlot = new TimeSlot(slotStart, slotStart.plusHours(2));

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);

        CountDownLatch readyGate = new CountDownLatch(1);
        CountDownLatch finishGate = new CountDownLatch(2);

        Runnable workerA = () -> {
            try {
                readyGate.await();
                bs.bookResource("clubA", "AUD-RACE", sharedSlot, "Club A Event");
                successCount.incrementAndGet();
            } catch (Exception e) {
                conflictCount.incrementAndGet();
            } finally {
                finishGate.countDown();
            }
        };

        Runnable workerB = () -> {
            try {
                readyGate.await();
                bs.bookResource("clubB", "AUD-RACE", sharedSlot, "Club B Event");
                successCount.incrementAndGet();
            } catch (Exception e) {
                conflictCount.incrementAndGet();
            } finally {
                finishGate.countDown();
            }
        };

        new Thread(workerA).start();
        new Thread(workerB).start();

        readyGate.countDown();

        try {
            finishGate.await();
        } catch (InterruptedException ignored) {}

        if (successCount.get() != 1 || conflictCount.get() != 1) {
            throw new RuntimeException("Assertion error! Expected 1 success and 1 conflict, but got successes=" 
                    + successCount.get() + ", conflicts=" + conflictCount.get());
        }

        System.out.println("PASSED (Successes: " + successCount.get() + ", Conflicts: " + conflictCount.get() + ")");
    }
}
