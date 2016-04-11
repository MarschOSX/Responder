package com.seniordesign.autoresponder.DataStructures;

import android.location.Location;

import java.util.ArrayList;

/**
 * Created by Garlan on 3/13/2016.
 */
public class DrivingDetectionInfo {
    private ArrayList<LocationRecord> mLocationHistory;
    private int mLimit;
    public static final float threshHold = 15.0f;

    //test mode params
    public static final float testingThreshHold = 2.0f;
    public static final boolean isTesting = false;

    public DrivingDetectionInfo(int maxRecords) {
        mLocationHistory = new ArrayList<>();
        this.mLimit = maxRecords;
    }

    public ArrayList<LocationRecord> getLocationHistory() {
        return mLocationHistory;
    }

    public void addToHistory(LocationRecord newRecord) {
        mLocationHistory.add(0, newRecord);

        if(mLocationHistory.size() > mLimit){
            mLocationHistory.remove(mLocationHistory.size()-1);
        }
    }

    public LocationRecord getMostRectent(){
        return mLocationHistory.get(mLocationHistory.size()-1);
    }

    /** @param p2 the LocationRecord of the current location
     * @param p1 the LocationRecord of the previous location
     * @return the speed in mph
     */
    public static float getSpeed(LocationRecord p1, LocationRecord p2){
        //get the distance in meters
        float distance = p1.getLocation().distanceTo(p2.getLocation());

        //convert the distance to miles
        float inMiles = Math.abs((distance / 1000f) * 0.62f);

        //convert the time (ms) to hours
        float hours = (p2.getDate().getTime() - p1.getDate().getTime()) / 3600000f;

        //arbitrarily filters out low mph deemed "white noise"
        if (inMiles / hours < 0.1) return 0;
        else return inMiles / hours;
    }
}
