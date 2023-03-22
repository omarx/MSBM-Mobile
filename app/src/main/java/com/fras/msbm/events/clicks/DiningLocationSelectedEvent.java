package com.fras.msbm.events.clicks;

import com.fras.msbm.models.locations.Location;

/**
 * Created by Shane on 7/31/2016.
 */
public final class DiningLocationSelectedEvent {
    private final Location location;

    public DiningLocationSelectedEvent(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
