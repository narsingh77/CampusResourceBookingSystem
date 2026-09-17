package com.vityarthi.booking.model;

/**
 * Concrete resource representing a high-capacity auditorium.
 */
public class Auditorium extends CampusResource {
    private static final long serialVersionUID = 1L;
    private final boolean hasStageAudioVisual;
    private final boolean hasAirConditioning;

    public Auditorium(String resourceId, String name, String buildingLocation, int capacity,
                      boolean hasStageAudioVisual, boolean hasAirConditioning) {
        super(resourceId, name, buildingLocation, capacity);
        this.hasStageAudioVisual = hasStageAudioVisual;
        this.hasAirConditioning = hasAirConditioning;
    }

    public boolean isHasStageAudioVisual() {
        return hasStageAudioVisual;
    }

    public boolean isHasAirConditioning() {
        return hasAirConditioning;
    }

    @Override
    public String getResourceType() {
        return "Auditorium";
    }

    @Override
    public double getHourlyCostRate() {
        return 1200.0;
    }
}
