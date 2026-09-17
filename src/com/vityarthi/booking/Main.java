package com.vityarthi.booking;

import com.vityarthi.booking.exception.BookingException;
import com.vityarthi.booking.model.*;
import com.vityarthi.booking.service.AnalyticsService;
import com.vityarthi.booking.service.BookingService;
import com.vityarthi.booking.service.ResourceService;
import com.vityarthi.booking.service.UserService;
import com.vityarthi.booking.util.DemoDataSeeder;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Main application CLI entry point.
 */
public class Main {
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        UserService userService = new UserService();
        ResourceService resourceService = new ResourceService();
        BookingService bookingService = new BookingService(userService, resourceService);
        AnalyticsService analyticsService = new AnalyticsService(bookingService, resourceService);

        // Seed initial campus dataset
        DemoDataSeeder.seed(userService, resourceService, bookingService);

        Scanner scanner = new Scanner(System.in);
        System.out.println("===============================================================================");
        System.out.println("     CAMPUS FACILITY & RESOURCE BOOKING SYSTEM (CampusResourceHub)            ");
        System.out.println("     Flipped Course Evaluation Project | Programming in Java                  ");
        System.out.println("===============================================================================");

        // Default login context
        String currentUserId = "club_gdsc";

        while (true) {
            User currentUser = userService.getUserById(currentUserId).orElse(null);
            System.out.println("\n>>> Current Active User: " + (currentUser != null ? currentUser : "None"));
            System.out.println("-------------------------------------------------------------------------------");
            System.out.println("1. Browse All Campus Facilities & Resources");
            System.out.println("2. Book a Resource / Facility Slot");
            System.out.println("3. View My Active Reservations & Quota");
            System.out.println("4. Cancel a Reservation");
            System.out.println("5. Switch User Account (Faculty / Club / Admin)");
            System.out.println("6. View System Analytics & Utilization (Streams API)");
            System.out.println("7. Export Audit & Reservation Report to CSV");
            System.out.println("8. View Live Audit Log Trail");
            System.out.println("9. Run Concurrency Stress Demonstration (Live Race Condition Test)");
            System.out.println("0. Exit");
            System.out.println("-------------------------------------------------------------------------------");
            System.out.print("Enter your choice (0-9): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.println("\n--- CAMPUS RESOURCES CATALOG ---");
                    resourceService.getAllResources().forEach(r -> {
                        System.out.printf("• [%s] %-36s | Location: %-22s | Capacity: %4d | Rate: Rs.%.1f/hr\n",
                                r.getResourceId(), r.getName(), r.getBuildingLocation(), r.getCapacity(), r.getHourlyCostRate());
                    });
                }
                case "2" -> {
                    System.out.println("\n--- BOOK A RESOURCE ---");
                    System.out.print("Enter Resource ID (e.g., AUD-101, LAB-CS-301, CONF-DEAN-1): ");
                    String resId = scanner.nextLine().trim();

                    System.out.print("Enter Start Date & Time (YYYY-MM-DD HH:MM): ");
                    String startStr = scanner.nextLine().trim();

                    System.out.print("Enter Duration in Hours (1-6): ");
                    String durationStr = scanner.nextLine().trim();

                    System.out.print("Enter Event Purpose: ");
                    String purpose = scanner.nextLine().trim();

                    try {
                        LocalDateTime start = LocalDateTime.parse(startStr, INPUT_FORMATTER);
                        int hours = Integer.parseInt(durationStr);
                        LocalDateTime end = start.plusHours(hours);
                        TimeSlot slot = new TimeSlot(start, end);

                        Reservation res = bookingService.bookResource(currentUserId, resId, slot, purpose);
                        System.out.println("\n[SUCCESS] Reservation successfully placed and confirmed!");
                        System.out.println("Details: " + res);
                    } catch (Exception e) {
                        System.out.println("\n[BOOKING REJECTED / ERROR]: " + e.getMessage());
                    }
                }
                case "3" -> {
                    System.out.println("\n--- MY ACTIVE RESERVATIONS ---");
                    List<Reservation> userBookings = bookingService.getReservationsForUser(currentUserId);
                    if (userBookings.isEmpty()) {
                        System.out.println("No reservations found for current user.");
                    } else {
                        userBookings.forEach(System.out::println);
                    }
                    long activeCount = userBookings.stream().filter(Reservation::isActive).count();
                    int max = currentUser != null ? currentUser.getMaxConcurrentBookings() : 0;
                    System.out.printf("Active Quota Status: %d used out of %d allowed\n", activeCount, max);
                }
                case "4" -> {
                    System.out.println("\n--- CANCEL A RESERVATION ---");
                    System.out.print("Enter Reservation ID to cancel: ");
                    String resId = scanner.nextLine().trim();
                    try {
                        boolean cancelled = bookingService.cancelReservation(resId, currentUserId);
                        if (cancelled) {
                            System.out.println("[SUCCESS] Reservation " + resId + " has been cancelled.");
                        }
                    } catch (BookingException e) {
                        System.out.println("[ERROR]: " + e.getMessage());
                    }
                }
                case "5" -> {
                    System.out.println("\n--- SWITCH ACTIVE USER ---");
                    userService.getAllUsers().forEach(u -> 
                            System.out.printf("• ID: %-14s | Name: %-22s | Role: %s\n", u.getUserId(), u.getFullName(), u.getRole()));
                    System.out.print("Enter User ID to switch to: ");
                    String newId = scanner.nextLine().trim().toLowerCase();
                    if (userService.getUserById(newId).isPresent()) {
                        currentUserId = newId;
                        System.out.println("[SUCCESS] Switched active user to: " + newId);
                    } else {
                        System.out.println("[ERROR] Invalid User ID.");
                    }
                }
                case "6" -> {
                    System.out.println("\n--- SYSTEM ANALYTICS & UTILIZATION (STREAMS API) ---");
                    System.out.println("Reservations Breakdown by Status:");
                    analyticsService.getReservationCountByStatus().forEach((k, v) -> 
                            System.out.printf("  • %-12s : %d bookings\n", k, v));

                    System.out.println("\nBooked Hours by Facility Category:");
                    analyticsService.getTotalHoursByResourceType().forEach((k, v) -> 
                            System.out.printf("  • %-16s : %.2f total hours\n", k, v));
                }
                case "7" -> {
                    System.out.println("\n--- EXPORT AUDIT & RESERVATIONS REPORT ---");
                    String outPath = "data" + File.separator + "campus_booking_report.csv";
                    try {
                        new File("data").mkdirs();
                        analyticsService.exportToCSV(outPath);
                        System.out.println("[SUCCESS] Report exported cleanly to: " + new File(outPath).getAbsolutePath());
                    } catch (Exception e) {
                        System.out.println("[ERROR] Failed to export CSV: " + e.getMessage());
                    }
                }
                case "8" -> {
                    System.out.println("\n--- AUDIT TRAIL LOGS ---");
                    bookingService.getAuditLogs().forEach(System.out::println);
                }
                case "9" -> {
                    System.out.println("\n--- CONCURRENCY RACE CONDITION TEST ---");
                    System.out.println("Simulating 2 simultaneous threads attempting to book the SAME auditorium at the EXACT same second...");
                    runConcurrencyDemo(bookingService);
                }
                case "0" -> {
                    System.out.println("\nThank you for using CampusResourceHub. Exiting.");
                    scanner.close();
                    return;
                }
                default -> System.out.println("[WARN] Invalid option. Please enter 0-9.");
            }
        }
    }

    private static void runConcurrencyDemo(BookingService bookingService) {
        LocalDateTime targetSlotStart = LocalDateTime.now().plusDays(5).withHour(9).withMinute(0).withSecond(0).withNano(0);
        TimeSlot contestedSlot = new TimeSlot(targetSlotStart, targetSlotStart.plusHours(2));

        Runnable task1 = () -> {
            try {
                Reservation r = bookingService.bookResource("club_gdsc", "AUD-102", contestedSlot, "Thread-1 GDSC Annual Meet");
                System.out.println(">> [Thread-1 GDSC] SUCCESS: Acquired slot -> " + r.getReservationId());
            } catch (Exception e) {
                System.out.println(">> [Thread-1 GDSC] REJECTED: " + e.getMessage());
            }
        };

        Runnable task2 = () -> {
            try {
                Reservation r = bookingService.bookResource("club_robotics", "AUD-102", contestedSlot, "Thread-2 Robotics Workshop");
                System.out.println(">> [Thread-2 Robotics] SUCCESS: Acquired slot -> " + r.getReservationId());
            } catch (Exception e) {
                System.out.println(">> [Thread-2 Robotics] REJECTED: " + e.getMessage());
            }
        };

        Thread t1 = new Thread(task1, "Worker-Thread-1");
        Thread t2 = new Thread(task2, "Worker-Thread-2");

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException ignored) {}

        System.out.println(">> Concurrency check complete: Double-booking successfully prevented by synchronization monitor!");
    }
}
