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
        groupTable.add(new Group(Group.DEFAULT_GROUP, Setting.REPLY_ALL_DEF, false, false));
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
        if (this.contactTable.size() > 0){
            for(int i = 0; i < this.contactTable.size(); i++){
                if (this.contactTable.get(i).getName().compareTo(newContact.getName()) < 0 ){
                    this.contactTable.add(i, newContact);
                    return 0;
                }
            }

            //in case last name was the same
            this.contactTable.add(newContact);
        }
        else {
            this.contactTable.add(newContact);
        }
        return 0;
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


     /*changes a contact's group name
   * @param String phoneNum, String groupName
   * @return 0 for success, or error code as int < 0*/
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
            for (int i = 0; i < this.contactTable.size(); i++){
                if (this.contactTable.get(i).getPhoneNumber().compareTo(phoneNum) == 0){
                    this.contactTable.get(i).setGroupName(groupName);
                    return 0;
                }
            }

            return -1; //no contact found to modify
        }
        else return -2; //group does not exist
    }

    /*use to get contact info for a phonenumber
   * @param String phoneNum
   * @return contact if found, null if not found or there was an error*/
    public Contact getContactInfo(String phoneNum){
        for (int i = 0; i < this.contactTable.size(); i++){
            if (this.contactTable.get(i).getPhoneNumber().compareTo(phoneNum) == 0){
                return this.contactTable.get(i);
            }
        }

        return null;
    }

    /*returns sorted A - Z by name
   * @param String phoneNum
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

    //TODO IMPLEMENT
    public int addGroup(Group newGroup){
        return  -1;
    }

    //TODO IMPLEMENT
    public int removeGroup(String groupName){
        return  -1;
    }

    public int changeGroupName(String oldName, String newName){
        return  -1;
    }

    //TODO IMPLEMENT
    public int setGroupLocationPermission(String groupName, boolean permission){
        return  -1;
    }

    //TODO IMPLEMENT
    public int setGroupActivityPermission(String groupName, boolean permission){
        return  -1;
    }

    //TODO IMPLEMENT
    public Group getGroupInfo(String groupName){
        return  null;
    }

    //TODO IMPLEMENT
    //returns sorted A-Z by group name
    public ArrayList<Group> getGroupList(){
        return  null;
    }

    /////////////////////////////////
    //DEVELOPER LOG TABLE FUNCTIONS//
    /////////////////////////////////

    //TODO IMPLEMENT
    public void addDevLog(Date timeStamp, String entry){

    }

    //TODO IMPLEMENT
    public DeveloperLog getDevLog(int index){
        return null;
    }

    //TODO IMPLEMENT
    public ArrayList<DeveloperLog> getDevLogRange(int first, int last){
        return null;
    }

    //TODO IMPLEMENT
    public ArrayList<DeveloperLog> getDevLogRangeByDate(Date start, Date end){
        return null;
    }
}
