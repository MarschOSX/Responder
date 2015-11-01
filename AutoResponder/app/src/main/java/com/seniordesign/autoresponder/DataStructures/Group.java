package com.seniordesign.autoresponder.DataStructures;

/**
 * Created by Garlan on 11/1/2015.
 */
public class Group {
    private String group_name;
    private String response;
    private boolean location_permission;
    private boolean activity_permission;

    public Group(String group_name, String response, boolean location_permission, boolean activity_permission) {
        this.group_name = group_name;
        this.response = response;
        this.location_permission = location_permission;
        this.activity_permission = activity_permission;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isLocation_permission() {
        return location_permission;
    }

    public void setLocation_permission(boolean location_permission) {
        this.location_permission = location_permission;
    }

    public boolean isActivity_permission() {
        return activity_permission;
    }

    public void setActivity_permission(boolean activity_permission) {
        this.activity_permission = activity_permission;
    }

    public String getGroupName() {
        return group_name;
    }

    public void setGroupName(String name) {
        this.group_name = name;
    }
}