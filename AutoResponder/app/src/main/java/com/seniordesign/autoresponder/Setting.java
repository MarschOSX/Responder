package com.seniordesign.autoresponder;

/**
 * Created by Garlan on 9/28/2015.
 */
public class Setting {
    //all settings + default values need to be added to DBHelper
    public static final String REPLY_ALL = "reply_all";
    public static final String TIME_DELAY = "time_delay";

    //if you are adding a new setting, it and its default value must be added here
    public static final String[][] DEFAULT_SETTINGS =
            {
                    {REPLY_ALL, "I am busy right now"},
                    {TIME_DELAY, "5"}
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

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
