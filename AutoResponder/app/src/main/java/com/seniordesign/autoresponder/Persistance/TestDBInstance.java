package com.seniordesign.autoresponder.Persistance;

import com.seniordesign.autoresponder.DataStructures.Setting;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Garlan on 10/5/2015.
 */
public class TestDBInstance implements DBInstance {

    private HashMap<String,String> settings;
    private ArrayList<String[]> ResponseLog;

    public TestDBInstance(){
        settings = new HashMap<>();
        for (String[] defaultSetting : Setting.DEFAULT_SETTINGS){
            settings.put(defaultSetting[0], defaultSetting[1]);
        }
    }

    public void setReplyAll(String reply){
        settings.put(Setting.REPLY_ALL, reply);
    }

    public String getReplyAll(){
       return settings.get(Setting.REPLY_ALL);
    }

    public void setDelay(int minutes){
        settings.put(Setting.TIME_DELAY, Integer.toString(minutes));
    }

    public int getDelay(){
        return Integer.parseInt(settings.get(Setting.TIME_DELAY));
    }

    public void setResponseToggle(boolean responseToggle){
        String responseToggleText;
        if(responseToggle){
            responseToggleText = "true";
        }
        else{
            responseToggleText = "false";
        }
        settings.put(Setting.RESPONSE_TOGGLE, responseToggleText);
    }

    public boolean getResponseToggle(){
        if(settings.get(Setting.RESPONSE_TOGGLE) == "true"){
            return true;
        }
        else{
            return false;
        }
    }
}
