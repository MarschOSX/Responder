package com.seniordesign.autoresponder.DataStructures;

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
        mLocationHistory.add(newRecord);

        if(mLocationHistory.size() > mLimit){
            mLocationHistory.remove(mLocationHistory.size()-1);
        }
    }
}
