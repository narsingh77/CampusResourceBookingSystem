package com.vityarthi.booking.service;

import com.vityarthi.booking.exception.*;
import com.vityarthi.booking.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Core business engine coordinating bookings, thread-safe conflict detection,
 * quota validation, priority resolution, and audit tracking.
 */
public class BookingService implements Reservable, Auditable {
    private final UserService userService;
    private final ResourceService resourceService;

    // Concurrent thread-safe collections
    private final Map<String, Reservation> reservationRegistry = new ConcurrentHashMap<>();
    private final Map<String, Object> resourceLocks = new ConcurrentHashMap<>();
    private final List<String> auditLogs = new CopyOnWriteArrayList<>();
    private final AtomicInteger sequenceGenerator = new AtomicInteger(1000);

    public BookingService(UserService userService, ResourceService resourceService) {
        this.userService = Objects.requireNonNull(userService, "UserService cannot be null");
        this.resourceService = Objects.requireNonNull(resourceService, "ResourceService cannot be null");
    }

    private Object getLockForResource(String resourceId) {
        return resourceLocks.computeIfAbsent(resourceId.toUpperCase(), k -> new Object());
    }

    @Override
    public Reservation bookResource(String userId, String resourceId, TimeSlot slot, String purpose) 
            throws BookingException {
        
        // 1. Validate User
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new BookingException("User not found with ID: " + userId));

        // 2. Validate Resource
        CampusResource resource = resourceService.getResource(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with ID: " + resourceId));

        if (!resource.isOperational()) {
            throw new BookingException("Resource is currently out of order for maintenance.");
        }

        // 3. Enforce User Active Quota
        long activeBookingsCount = getReservationsForUser(userId).stream()
                .filter(Reservation::isActive)
                .count();

        if (activeBookingsCount >= user.getMaxConcurrentBookings()) {
            throw new QuotaExceededException(String.format(
                    "User %s has reached quota limit (%d/%d active bookings).",
                    user.getFullName(), activeBookingsCount, user.getMaxConcurrentBookings()));
        }

        // 4. Thread-Safe Slot Check and Allocation
        Object lock = getLockForResource(resourceId);
        synchronized (lock) {
            List<Reservation> clashes = reservationRegistry.values().stream()
                    .filter(r -> r.getResourceId().equalsIgnoreCase(resourceId))
                    .filter(Reservation::isActive)
                    .filter(r -> r.getTimeSlot().overlapsWith(slot))
                    .toList();

            if (!clashes.isEmpty()) {
                Reservation existing = clashes.get(0);
                User existingUser = userService.getUserById(existing.getUserId()).orElse(null);

                int incomingPriority = user.getRole().getPriorityLevel();
                int existingPriority = (existingUser != null) ? existingUser.getRole().getPriorityLevel() : 0;

                if (incomingPriority > existingPriority && user.getRole() == UserRole.FACULTY) {
                    // Preemption mechanism: Faculty overrides lower-priority student club reservation
                    existing.setStatus(ReservationStatus.CANCELLED);
                    existing.setAdminRemarks("Preempted by Faculty Reservation (" + user.getFullName() + ")");
                    logAction("PREEMPTION", user.getUserId(), 
                            "Preempted booking " + existing.getReservationId() + " of user " + existing.getUserId());
                } else {
                    throw new SlotConflictException(String.format(
                            "CONFLICT: Slot %s is already reserved by Booking #%s (User: %s).",
                            slot, existing.getReservationId(), existing.getUserId()));
                }
            }

            // Create new confirmed reservation
            String resId = "RES-" + sequenceGenerator.incrementAndGet();
            Reservation reservation = new Reservation(resId, user.getUserId(), resource.getResourceId(),
                    slot, purpose, user.getRole().getPriorityLevel());

            reservationRegistry.put(resId, reservation);
            logAction("BOOKING_CREATED", user.getUserId(), 
                    "Booked " + resource.getName() + " for slot " + slot);

            return reservation;
        }
    }

    @Override
    public boolean cancelReservation(String reservationId, String userId) throws BookingException {
        Reservation reservation = reservationRegistry.get(reservationId);
        if (reservation == null) {
            throw new BookingException("Reservation not found: " + reservationId);
        }

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new BookingException("User not found: " + userId));

        // Only reservation owner or ADMIN can cancel
        if (!reservation.getUserId().equalsIgnoreCase(userId) && user.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedAccessException("You are not authorized to cancel this reservation.");
        }

        Object lock = getLockForResource(reservation.getResourceId());
        synchronized (lock) {
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservation.setAdminRemarks("Cancelled by " + user.getFullName());
            logAction("BOOKING_CANCELLED", userId, "Cancelled booking " + reservationId);
            return true;
        }
    }

    @Override
    public List<Reservation> getReservationsForResource(String resourceId) {
        return reservationRegistry.values().stream()
                .filter(r -> r.getResourceId().equalsIgnoreCase(resourceId))
                .sorted(Comparator.comparing(r -> r.getTimeSlot().getStartTime()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> getReservationsForUser(String userId) {
        return reservationRegistry.values().stream()
                .filter(r -> r.getUserId().equalsIgnoreCase(userId))
                .sorted(Comparator.comparing(r -> r.getTimeSlot().getStartTime()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAvailable(String resourceId, TimeSlot slot) {
        return reservationRegistry.values().stream()
                .filter(r -> r.getResourceId().equalsIgnoreCase(resourceId))
                .filter(Reservation::isActive)
                .noneMatch(r -> r.getTimeSlot().overlapsWith(slot));
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservationRegistry.values());
    }

    @Override
    public void logAction(String action, String performedBy, String details) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String entry = String.format("[%s] ACTION: %-18s | BY: %-12s | DETAILS: %s",
                timestamp, action, performedBy, details);
        auditLogs.add(entry);
    }

    @Override
    public List<String> getAuditLogs() {
        return Collections.unmodifiableList(auditLogs);
    }
}
