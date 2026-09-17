package com.vityarthi.booking.model;

/**
 * Concrete resource representing a computing laboratory.
 */
public class ComputerLab extends CampusResource {
    private static final long serialVersionUID = 1L;
    private final int workstationCount;
    private final String operatingSystem;
    private final boolean hasGpuAcceleration;

    public ComputerLab(String resourceId, String name, String buildingLocation, int capacity,
                       int workstationCount, String operatingSystem, boolean hasGpuAcceleration) {
        super(resourceId, name, buildingLocation, capacity);
        this.workstationCount = workstationCount;
        this.operatingSystem = operatingSystem;
        this.hasGpuAcceleration = hasGpuAcceleration;
    }

    public int getWorkstationCount() {
        return workstationCount;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public boolean isHasGpuAcceleration() {
        return hasGpuAcceleration;
    }

    @Override
    public String getResourceType() {
        return "Computer Lab";
    }

    @Override
    public double getHourlyCostRate() {
        return 650.0;
    }
}
