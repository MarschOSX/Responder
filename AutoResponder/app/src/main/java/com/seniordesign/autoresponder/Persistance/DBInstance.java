package com.seniordesign.autoresponder.Persistance;

/**
 * Created by Garlan on 10/5/2015.
 */
public interface DBInstance {

    public void setReplyAll(String reply);

    public String getReplyAll();

    public void setDelay(int minutes);

    public int getDelay();
}
