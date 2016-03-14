package com.seniordesign.autoresponder.DataStructures;

import android.location.Location;
import java.sql.Date;

/**
 * Created by Garlan on 3/13/2016.
 */
public class LocationRecord {
    private Location mLocation;
    private Date mDate;

    public LocationRecord(Location location, Date date) {
        mLocation = location;
        mDate = date;
    }

    public Location getLocation() {
        return mLocation;
    }

    public Date getDate() {
        return mDate;
    }
}

