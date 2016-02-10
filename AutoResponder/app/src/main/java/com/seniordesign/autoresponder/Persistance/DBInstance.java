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

    void setUniversalReply(String universalReply);

    String getUniversalReply();

    void setDelay(int minutes);

    int getDelay();

    void setResponseToggle(boolean toggle);

    void setActivityToggle(boolean toggle);

    void setLocationToggle(boolean toggle);

    void setUniversalToggle(boolean toggle);

    boolean getResponseToggle();
    
    boolean getLocationToggle();

    boolean getActivityToggle();

    boolean getUniversalToggle();

    ////////////////////////////////
    //RESPONSE LOG TABLE FUNCTIONS//
    ////////////////////////////////

    void addToResponseLog(ResponseLog newLog);

    ResponseLog getLastResponseByNum(String phoneNum);

    ///////////////////////////
    //CONTACT TABLE FUNCTIONS//
    ///////////////////////////

    /*adds a new contact
    * @param Contact newContact
    * @return # 0 for success,
    * @return -1 if group does not exist*/
    int addContact(Contact newContact);

    /*removes all contacts with that phone number
    * @param String phoneNumber
    * @return # of contacts removed as int >= 0, or error code as int < 0*/
    int removeContact(String phoneNum);

    /*changes a contacts name
    * @param String phoneNumber, String newName
    * @return 0 for success, or error code as int < 0*/
    int setContactName(String phoneNum, String newName);

    /*changes a contacts phoneNumber
    * @param String oldPhoneNumber, String newPhoneNum
    * @return 0 for success, or error code as int < 0*/
    int setContactNumber(String oldPhoneNum, String newPhoneNum);

    /*changes a contacts locationPermission
   * @param String phoneNum, boolean permission
   * @return 0 for success, or error code as int < 0*/
    int setContactResponse(String phoneNum, String response);

    /*changes a contacts activityPermission
   * @param String phoneNum, boolean permission
   * @return 0 for success, or error code as int < 0*/
    int setContactLocationPermission(String phoneNum, boolean permission);

    /*changes a contact's group name
   * @param String phoneNum, String groupName
   * @return # of rows updated, or error code as int < 0*/
    int setContactActivityPermission(String phoneNum, boolean permission);

    /*changes a contact's inheritance, ie if true, when you run getContactInfo() it will
       return a contact, but the response and permissions returned will be from the group
   * @param String phoneNum, String groupName
   * @return # of rows updated, or error code as int < 0*/
    int setContactInheritance(String phoneNum, boolean inheritance);

    /*changes a contact's response
    * @param String phoneNum, String response
    * @return # of rows updated,
    * @return -1 if the new groupName does not exist*/
    int setContactGroup(String phoneNum, String groupName);

    /*use to get contact info for a phonenumber
   * @param String phoneNum
   * @return contact if found, null if not found or there was an error*/
    Contact getContactInfo(String phoneNum);

    /*returns sorted A - Z by name
   * @return contact if found, null if not found or there was an error*/
    ArrayList<Contact> getContactList();

    /*returns list of contact 1 group sorted A - Z by name
   * @param String phoneNum
   * @return contact if found, null if not found or there was an error*/
    ArrayList<Contact> getGroup(String groupName);

    /////////////////////////
    //GROUP TABLE FUNCTIONS//
    /////////////////////////

    /*adds a new group
    * @param Group newGroup
    * @return # 0 for success,
    * @return -1 if group already exists
    * */
    int addGroup(Group newGroup);

    /*removes all groups with that name
   * @param String phoneNumber
   * @return # of contacts removed as int >= 0, or error code as int < 0*/
    int removeGroup(String groupName);

    /*changes a group's name
    * @param String oldName, String newName
    * @return 0 for success, or error code as int < 0*/
    int changeGroupName(String oldName, String newName);

    /*changes a group's location permission
    * @param String String groupName, boolean permission
    * @return 0 for success, or error code as int < 0*/
    int setGroupResponse(String groupName, String response);

    /*changes a group's activity permission
     * @param String String groupName, boolean permission
     * @return 0 for success, or error code as int < 0*/
    int setGroupLocationPermission(String groupName, boolean permission);

    /*removes all groups with that name
   * @param String phoneNumber
   * @return # of contacts removed as int >= 0, or error code as int < 0*/
    int setGroupActivityPermission(String groupName, boolean permission);

    /*use to get group info from a group name
   * @param String groupName
   * @return group if found, null if not found or there was an error*/
    Group getGroupInfo(String groupName);

    /*returns sorted A - Z by name
  * @return contact if found, null if not found or there was an error*/
    ArrayList<Group> getGroupList();

    /////////////////////////////////
    //DEVELOPER LOG TABLE FUNCTIONS//
    /////////////////////////////////

    /*adds new entry to the developer log
  * @param Date timeStamp, String entry*/
    //void addDevLog(Date timeStamp, String entry);

    /*returns log at that index
  * @param int index
  * @return log if found, null if not found or there was an error*/
    //DeveloperLog getDevLog(int index);

    /*returns all logs between first and last index
     * @param int first, int last
     * @return log range if found, null if not found or there was an error*/
    //ArrayList<DeveloperLog> getDevLogRange(int first, int last);

    /*returns all logs between first and last date
  * @param Date start, Date end
  * @return log range if found, null if not found or there was an error*/
    //ArrayList<DeveloperLog> getDevLogRangeByDate(Date start, Date end);
}
