package com.vityarthi.booking.model;

/**
 * Concrete resource representing a departmental conference/meeting room.
 */
public class ConferenceRoom extends CampusResource {
    private static final long serialVersionUID = 1L;
    private final boolean hasVideoConferenceKit;
    private final boolean hasSmartBoard;

    public ConferenceRoom(String resourceId, String name, String buildingLocation, int capacity,
                          boolean hasVideoConferenceKit, boolean hasSmartBoard) {
        super(resourceId, name, buildingLocation, capacity);
        this.hasVideoConferenceKit = hasVideoConferenceKit;
        this.hasSmartBoard = hasSmartBoard;
    }

    public boolean isHasVideoConferenceKit() {
        return hasVideoConferenceKit;
    }

    public boolean isHasSmartBoard() {
        return hasSmartBoard;
    }

    @Override
    public String getResourceType() {
        return "Conference Room";
    }

    @Override
    public double getHourlyCostRate() {
        return 350.0;
    }
}
