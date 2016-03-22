package com.seniordesign.autoresponder.Persistance;

import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.DataStructures.Group;
import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import java.util.ArrayList;

/**
 * Created by Garlan on 10/5/2015.
 */
public interface DBInstance {

    ///////////////////////////
    //SETTING TABLE FUNCTIONS//
    ///////////////////////////
    /** sets the default group response*/
    void setReplyAll(String reply);

    /** @return the default group response */
    String getReplyAll();

    /** sets the Universal Response*/
    void setUniversalReply(String universalReply);

    /** @return the universal response*/
    String getUniversalReply();

    /** set how long the responder will ignore a contact's messages after responding to a message*/
    void setDelay(int minutes);

    /** @return time period in minutes before responding to the same phone number */
    int getDelay();

    /** set how long AutoResponder will remain on until it disables itself */
    void setTimeLimit(int hours);

    /** @return the time period in hours that the autoresponder is to be on*/
    int getTimeLimit();

    /** record the time that the response toggle was set */
    void setTimeResponseToggleSet(long milliseconds);

    /** @return when the response toggle was enabled */
    long getTimeResponseToggleSet();

    /** @return the current period in minutes between getting the current location*/
    int getDrivingDetectionPeriod();

    /** specify the time period in minutes between receipt of current location
     * to determine impact on battery life*/
    void setDrivingDetectionPeriod(int minutes);

    /** enable/disable all autoresponse functionality feature*/
    void setResponseToggle(boolean toggle);

    /** enable/disable the Activity Response feature*/
    void setActivityToggle(boolean toggle);

    /** enable/disable the Location Response feature*/
    void setLocationToggle(boolean toggle);

    /** enable/disable the Universal Reply feature*/
    void setUniversalToggle(boolean toggle);

    /** @return whether or not any messages can be responded to */
    boolean getResponseToggle();

    /** @return whether or not location requests can be responded to */
    boolean getLocationToggle();

    /** @return whether or not activity requests can be responded to */
    boolean getActivityToggle();

    /** @return the status of the Universal Reply Toggle*/
    boolean getUniversalToggle();

    ////////////////////////////////
    //RESPONSE LOG TABLE FUNCTIONS//
    ////////////////////////////////

    /** adds new log to the response log*/
    void addToResponseLog(ResponseLog newLog);

    /** @return the most recent response log for the specified phone number*/
    ResponseLog getLastResponseByNum(String phoneNum);

    /** @return  the entire response log sorted from newest to oldest*/
    ArrayList<ResponseLog> getResponseLogList();

    /** clears all entries from the response log table*/
    void deleteResponseLogs();

    ///////////////////////////
    //CONTACT TABLE FUNCTIONS//
    ///////////////////////////

    /**adds a new contact to the contact table
    * @return # 0 for success, -1 if group does not exist*/
    int addContact(Contact newContact);

    /**removes all contacts with that phone number
    * @return # of contacts removed as int >= 0, or error code as int < 0*/
    int removeContact(String phoneNum);

    /**changes a contacts name
    * @return 0 for success, or error code as int < 0*/
    int setContactName(String phoneNum, String newName);

    /**changes a contacts phoneNumber
    * @return 0 for success, or error code as int < 0*/
    int setContactNumber(String oldPhoneNum, String newPhoneNum);

    /**changes a contacts locationPermission
   * @return 0 for success, or error code as int < 0*/
    int setContactResponse(String phoneNum, String response);

    /**changes a contacts activityPermission
      * @return 0 for success, or error code as int < 0*/
    int setContactLocationPermission(String phoneNum, boolean permission);

    /**changes a contact's group name
      * @return # of rows updated, or error code as int < 0*/
    int setContactActivityPermission(String phoneNum, boolean permission);

    /**changes a contact's inheritance, ie if true, when you run getContactInfo() it will
       return a contact, but the response and permissions returned will be from the group
      * @return # of rows updated, or error code as int < 0*/
    int setContactInheritance(String phoneNum, boolean inheritance);

    /**changes a contact's response
        * @return # of rows updated, -1 if the new groupName does not exist*/
    int setContactGroup(String phoneNum, String groupName);

    /**use to get contact info for a phonenumber
      * @return contact if found, null if not found or there was an error*/
    Contact getContactInfo(String phoneNum);

    /**returns sorted A - Z by name
   * @return contact if found, null if not found or there was an error*/
    ArrayList<Contact> getContactList();

    /**returns list of contact 1 group sorted A - Z by name
      * @return contact if found, null if not found or there was an error*/
    ArrayList<Contact> getGroup(String groupName);

    /////////////////////////
    //GROUP TABLE FUNCTIONS//
    /////////////////////////

    /*adds a new group
    * @return # 0 for success,
    * @return -1 if group already exists
    * */
    int addGroup(Group newGroup);

    /**removes all groups with that name
     * @return # of contacts removed as int >= 0, or error code as int < 0*/
    int removeGroup(String groupName);

    /**changes a group's name
     * @return 0 for success, or error code as int < 0*/
    int changeGroupName(String oldName, String newName);

    /**changes a group's location permission
     * @return 0 for success, or error code as int < 0*/
    int setGroupResponse(String groupName, String response);

    /**changes a group's activity permission
     * @return 0 for success, or error code as int < 0*/
    int setGroupLocationPermission(String groupName, boolean permission);

    /**removes all groups with that name
     * @return # of contacts removed as int >= 0, or error code as int < 0*/
    int setGroupActivityPermission(String groupName, boolean permission);

    /**use to get group info from a group name
     * @return group if found, null if not found or there was an error*/
    Group getGroupInfo(String groupName);

    /**returns sorted A - Z by name
  * @return contact if found, null if not found or there was an error*/
    ArrayList<Group> getGroupList();

    /////////////////////////////////
    //DEVELOPER LOG TABLE FUNCTIONS//
    /////////////////////////////////

    /*adds new entry to the developer log
      //void addDevLog(Date timeStamp, String entry);

    /*returns log at that index
    * @return log if found, null if not found or there was an error*/
    //DeveloperLog getDevLog(int index);

    /*returns all logs between first and last index
     * @return log range if found, null if not found or there was an error*/
    //ArrayList<DeveloperLog> getDevLogRange(int first, int last);

    /*returns all logs between first and last date
    * @return log range if found, null if not found or there was an error*/
    //ArrayList<DeveloperLog> getDevLogRangeByDate(Date start, Date end);
}
