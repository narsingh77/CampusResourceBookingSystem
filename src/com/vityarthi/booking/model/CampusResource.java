package com.vityarthi.booking.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Abstract base class for physical campus venues and equipment.
 */
public abstract class CampusResource implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String resourceId;
    private final String name;
    private final String buildingLocation;
    private final int capacity;
    private boolean operational;

    public CampusResource(String resourceId, String name, String buildingLocation, int capacity) {
        this.resourceId = Objects.requireNonNull(resourceId, "Resource ID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.buildingLocation = Objects.requireNonNull(buildingLocation, "Location cannot be null");
        this.capacity = capacity;
        this.operational = true;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getName() {
        return name;
    }

    public String getBuildingLocation() {
        return buildingLocation;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isOperational() {
        return operational;
    }

    public void setOperational(boolean operational) {
        this.operational = operational;
    }

    public abstract String getResourceType();

    public abstract double getHourlyCostRate();

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - Loc: %s | Capacity: %d | Operational: %s",
                getResourceType(), name, resourceId, buildingLocation, capacity, operational);
    }
}
