package com.seniordesign.autoresponder.DataStructures;

/**
 * Created by Garlan on 11/1/2015.
 */
public class Contact extends Group{
    private String phoneNumber;
    private String name;
    private boolean inheritance;

    public Contact(String name, String phoneNumber, String group_name, String response, boolean location_permission, boolean activity_permission, boolean inheritance) {
        super(group_name, response, location_permission, activity_permission);
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.inheritance = inheritance;
    }

    public boolean isInheritance() {
        return inheritance;
    }

    public void setInheritance(boolean inheritance) {
        this.inheritance = inheritance;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return this.name + ", "
                + this.phoneNumber + ", "
                + this.getGroupName() + ", "
                + this.getResponse() + ", "
                + this.isLocationPermission() + ", "
                + this.isLocationPermission() + ", "
                + this.isActivityPermission() + ", "
                + this.inheritance;
    }
}
