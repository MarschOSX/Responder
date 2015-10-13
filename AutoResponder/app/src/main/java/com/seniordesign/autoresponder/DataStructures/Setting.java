package com.seniordesign.autoresponder.DataStructures;

/**
 * Created by Garlan on 9/28/2015.
 */
public class Setting {
    //all settings + default values need to be added to DBHelper
    public static final String REPLY_ALL = "reply_all";
    public static final String REPLY_ALL_DEF = "I am busy right now";
    public static final String TIME_DELAY = "time_delay";
    public static final String TIME_DELAY_DEF = "20";
    public static final String RESPONSE_TOGGLE = "response_toggle";
    public static final String RESPONSE_TOGGLE_DEF = "true";

    //if you are adding a new setting, it and its default value must be added here
    public static final String[][] DEFAULT_SETTINGS =
            {       //name        value
                    {REPLY_ALL, REPLY_ALL_DEF},
                    {TIME_DELAY, TIME_DELAY_DEF},
                    {RESPONSE_TOGGLE, RESPONSE_TOGGLE_DEF}
            };


    private String name;
    private String value;

    public Setting(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    //disabled setName due to settings only being created by developer.  User should not be able to change setting name
    /*public void setName(String name) {
        this.name = name;
    }*/

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
