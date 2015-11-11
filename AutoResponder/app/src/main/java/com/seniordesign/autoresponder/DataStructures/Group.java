package com.seniordesign.autoresponder.DataStructures;

/**
 * Created by Garlan on 11/1/2015.
 */
public class Group {
    private String group_name;
    private String response;
    private boolean locationPermission;
    private boolean activityPermission;
    public static final String DEFAULT_GROUP = "default";

    public Group(String group_name, String response, boolean locationPermission, boolean activityPermission) {
        this.group_name = group_name;
        this.response = response;
        this.locationPermission = locationPermission;
        this.activityPermission = activityPermission;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isLocationPermission() {
        return locationPermission;
    }

    public void setLocationPermission(boolean locationPermission) {
        this.locationPermission = locationPermission;
    }

    public boolean isActivityPermission() {
        return activityPermission;
    }

    public void setActivityPermission(boolean activityPermission) {
        this.activityPermission = activityPermission;
    }

    public String getGroupName() {
        return group_name;
    }

    public void setGroupName(String name) {
        this.group_name = name;
    }

    public String toString(){
        return this.group_name + ", " + this.response + ", " + this.locationPermission + ", " + this.activityPermission;
    }
}