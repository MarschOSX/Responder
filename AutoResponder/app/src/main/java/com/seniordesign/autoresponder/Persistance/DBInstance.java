package com.seniordesign.autoresponder.Persistance;

/**
 * Created by Garlan on 10/5/2015.
 */
public interface DBInstance {

    void setReplyAll(String reply);

    String getReplyAll();

    void setDelay(int minutes);

    int getDelay();
}
