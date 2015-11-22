package com.seniordesign.autoresponder.Persistance;

import android.util.Log;

import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.DataStructures.DeveloperLog;
import com.seniordesign.autoresponder.DataStructures.Group;
import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.DataStructures.Setting;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;

/**
 * Created by Garlan on 10/5/2015.
 */
public class TestDBInstance implements DBInstance {
    private static final String TAG = "TestDBInstance";
    private HashMap<String,String> settings;
    private ArrayList<ResponseLog> responseLog;
    private ArrayList<Contact> contactTable;
    private ArrayList<Group> groupTable;
    private ArrayList<DeveloperLog> devLogTable;

    public TestDBInstance(){
        Log.d(TAG, "initializing mock database");
        this.settings = new HashMap<>();
        this.responseLog = new ArrayList<>();
        for (String[] defaultSetting : Setting.DEFAULT_SETTINGS){
            settings.put(defaultSetting[0], defaultSetting[1]);
        }

        //create moc contact table
        this.contactTable = new ArrayList<>();

        //create mock group table with default group
        this.groupTable = new ArrayList<>();
        this.groupTable.add(new Group(Group.DEFAULT_GROUP, Setting.REPLY_ALL_DEF, false, false));

        this.devLogTable = new ArrayList<>();
    }

    ///////////////////////////
    //SETTING TABLE FUNCTIONS//
    ///////////////////////////
    public void setReplyAll(String reply){
        this.settings.put(Setting.REPLY_ALL, reply);
    }

    public String getReplyAll(){
       return this.settings.get(Setting.REPLY_ALL);
    }

    public void setDelay(int minutes){
        this.settings.put(Setting.TIME_DELAY, Integer.toString(minutes));
    }

    public int getDelay(){
        return Integer.parseInt(this.settings.get(Setting.TIME_DELAY));
    }

    public void setResponseToggle(boolean responseToggle){
        String responseToggleText;
        if(responseToggle){
            responseToggleText = "true";
        }
        else{
            responseToggleText = "false";
        }
        this.settings.put(Setting.RESPONSE_TOGGLE, responseToggleText);
    }

    public void setActivityToggle(boolean responseToggle){
        String responseToggleText;
        if(responseToggle){
            responseToggleText = "true";
        }
        else{
            responseToggleText = "false";
        }
        this.settings.put(Setting.ACTIVITY_TOGGLE, responseToggleText);
    }

    public void setLocationToggle(boolean responseToggle){
        String responseToggleText;
        if(responseToggle){
            responseToggleText = "true";
        }
        else{
            responseToggleText = "false";
        }
        this.settings.put(Setting.LOCATION_TOGGLE, responseToggleText);
    }

    public boolean getResponseToggle(){
        String value = this.settings.get(Setting.RESPONSE_TOGGLE);
        if(value.compareTo("true") == 0){
            return true;
        }
        else if(value.compareTo("false") == 0){
            return false;
        }
        else{
            Log.e(TAG, "ERROR: getResponseToggle: found " + Setting.RESPONSE_TOGGLE + " set to " + value + " when true/false was expected");
            throw new InputMismatchException();
        }
    }

    ////////////////////////////////
    //RESPONSE LOG TABLE FUNCTIONS//
    ////////////////////////////////
    public void addToResponseLog(ResponseLog newLog){
        this.responseLog.add(newLog);
    }

    public ResponseLog getFirstResponse(){
        return this.responseLog.get(0);
    }

    public ResponseLog getLastResponse(){
        return this.responseLog.get(responseLog.size()-1);
    }

