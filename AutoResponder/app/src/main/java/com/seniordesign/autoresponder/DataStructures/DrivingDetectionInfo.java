package com.seniordesign.autoresponder.DataStructures;

import android.location.Location;

import java.util.ArrayList;

/**
 * Created by Garlan on 3/13/2016.
 */
public class DrivingDetectionInfo {
    private ArrayList<LocationRecord> mLocationHistory;
    private int mLimit;
    private final float earthRadius_miles = 3959.0f;
    private final float earthRadius_km = 6371.0f;

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

    public static double calculateDistance(Location start, Location stop, boolean isMetric){
        double lat_start = Math.toRadians(start.getLatitude());
        double long_start = Math.toRadians(start.getLongitude());
        double lat_stop = Math.toRadians(stop.getLatitude());
        double long_stop = Math.toRadians(stop.getLongitude());

        return -1;
    }

    public static double calculateDistance_long(Location start, Location stop, boolean isMetric){
        double long_start = Math.toRadians(start.getLongitude());
        double long_stop = Math.toRadians(stop.getLongitude());

        return -1;
    }

    public static double calculateDistance_lat(Location start, Location stop, boolean isMetric){
        double lat_start = Math.toRadians(start.getLatitude());
        double lat_stop = Math.toRadians(stop.getLatitude());

        

        return -1;
    }
}
