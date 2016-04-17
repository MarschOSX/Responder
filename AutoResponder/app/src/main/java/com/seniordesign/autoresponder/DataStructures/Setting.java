package com.seniordesign.autoresponder.DataStructures;

/**
 * Created by Garlan on 9/28/2015.
 */
public class Setting {
    //all settings + default values need to be added to DBHelper
    public static final String REPLY_ALL = "reply_all";
    public static final String REPLY_ALL_DEF = "I am busy right now!";
    public static final String UNIVERSAL_REPLY = "universal_reply";
    public static final String UNIVERSAL_REPLY_DEF = "Sorry I don't have my phone right now!";
    public static final String TIME_DELAY = "time_delay";
    public static final String TIME_DELAY_DEF = "20";
    public static final String TIME_LIMIT = "time_limit";
    public static final String TIME_LIMIT_DEF = "100";
    public static final String RESPONSE_TOGGLE = "response_toggle";
    public static final String RESPONSE_TOGGLE_DEF = "false";
    public static final String RESPONSE_TOGGLE_TIME_SET = "response_toggle_time_set";
    public static final String RESPONSE_TOGGLE_TIME_SET_DEF = "0L";
    public static final String ACTIVITY_TOGGLE = "activity_toggle";
    public static final String ACTIVITY_TOGGLE_DEF = "false";
    public static final String LOCATION_TOGGLE = "location_toggle";
    public static final String LOCATION_TOGGLE_DEF = "false";
    public static final String UNIVERSAL_TOGGLE = "universal_toggle";
    public static final String UNIVERSAL_TOGGLE_DEF = "false";
    public static final String PARENTAL_CONTROLS_NUMBER = "parental_controls_num";
    public static final String PARENTAL_CONTROLS_NUMBER_DEF = "0";
    public static final String PARENTAL_CONTROLS_TOGGLLE = "parental_controls_toggle";
    public static final String PARENTAL_CONTROLS_TOGGLLE_DEF = "false";
    public static final String DRIVING_DETECTION_TOGGLE = "driving_detection_toggle";
    public static final String DRIVING_DETECTION_TOGGLE_DEF = "false";
    public static final String DRIVING_DETECTION_INTERVAL = "driving_detection_interval";
    public static final String DRIVING_DETECTION_INTERVAL_DEF = "5";
    public static final String WORLD_TOGGLE = "world_toggle";
    public static final String WORLD_TOGGLE_DEF = "false";


    //if you are adding a new setting, it and its default value must be added here
    public static final String[][] DEFAULT_SETTINGS =
            {       //name        value
                    {REPLY_ALL, REPLY_ALL_DEF},
                    {UNIVERSAL_REPLY, UNIVERSAL_REPLY_DEF},
                    {TIME_DELAY, TIME_DELAY_DEF},
                    {TIME_LIMIT, TIME_LIMIT_DEF},
                    {RESPONSE_TOGGLE, RESPONSE_TOGGLE_DEF},
                    {RESPONSE_TOGGLE_TIME_SET, RESPONSE_TOGGLE_TIME_SET_DEF},
                    {ACTIVITY_TOGGLE, ACTIVITY_TOGGLE_DEF},
                    {LOCATION_TOGGLE, LOCATION_TOGGLE_DEF},
                    {UNIVERSAL_TOGGLE, UNIVERSAL_TOGGLE_DEF},
                    {PARENTAL_CONTROLS_NUMBER, PARENTAL_CONTROLS_NUMBER_DEF},
                    {PARENTAL_CONTROLS_TOGGLLE, PARENTAL_CONTROLS_TOGGLLE_DEF},
                    {DRIVING_DETECTION_TOGGLE, DRIVING_DETECTION_TOGGLE_DEF},
                    {DRIVING_DETECTION_INTERVAL, DRIVING_DETECTION_INTERVAL_DEF},
                    {WORLD_TOGGLE, WORLD_TOGGLE_DEF}
            };

    public static final String[] settingUIList = {
            "World Toggle",
            "Default Contact Response",
            "Default Contact Location Setting",
            "Default Contact Activity Setting",
            "Driving Detection Interval",
            "Parental Controls",
            "Response Log",
            "Time Limit",
            "Time Delay"
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