    public ResponseLog getResponse(int index){
        if (index < responseLog.size() && index >= 0){
            return this.responseLog.get(index);
        }
        else{
            Log.e(TAG, "ERROR: getResponse(): attempted to access index out of bounds: " + index);
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public ResponseLog getLastResponseByNum(String phoneNum){
        for (int i = this.responseLog.size() - 1; i >= 0; i--){
            if (responseLog.get(i).getSenderNumber().compareTo(phoneNum) == 0){
                return responseLog.get(i);
            }
        }
        //if not nothing found, returns null
        return null;
    }

    //TODO: MAKE SEARCH FOR BEGINNING AND END BINARY SEARCH FOR BETTER RUNTIME, currently O(x)
    public ArrayList<ResponseLog> getResponseByDateRange(Date start, Date end){
        ArrayList<ResponseLog> range = new ArrayList<>();

        //ERROR CHECKING
        if (start.after(end)){
            Log.e(TAG, "ERROR: getResponseByDateRange(): start date comes after end date");
            throw new InputMismatchException();
        }

        for(int i = 0; i < this.responseLog.size(); i++){
            if (this.responseLog.get(i).getTimeStamp().after(start) && this.responseLog.get(i).getTimeStamp().before(end)){
                range.add(this.responseLog.get(i));
            }
        }
        return range;
    }

    public ArrayList<ResponseLog> getResponseRange(int start, int end){
        ArrayList<ResponseLog> range = new ArrayList<>();

        //ERROR CHECKING
        if (start > end){
            Log.e(TAG, "ERROR: getResponseRange(): start index came after end index");
            throw new InputMismatchException();
        }
        //ERROR CHECKING
        if (start < 0 || end < 0 || start >= this.responseLog.size() || end >= this.responseLog.size()){
            Log.e(TAG, "ERROR: getResponseRange(): attempted to access index out of bounds: " + start + " " + end);
            throw new ArrayIndexOutOfBoundsException();
        }

        for(int i = start; i <= end; i++){
            range.add(this.responseLog.get(i));
        }

        return range;
    }

    ///////////////////////////
    //CONTACT TABLE FUNCTIONS//
    ///////////////////////////

    /*adds a new contact
    * @param Contact newContact
    * @return # 0 for success, negative number for error code*/
    public int addContact(Contact newContact){
        //check to make sure group name exists
        boolean groupExists = false;
        for (int i = 0; i < this.groupTable.size(); i++){
            if (this.groupTable.get(i).getGroupName().compareTo(newContact.getGroupName()) == 0){
                groupExists = true;
                break;
            }
        }

        if(groupExists) {
            if (this.contactTable.size() > 0) {
                for (int i = 0; i < this.contactTable.size(); i++) {
                    if (this.contactTable.get(i).getName().compareTo(newContact.getName()) > 0) {
                        this.contactTable.add(i, newContact);
                        return 0;
                    }
                }

                //in case last name was the same
                this.contactTable.add(newContact);
            } else {
                this.contactTable.add(newContact);
            }
            return 0;
        }
        else{
            return -1;
        }
    }

    /*removes all contacts with that phone number
    * @param String phoneNumber
    * @return # of contacts removed as int >= 0, or error code as int < 0*/
    public int removeContact(String phoneNum){
        int count = 0;
        for (int i = 0; i < this.contactTable.size(); i++){
            if (this.contactTable.get(i).getPhoneNumber().compareTo(phoneNum) == 0){
                this.contactTable.remove(i);
                count++;
            }
        }

        //no contacts found to remove
        return count;
    }

     /*changes a contacts name
    * @param String phoneNumber, String newName
    * @return 0 for success, or error code as int < 0*/
    public int setContactName(String phoneNum, String newName){

        for (int i = 0; i < this.contactTable.size(); i++){
            if (this.contactTable.get(i).getPhoneNumber().compareTo(phoneNum) == 0){
                this.contactTable.get(i).setName(newName);
                return 0;
            }
        }

        //no contact found to modify
        return -1;
    }

     /*changes a contacts phoneNumber
    * @param String oldPhoneNumber, String newPhoneNum
    * @return 0 for success, or error code as int < 0*/
    public int setContactNumber(String oldPhoneNum, String newPhoneNum){
        for (int i = 0; i < this.contactTable.size(); i++){
            if (this.contactTable.get(i).getPhoneNumber().compareTo(oldPhoneNum) == 0){
                this.contactTable.get(i).setPhoneNumber(newPhoneNum);
                return 0;
            }
        }

        //no contact found to modify
        return -1;
    }

    /*changes a contacts locationPermission
   * @param String phoneNum, boolean permission
   * @return 0 for success, or error code as int < 0*/
    public int setContactLocationPermission(String phoneNum, boolean permission){
        for (int i = 0; i < this.contactTable.size(); i++){
            if (this.contactTable.get(i).getPhoneNumber().compareTo(phoneNum) == 0){
                this.contactTable.get(i).setLocationPermission(permission);
                return 0;
            }
        }

        //no contact found to modify
        return -1;
    }


     /*changes a contacts activityPermission
   * @param String phoneNum, boolean permission
   * @return 0 for success, or error code as int < 0*/
    public int setContactActivityPermission(String phoneNum, boolean permission){
        for (int i = 0; i < this.contactTable.size(); i++){
            if (this.contactTable.get(i).getPhoneNumber().compareTo(phoneNum) == 0){
                this.contactTable.get(i).setActivityPermission(permission);
                return 0;
            }
        }

        //no contact found to modify
        return -1;
    }

    public int setContactInheritance(String phoneNum, boolean inheritance){
        for (int i = 0; i < this.contactTable.size(); i++){
            if (this.contactTable.get(i).getPhoneNumber().compareTo(phoneNum) == 0){
                this.contactTable.get(i).setInheritance(inheritance);
                return 0;
            }
        }

        //no contact found to modify
        return -1;
    }


     /*changes a contact's group name
   * @param String phoneNum, String groupName
   * @return # of rows updated, or error code as int < 0*/
    public int setContactGroup(String phoneNum, String groupName){
        //check to make sure group name exists
        boolean groupExists = false;
        for (int i = 0; i < this.groupTable.size(); i++){
            if (this.groupTable.get(i).getGroupName().compareTo(groupName) == 0){
                groupExists = true;
                break;
            }
        }

        if(groupExists){
            int count = 0;
            for (int i = 0; i < this.contactTable.size(); i++){
                if (this.contactTable.get(i).getPhoneNumber().compareTo(phoneNum) == 0){
                    this.contactTable.get(i).setGroupName(groupName);
                    count++;
                }
            }

            return count;
        }
        else return -1; //group does not exist
    }

    /*changes a contact's response
    * @param String phoneNum, String response
    * @return # of rows updated, or error code as int < 0*/
    public int setContactResponse(String phoneNum, String response){
        int count = 0;
        for (int i = 0; i < this.contactTable.size(); i++){
            if (this.contactTable.get(i).getPhoneNumber().compareTo(phoneNum) == 0){
                this.contactTable.get(i).setResponse(response);
                count++;
            }
        }

        return count;
    }

    /*use to get contact info for a phonenumber
   * @param String phoneNum
   * @return contact if found, null if not found or there was an error*/
    public Contact getContactInfo(String phoneNum){


        for (int i = 0; i < this.contactTable.size(); i++){
            if (this.contactTable.get(i).getPhoneNumber().compareTo(phoneNum) == 0){
                Contact c = this.contactTable.get(i);

                if (c.isInheritance()){
                    Group g = this.getGroupInfo(c.getGroupName());
                    c.setResponse(g.getResponse());
                    c.setActivityPermission(g.isActivityPermission());
                    c.setLocationPermission(g.isLocationPermission());
                }

                return c;
            }
        }
        return null;
    }

    /*returns sorted A - Z by name
   * @param ()
   * @return contact if found, null if not found or there was an error*/
    public ArrayList<Contact> getContactList(){
        return  contactTable;
    }

    /*returns list of contact 1 group sorted A - Z by name
   * @param String phoneNum
   * @return contact if found, null if not found or there was an error*/
    public ArrayList<Contact> getGroup(String groupName){
        ArrayList<Contact> group = new ArrayList<>();

        for (int i = 0; i < this.contactTable.size(); i++){
            if (this.contactTable.get(i).getGroupName().compareTo(groupName) == 0){
                group.add(this.contactTable.get(i));
            }
        }

        return group;
    }

    /////////////////////////
    //GROUP TABLE FUNCTIONS//
    /////////////////////////

    /*adds a new group
    * @param Group newGroup
    * @return # 0 for success, negative number for error code*/
    public int addGroup(Group newGroup){
        if (this.groupTable.size() > 0){
            for(int i = 0; i < this.groupTable.size(); i++){
                if (this.groupTable.get(i).getGroupName().compareTo(newGroup.getGroupName()) == 0 ){
                    return -1; //group already exists
                }
                else if (this.groupTable.get(i).getGroupName().compareTo(newGroup.getGroupName()) < 0 ){
                    this.groupTable.add(i, newGroup);
                    return 0;
                }
            }

            //in case last name was the same
            this.groupTable.add(newGroup);
        }
        else {
            this.groupTable.add(newGroup);
        }
        return 0;
    }

    /*removes all groups with that name
   * @param String phoneNumber
   * @return # of contacts removed as int >= 0, or error code as int < 0*/
    public int removeGroup(String groupName){
        int count = 0;
        for (int i = 0; i < this.groupTable.size(); i++){
            if (this.groupTable.get(i).getGroupName().compareTo(groupName) == 0){
                this.groupTable.remove(i);
                count++;
            }
        }

        //no groups found to remove
        return count;
    }

    /*changes a group's name
    * @param String oldName, String newName
    * @return 0 for success, or error code as int < 0*/
    public int changeGroupName(String oldName, String newName){
        for (int i = 0; i < this.groupTable.size(); i++){
            if (this.groupTable.get(i).getGroupName().compareTo(oldName) == 0){
                this.groupTable.get(i).setGroupName(newName);
                return 0;
            }
        }

        //no group found to modify
        return -1;
    }

    /*changes a group's location permission
    * @param String String groupName, boolean permission
    * @return 0 for success, or error code as int < 0*/
    public int setGroupLocationPermission(String groupName, boolean permission){
        for (int i = 0; i < this.groupTable.size(); i++){
            if (this.groupTable.get(i).getGroupName().compareTo(groupName) == 0){
                this.groupTable.get(i).setLocationPermission(permission);
                return 0;
            }
        }

        //no group found to modify
        return -1;
    }

    /*changes a group's activity permission
    * @param String String groupName, boolean permission
    * @return 0 for success, or error code as int < 0*/
    public int setGroupActivityPermission(String groupName, boolean permission){
        for (int i = 0; i < this.groupTable.size(); i++){
            if (this.groupTable.get(i).getGroupName().compareTo(groupName) == 0){
                this.groupTable.get(i).setActivityPermission(permission);
                return 0;
            }
        }

        //no group found to modify
        return -1;
    }

    /*removes all groups with that name
   * @param String phoneNumber
   * @return # of contacts removed as int >= 0, or error code as int < 0*/
    public int setGroupResponse(String groupName, String response){
        int count = 0;
        for (int i = 0; i < this.groupTable.size(); i++){
            if (this.groupTable.get(i).getGroupName().compareTo(groupName) == 0){
                this.groupTable.get(i).setResponse(response);
                count++;
            }
        }

        return count;
    }

    /*use to get group info from a group name
   * @param String groupName
   * @return group if found, null if not found or there was an error*/
    public Group getGroupInfo(String groupName){
        for (int i = 0; i < this.groupTable.size(); i++){
            if (this.groupTable.get(i).getGroupName().compareTo(groupName) == 0){
                return this.groupTable.get(i);
            }
        }

        return null;
    }

    /*returns sorted A - Z by name
  * @return contact if found, null if not found or there was an error*/
    public ArrayList<Group> getGroupList(){
        return  this.groupTable;
    }

    /////////////////////////////////
    //DEVELOPER LOG TABLE FUNCTIONS//
    /////////////////////////////////

    /*adds new entry to the developer log
  * @param Date timeStamp, String entry*/
    public void addDevLog(Date timeStamp, String entry){
        this.devLogTable.add(new DeveloperLog(timeStamp, entry));
    }

    /*returns log at that index
   * @param int index
   * @return log if found, null if not found or there was an error*/
    public DeveloperLog getDevLog(int index){
        if (index < this.devLogTable.size()) return this.devLogTable.get(index);
        else return null;
    }

    /*returns all logs between first and last index
  * @param int first, int last
  * @return log range if found, null if not found or there was an error*/
    public ArrayList<DeveloperLog> getDevLogRange(int first, int last){
        ArrayList<DeveloperLog> range = new ArrayList<>();

        //ERROR CHECKING
        if (first > last){
            Log.e(TAG, "ERROR: getResponseRange(): start index came after end index");
            return null;
        }
        //ERROR CHECKING
        if (first < 0 || last < 0 || first >= this.devLogTable.size() || last >= this.devLogTable.size()){
            Log.e(TAG, "ERROR: getResponseRange(): attempted to access index out of bounds: " + first + " " + last);
            return null;
        }

        for(int i = first; i <= last; i++){
            range.add(this.devLogTable.get(i));
        }

        return range;
    }

    /*returns all logs between first and last date
  * @param Date start, Date end
  * @return log range if found, null if not found or there was an error*/
    public ArrayList<DeveloperLog> getDevLogRangeByDate(Date start, Date end){
        ArrayList<DeveloperLog> range = new ArrayList<>();

        //ERROR CHECKING
        if (start.after(end)){
            Log.e(TAG, "ERROR: getResponseByDateRange(): start date comes after end date");
            return null;
        }

        for(int i = 0; i < this.devLogTable.size(); i++){
            if (this.devLogTable.get(i).getTimeStamp().after(start) && this.devLogTable.get(i).getTimeStamp().before(end)){
                range.add(this.devLogTable.get(i));
            }
        }
        return range;
    }
}
