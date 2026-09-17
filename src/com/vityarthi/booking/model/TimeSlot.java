package com.vityarthi.booking.model;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Value object representing a reservation time window.
 * Implements Comparable to maintain sorted chronological ordering in collections.
 */
public class TimeSlot implements Comparable<TimeSlot>, Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        Objects.requireNonNull(startTime, "Start time cannot be null");
        Objects.requireNonNull(endTime, "End time cannot be null");
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be strictly after start time.");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public long getDurationMinutes() {
        return Duration.between(startTime, endTime).toMinutes();
    }

    public boolean overlapsWith(TimeSlot other) {
        if (other == null) return false;
        return this.startTime.isBefore(other.endTime) && this.endTime.isAfter(other.startTime);
    }

    @Override
    public int compareTo(TimeSlot o) {
        int comp = this.startTime.compareTo(o.startTime);
        if (comp != 0) return comp;
        return this.endTime.compareTo(o.endTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeSlot timeSlot)) return false;
        return startTime.equals(timeSlot.startTime) && endTime.equals(timeSlot.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime);
    }

    @Override
    public String toString() {
        return startTime.format(FORMATTER) + " -> " + endTime.format(FORMATTER) + " (" + getDurationMinutes() + " mins)";
    }
}
