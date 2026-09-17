package com.vityarthi.booking.service;

import com.vityarthi.booking.model.CampusResource;
import com.vityarthi.booking.model.Reservation;
import com.vityarthi.booking.model.ReservationStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates reporting, system metrics, and CSV export using Java 8+ Streams API.
 */
public class AnalyticsService {
    private final BookingService bookingService;
    private final ResourceService resourceService;

    public AnalyticsService(BookingService bookingService, ResourceService resourceService) {
        this.bookingService = bookingService;
        this.resourceService = resourceService;
    }

    public Map<String, Double> getTotalHoursByResourceType() {
        List<Reservation> confirmed = bookingService.getAllReservations().stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .toList();

        Map<String, Double> hoursMap = new HashMap<>();

        for (Reservation res : confirmed) {
            Optional<CampusResource> opt = resourceService.getResource(res.getResourceId());
            if (opt.isPresent()) {
                String type = opt.get().getResourceType();
                double hours = res.getTimeSlot().getDurationMinutes() / 60.0;
                hoursMap.put(type, hoursMap.getOrDefault(type, 0.0) + hours);
            }
        }
        return hoursMap;
    }

    public Map<ReservationStatus, Long> getReservationCountByStatus() {
        return bookingService.getAllReservations().stream()
                .collect(Collectors.groupingBy(Reservation::getStatus, Collectors.counting()));
    }

    public String exportToCSV(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("ReservationId,UserId,ResourceId,ResourceType,StartTime,EndTime,DurationMin,Status,Purpose\n");

        for (Reservation r : bookingService.getAllReservations()) {
            String resType = resourceService.getResource(r.getResourceId())
                    .map(CampusResource::getResourceType)
                    .orElse("Unknown");

            sb.append(String.format("%s,%s,%s,%s,%s,%s,%d,%s,\"%s\"\n",
                    r.getReservationId(),
                    r.getUserId(),
                    r.getResourceId(),
                    resType,
                    r.getTimeSlot().getStartTime(),
                    r.getTimeSlot().getEndTime(),
                    r.getTimeSlot().getDurationMinutes(),
                    r.getStatus(),
                    r.getPurpose().replace("\"", "'")));
        }

        Files.writeString(Paths.get(filePath), sb.toString());
        return filePath;
    }
}
