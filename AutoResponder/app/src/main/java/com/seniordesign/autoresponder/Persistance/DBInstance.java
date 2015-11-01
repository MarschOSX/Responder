package com.seniordesign.autoresponder.Persistance;

import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.DataStructures.DeveloperLog;
import com.seniordesign.autoresponder.DataStructures.Group;
import com.seniordesign.autoresponder.DataStructures.ResponseLog;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by Garlan on 10/5/2015.
 */
public interface DBInstance {

    ///////////////////////////
    //SETTING TABLE FUNCTIONS//
    ///////////////////////////
    void setReplyAll(String reply);

    String getReplyAll();

    void setDelay(int minutes);

    int getDelay();

    void setResponseToggle(boolean toggle);

    boolean getResponseToggle();

    ////////////////////////////////
    //RESPONSE LOG TABLE FUNCTIONS//
    ////////////////////////////////
    void addToResponseLog(ResponseLog newLog);

    ResponseLog getFirstResponse();

    ResponseLog getLastResponse();

    ResponseLog getResponse(int index);

    ResponseLog getLastResponseByNum(String phoneNum);

    ArrayList<ResponseLog> getResponseByDateRange(Date start, Date end);

    ArrayList<ResponseLog> getResponseRange(int start, int end);

    ///////////////////////////
    //CONTACT TABLE FUNCTIONS//
    ///////////////////////////

    int addContact(Contact newContact);

    int removeContact(String phoneNum);

    int setContactName(String phoneNum, String newName);

    int setContactNumber(String oldPhoneNum, String newPhoneNum);

    int setContactLocationPermission(String phoneNum, boolean permission);

    int setContactActivityPermission(String phoneNum, boolean permission);

    int setContactGroup(String phoneNum, String groupName);

    Contact getContactInfo(String phoneNum);

    //returns sorted A - Z by name
    ArrayList<Contact> getContactList();

    ArrayList<Contact> getGroup(String groupName);

    /////////////////////////
    //GROUP TABLE FUNCTIONS//
    /////////////////////////

    int addGroup(Group newGroup);

    int removeGroup(String groupName);

    int changeGroupName(String oldName, String newName);

    int setGroupLocationPermission(String groupName, boolean permission);

    int setGroupActivityPermission(String groupName, boolean permission);

    Group getGroupInfo(String groupName);

    //returns sorted A-Z by group name
    ArrayList<Group> getGroupList();

    /////////////////////////////////
    //DEVELOPER LOG TABLE FUNCTIONS//
    /////////////////////////////////

    void addDevLog(Date timeStamp, String entry);

    DeveloperLog getDevLog(int index);

    ArrayList<DeveloperLog> getDevLogRange(int first, int last);

    ArrayList<DeveloperLog> getDevLogRangeByDate(Date start, Date end);
}
