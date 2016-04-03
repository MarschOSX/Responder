package com.seniordesign.autoresponder.DataStructures;

import android.location.Location;

import java.util.ArrayList;

/**
 * Created by Garlan on 3/13/2016.
 */
public class DrivingDetectionInfo {
    private ArrayList<LocationRecord> mLocationHistory;
    private int mLimit;

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
}
