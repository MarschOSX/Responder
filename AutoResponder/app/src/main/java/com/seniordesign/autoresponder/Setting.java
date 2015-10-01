package com.seniordesign.autoresponder;

/**
 * Created by Garlan on 9/28/2015.
 */
public class Setting {
    //all settings + default values need to be added to DBHelper
    public static final String REPLY_ALL = "reply_all";
    public static final String TIME_DELAY = "time_delay";


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
