package com.seniordesign.autoresponder.Persistance;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.util.Log;

import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.DataStructures.DeveloperLog;
import com.seniordesign.autoresponder.DataStructures.Group;
import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.DataStructures.Setting;

import java.sql.Date;
import java.util.ArrayList;
import java.util.InputMismatchException;

/**
 * Created by Garlan on 9/28/2015.
 */

//note for setting up transactions: goto https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html
public class PermDBInstance implements DBInstance {
    private static final String TAG = "PermDBInstance";
    private SQLiteDatabase myDB;


    public PermDBInstance(Context context) {
        //this.mDB = SQLiteDatabase.openOrCreateDatabase(DATABASE_NAME, null);
        Log.d(TAG, "initializing database instance");

        //access DB via helper class
        DBHelper myLittleHelper = new DBHelper(context);
        this.myDB = myLittleHelper.getWritableDatabase();
    }


    ///////////////////////////
    //SETTING TABLE FUNCTIONS//
    ///////////////////////////

    public void setReplyAll(String reply){
        Log.d(TAG, "setting replyAll to " + reply + "....");
        update(DBHelper.TABLE_SETTINGS, DBHelper.SETTING_NAME[0], Setting.REPLY_ALL, DBHelper.SETTING_VALUE[0], reply);
    }

    public String getReplyAll(){
        final String query =
                "SELECT " + DBHelper.SETTING_VALUE[0] +
                 " FROM " + DBHelper.TABLE_SETTINGS +
                 " WHERE " + DBHelper.SETTING_NAME[0] + "=" + "\"" + Setting.REPLY_ALL + "\"";

        //query db and ensure object was returned
        Cursor result = myDB.rawQuery(query, null);
        String response;
        if ((result != null) && (result.moveToFirst())){ // move pointer to first row
            //load setting value into string
            response = result.getString(result.getColumnIndex(DBHelper.SETTING_VALUE[0]));

            //close the cursor
            result.close();

            //return the query result
            Log.d(TAG, getMethodName(0) + ": replyAll is " + response);
            return response;
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not access cursor object from: " + query);
            return null;
        }
    }

    public void setDelay(int minutes){
        Log.d(TAG, "setting delay to "+ minutes +"....");
        update(DBHelper.TABLE_SETTINGS, DBHelper.SETTING_NAME[0], Setting.TIME_DELAY, DBHelper.SETTING_VALUE[0], minutes);
    }

    //will return -1 if no result returned
    public int getDelay(){
        final String query =
                "SELECT " + DBHelper.SETTING_VALUE[0] +
                 " FROM " + DBHelper.TABLE_SETTINGS +
                 " WHERE " + DBHelper.SETTING_NAME[0] + " = " + "\"" + Setting.TIME_DELAY + "\"";

        //query db and ensure cursor object returned is valid
        Cursor result = myDB.rawQuery(query, null);
        int delay;
        String value;
        if ((result != null) && (result.moveToFirst())){

            //retrieve and return setting value
            value = result.getString(result.getColumnIndex(DBHelper.SETTING_VALUE[0]));
            delay = Integer.parseInt(value);
            result.close();
            Log.d(TAG, getMethodName(0) + ": delay is " + delay);
            return delay;
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not access cursor object from: " + query);
            return -1;
        }
    }

    public void setResponseToggle(boolean responseToggle){
        Log.d(TAG, "setting  responseToggle to " + responseToggle + "....");
        update(DBHelper.TABLE_SETTINGS, DBHelper.SETTING_NAME[0], Setting.RESPONSE_TOGGLE, DBHelper.SETTING_VALUE[0], responseToggle);
    }

    public boolean getResponseToggle(){
        final String query =
                "SELECT " + DBHelper.SETTING_VALUE[0] +
                 " FROM " + DBHelper.TABLE_SETTINGS +
                 " WHERE " + DBHelper.SETTING_NAME[0] + " = " + "\"" + Setting.RESPONSE_TOGGLE + "\"";

        //query database and ensure cursor returned is valid
        Cursor result = myDB.rawQuery(query, null);
        boolean toggle;
        if ((result != null) && (result.moveToFirst())){

            //retrieve setting value
            String response = result.getString(result.getColumnIndex(DBHelper.SETTING_VALUE[0]));

            //determine if value is true or false and returns as translates into a boolean
            if( response.compareTo("true") == 0){
                toggle = true;
            }
            else if ( response.compareTo("false") == 0){
                toggle = false;
            }
            else{
                Log.e(TAG, "ERROR: " + getMethodName(0) + ": found " + response + " when a value of true or false was expected from: " + query);
                throw new InputMismatchException();
            }
            result.close();
        } else {
            Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not get/access cursor object from: " + query);
            throw new NullPointerException();
        }
        Log.d(TAG, getMethodName(0) + ": toggle is " + toggle);
        return toggle;
    }

    ////////////////////////////////
    //RESPONSE LOG TABLE FUNCTIONS//
    ////////////////////////////////

    public void addToResponseLog(ResponseLog newLog){
        Log.d(TAG, "adding entry to ResponseLog....");
        myDB.beginTransaction();
        try {
            //load columns in args
            ContentValues args = new ContentValues();
            args.put(DBHelper.RESPONSELOG_TIMESTAMP[0], newLog.getTimeStamp().getTime());
            args.put(DBHelper.RESPONSELOG_SENDERNUM[0], newLog.getSenderNumber());
            args.put(DBHelper.RESPONSELOG_MESSAGERCV[0], newLog.getMessageReceived());
            args.put(DBHelper.RESPONSELOG_MESSAGESNT[0], newLog.getMessageSent());

            //add the row to the table and checks if insert was succesfull
            long insert = myDB.insertOrThrow(DBHelper.TABLE_RESPONSELOG, null, args);
            if (insert != -1) {
                myDB.setTransactionSuccessful();
                Log.d(TAG, "entry added to ResponseLog " + newLog.toString());
            }
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName(0) + " failed");
            throw e;
        }
        finally {
            myDB.endTransaction();
        }
    }

    //TODO TEST THIS FUNCTION
    public ResponseLog getFirstResponse(){
        final String query =
                "SELECT MIN(" + DBHelper.RESPONSELOG_TIMESTAMP[0] + "), * " +
                 " FROM " + DBHelper.TABLE_RESPONSELOG;

        Cursor result = myDB.rawQuery(query, null);

        //fields to be returned
        Date timeStamp;
        String senderNumber;
        String messageRecv;
        String messageSent;

        if ((result != null) && (result.moveToFirst())){

            //retrieve and return setting value
            timeStamp = new Date(result.getLong(result.getColumnIndex(DBHelper.RESPONSELOG_TIMESTAMP[0])));
            senderNumber = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_SENDERNUM[0]));
            messageRecv = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_MESSAGERCV[0]));
            messageSent = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_MESSAGESNT[0]));
            result.close();

            return new ResponseLog(messageSent, messageRecv, senderNumber, timeStamp);
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not access cursor object from: " + query);
            return null;
        }
    }


    //TODO TEST THIS FUNCTION
    public ResponseLog getLastResponse(){
        final String query =
                "SELECT MAX(" + DBHelper.RESPONSELOG_TIMESTAMP[0] + "), * " +
                        " FROM " + DBHelper.TABLE_RESPONSELOG;

        Cursor result = myDB.rawQuery(query, null);

        //fields to be returned
        Date timeStamp;
        String senderNumber;
        String messageRecv;
        String messageSent;

        if ((result != null) && (result.moveToFirst())){

            //retrieve and return setting value
            timeStamp = new Date(result.getLong(result.getColumnIndex(DBHelper.RESPONSELOG_TIMESTAMP[0])));
            senderNumber = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_SENDERNUM[0]));
            messageRecv = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_MESSAGERCV[0]));
            messageSent = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_MESSAGESNT[0]));
            result.close();

            return new ResponseLog(messageSent, messageRecv, senderNumber, timeStamp);
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not access cursor object from: " + query);
            return null;
        }
    }

    //TODO TEST THIS FUNCTION
    public ResponseLog getResponse(int index){
        final String query =
                "SELECT * " +
                " FROM " + DBHelper.TABLE_RESPONSELOG +
                " WHERE ROWID = \"" + index + "\"";

        Cursor result = myDB.rawQuery(query, null);

        //fields to be returned
        Date timeStamp;
        String senderNumber;
        String messageRecv;
        String messageSent;

        if ((result != null) && (result.moveToFirst())){

            //retrieve and return setting value
            timeStamp = new Date(result.getLong(result.getColumnIndex(DBHelper.RESPONSELOG_TIMESTAMP[0])));
            senderNumber = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_SENDERNUM[0]));
            messageRecv = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_MESSAGERCV[0]));
            messageSent = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_MESSAGESNT[0]));
            result.close();

            return new ResponseLog(messageSent, messageRecv, senderNumber, timeStamp);
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not access cursor object from: " + query);
            return null;
        }
    }

    public ResponseLog getLastResponseByNum(String phoneNum){
        final String query =
                "SELECT MAX(" + DBHelper.RESPONSELOG_TIMESTAMP[0] + "), " + DBHelper.RESPONSELOG_SENDERNUM[0] + ", " + DBHelper.RESPONSELOG_MESSAGERCV[0] + ", " + DBHelper.RESPONSELOG_MESSAGESNT[0] +
                        " FROM " + DBHelper.TABLE_RESPONSELOG +
                        " WHERE "+ DBHelper.RESPONSELOG_SENDERNUM[0] + " = \"" + phoneNum + "\"";

        final String query_2 = "SELECT *" +
            " FROM " + DBHelper.TABLE_RESPONSELOG +
            " WHERE " + DBHelper.RESPONSELOG_SENDERNUM[0] + "=\"" + phoneNum + "\" ORDER BY " + DBHelper.RESPONSELOG_TIMESTAMP[0];

        String senderNum = phoneNum;
        String msgRcv = "hi";
        String msgSnt = "hi";
        Date date = new Date(0);

        try {
            Cursor result = myDB.rawQuery(query, null);
            if (result != null) {
                //check and see how many rows were returned
                int numRows = result.getCount();
                if (numRows == 0) {
                    Log.d(TAG, "ResponseLogRetrieved: "+ date + ", " + senderNum + ", " + msgSnt + ", " + msgRcv);
                    return new ResponseLog(msgSnt, msgRcv, senderNum, date); //returns a false record from the beginning of time if no record found
                }
                else{
                    result.moveToFirst();
                }

                //load query results
                date = new Date(result.getLong(0));
                senderNum = result.getString(1);
                msgRcv = result.getString(2);
                msgSnt = result.getString(3);

                Log.d(TAG, "ResponseLogRetrieved:("+ numRows +") "+ date + ", " + senderNum + ", " + msgSnt + ", " + msgRcv);

                result.close();

                return new ResponseLog(msgSnt, msgRcv, senderNum, date);
            } else {
                Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not access cursor object from: " + query);
                return null;
            }
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName(0) + " could not retrieve data");
            throw e;
        }
    }

    //TODO TEST THIS FUNCTION
    public ArrayList<ResponseLog> getResponseByDateRange(Date start, Date end){
        final String query =
                "SELECT * " +
                " FROM " + DBHelper.TABLE_RESPONSELOG +
                " WHERE " + DBHelper.RESPONSELOG_TIMESTAMP[0] + " BETWEEN \"" + start.getTime() + "\" AND \"" + end.getTime() + "\"";

        Cursor result = myDB.rawQuery(query, null);

        ArrayList<ResponseLog> range = new ArrayList<>();
        Date timeStamp;
        String senderNumber;
        String messageRecv;
        String messageSent;

        if ((result != null) && (result.moveToFirst())){

            for (int i = 0; i < result.getCount(); i++){
                timeStamp = new Date(result.getLong(result.getColumnIndex(DBHelper.RESPONSELOG_TIMESTAMP[0])));
                senderNumber = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_SENDERNUM[0]));
                messageRecv = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_MESSAGERCV[0]));
                messageSent = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_MESSAGESNT[0]));

                range.add(new ResponseLog(messageSent, messageRecv, senderNumber, timeStamp));

                result.moveToNext();
            }
            //retrieve and return setting value

            result.close();

            return range;
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not access cursor object from: " + query);
            return null;
        }
    }

    //TODO TEST THIS FUNCTION
    public ArrayList<ResponseLog> getResponseRange(int start, int end){
        final String query =
                "SELECT * " +
                " FROM " + DBHelper.TABLE_RESPONSELOG +
                " WHERE ROWID BETWEEN \"" + start + "\" AND \"" + end + "\" ORDER BY("+ DBHelper.RESPONSELOG_TIMESTAMP[0] +") ASC";

        Cursor result = myDB.rawQuery(query, null);

        ArrayList<ResponseLog> range = new ArrayList<>();
        Date timeStamp;
        String senderNumber;
        String messageRecv;
        String messageSent;

        if ((result != null) && (result.moveToFirst())){

            for (int i = 0; i < result.getCount(); i++){
                timeStamp = new Date(result.getLong(result.getColumnIndex(DBHelper.RESPONSELOG_TIMESTAMP[0])));
                senderNumber = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_SENDERNUM[0]));
                messageRecv = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_MESSAGERCV[0]));
                messageSent = result.getString(result.getColumnIndex(DBHelper.RESPONSELOG_MESSAGESNT[0]));

                range.add(new ResponseLog(messageSent, messageRecv, senderNumber, timeStamp));

                result.moveToNext();
            }
            //retrieve and return setting value

            result.close();

            return range;
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not access cursor object from: " + query);
            return null;
        }
    }

    ///////////////////////////
    //CONTACT TABLE FUNCTIONS//
    ///////////////////////////

    public int addContact(Contact newContact){
        Log.d(TAG, "adding contact(" + newContact.toString() + ")....");

        //verify first that the group does exist
        Group g = this.getGroupInfo(newContact.getGroupName());
        if (g == null){

            return -1;
        }

        myDB.beginTransaction();
        try {

            //load columns in args
            ContentValues args = new ContentValues();
            args.put(DBHelper.CONTACT_NAME[0], newContact.getName());
            args.put(DBHelper.CONTACT_PHONENUM[0], newContact.getPhoneNumber());
            args.put(DBHelper.CONTACT_GROUP[0], newContact.getGroupName());
            args.put(DBHelper.CONTACT_RESPONSE[0], newContact.getResponse());
            args.put(DBHelper.CONTACT_ACTIVITYPERM[0], convertBool(newContact.isActivityPermission()));
            args.put(DBHelper.CONTACT_LOCATIONPERM[0], convertBool(newContact.isLocationPermission()));

            //add the row to the table and checks if insert was succesfull
            long insert = myDB.insertOrThrow(DBHelper.TABLE_CONTACT, null, args);
            if (insert != -1) {
                myDB.setTransactionSuccessful();
                Log.d(TAG, getMethodName(0) + ":"  + newContact.toString() + " added successfully");
            }
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName(0) + " failed");
            throw e;
        }
        finally {
            myDB.endTransaction();
        }

        return 0;
    }

    public int removeContact(String phoneNum){
        Log.d(TAG, "removing " + phoneNum + " from contacts....");
        return remove(DBHelper.TABLE_CONTACT, DBHelper.CONTACT_PHONENUM[0] + "=\"" + phoneNum + "\"");
    }

    public int setContactName(String phoneNum, String newName){
        Log.d(TAG, "setting name of " + phoneNum + " to " + newName + ".....");
        return update(DBHelper.TABLE_CONTACT, DBHelper.CONTACT_PHONENUM[0], phoneNum ,DBHelper.CONTACT_NAME[0], newName);
    }

    public int setContactNumber(String oldPhoneNum, String newPhoneNum){
        Log.d(TAG, "changing phone number " + oldPhoneNum + " to " + newPhoneNum + ".....");
        return update(DBHelper.TABLE_CONTACT, DBHelper.CONTACT_PHONENUM[0], oldPhoneNum ,DBHelper.CONTACT_PHONENUM[0], newPhoneNum);
    }

    public int setContactLocationPermission(String phoneNum, boolean permission){
        Log.d(TAG, "setting location permission of " + phoneNum + " to " + permission + ".....");
        return update(DBHelper.TABLE_CONTACT, DBHelper.CONTACT_PHONENUM[0], phoneNum ,DBHelper.CONTACT_LOCATIONPERM[0], permission);
    }

    public int setContactActivityPermission(String phoneNum, boolean permission){
        Log.d(TAG, "setting activity permission of " + phoneNum + " to " + permission + ".....");
        return update(DBHelper.TABLE_CONTACT, DBHelper.CONTACT_PHONENUM[0], phoneNum ,DBHelper.CONTACT_ACTIVITYPERM[0], permission);
    }

    public int setContactGroup(String phoneNum, String groupName){
        Log.d(TAG, "setting group of " + phoneNum + " to " + groupName + ".....");
        return update(DBHelper.TABLE_CONTACT, DBHelper.CONTACT_PHONENUM[0], phoneNum ,DBHelper.CONTACT_GROUP[0], groupName);
    }

    public int setContactResponse(String phoneNum, String response){
        Log.d(TAG, "setting response of " + phoneNum + " to " + response + ".....");
        return update(DBHelper.TABLE_CONTACT, DBHelper.CONTACT_PHONENUM[0], phoneNum ,DBHelper.CONTACT_RESPONSE[0], response);
    }

    public Contact getContactInfo(String phoneNum){
        final String query =
                "SELECT * " +
                " FROM " + DBHelper.TABLE_CONTACT +
                " WHERE " + DBHelper.CONTACT_PHONENUM[0] + "=" + "\"" + phoneNum + "\"";

        Cursor result = myDB.rawQuery(query, null);

        Contact c = null;

        String name;
        String phoneNumber;
        String groupName;
        String response;
        boolean locationPermission;
        boolean activityPermission;

        if ((result != null) && (result.moveToFirst())){

            name = result.getString(result.getColumnIndex(DBHelper.CONTACT_NAME[0]));
            phoneNumber = result.getString(result.getColumnIndex(DBHelper.CONTACT_PHONENUM[0]));
            groupName = result.getString(result.getColumnIndex(DBHelper.CONTACT_GROUP[0]));
            response = result.getString(result.getColumnIndex(DBHelper.CONTACT_RESPONSE[0]));

            if (result.getString(result.getColumnIndex(DBHelper.CONTACT_LOCATIONPERM[0])).compareTo("true") == 0) locationPermission = true;
            else locationPermission = false;

            if (result.getString(result.getColumnIndex(DBHelper.CONTACT_ACTIVITYPERM[0])).compareTo("true") == 0) activityPermission = true;
            else activityPermission = false;

            c = new Contact(name, phoneNumber, groupName, response, locationPermission, activityPermission);

            result.close();
            Log.d(TAG, getMethodName(0) + " contact info for " + phoneNum + " is " + c.toString());
            return c;
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName(0) + ": no rows returned from: " + query);
            return null;
        }
    }

    //returns sorted A - Z by name
    public ArrayList<Contact> getContactList(){
        final String query =
                "SELECT * " +
                " FROM " + DBHelper.TABLE_CONTACT +
                " ORDER BY (" + DBHelper.CONTACT_NAME[0] + ") ASC";

        Cursor result = myDB.rawQuery(query, null);

        ArrayList<Contact> range = new ArrayList<>();
        String name;
        String phoneNumber;
        String groupName;
        String response;
        boolean locationPermission;
        boolean activityPermission;


        if ((result != null) && (result.moveToFirst())){

            for (int i = 0; i < result.getCount(); i++){
                name = result.getString(result.getColumnIndex(DBHelper.CONTACT_NAME[0]));
                phoneNumber = result.getString(result.getColumnIndex(DBHelper.CONTACT_PHONENUM[0]));
                groupName = result.getString(result.getColumnIndex(DBHelper.CONTACT_GROUP[0]));
                response = result.getString(result.getColumnIndex(DBHelper.CONTACT_RESPONSE[0]));

                if (result.getString(result.getColumnIndex(DBHelper.CONTACT_LOCATIONPERM[0])).compareTo("true") == 0) locationPermission = true;
                else locationPermission = false;

                if (result.getString(result.getColumnIndex(DBHelper.CONTACT_ACTIVITYPERM[0])).compareTo("true") == 0) activityPermission = true;
                else activityPermission = false;

                Contact c = new Contact(name, phoneNumber, groupName, response, locationPermission, activityPermission);
                Log.d(TAG, getMethodName(0) +": "+ c.toString());
                range.add(c);
                result.moveToNext();
            }

            result.close();

            return range;
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not access cursor object from: " + query);
            return null;
        }
    }

    //TODO TEST THIS FUNCTION
    public ArrayList<Contact> getGroup(String group){
        final String query =
                "SELECT * " +
                " FROM " + DBHelper.TABLE_CONTACT +
                " WHERE " + DBHelper.CONTACT_GROUP[0] + " = \"" + group + "\"" +
                " ORDER BY (" + DBHelper.CONTACT_NAME[0] + ") DESC";

        Cursor result = myDB.rawQuery(query, null);

        ArrayList<Contact> range = new ArrayList<>();
        String name;
        String phoneNumber;
        String groupName;
        String response;

        String activityString;
        String locationString;
        boolean locationPermission;
        boolean activityPermission;

        if ((result != null) && (result.moveToFirst())){

            for (int i = 0; i < result.getCount(); i++){
                name = result.getString(result.getColumnIndex(DBHelper.CONTACT_NAME[0]));
                phoneNumber = result.getString(result.getColumnIndex(DBHelper.CONTACT_PHONENUM[0]));
                groupName = result.getString(result.getColumnIndex(DBHelper.CONTACT_GROUP[0]));
                response = result.getString(result.getColumnIndex(DBHelper.CONTACT_RESPONSE[0]));
                activityString = result.getString(result.getColumnIndex(DBHelper.CONTACT_RESPONSE[0]));
                locationString = result.getString(result.getColumnIndex(DBHelper.CONTACT_RESPONSE[0]));

                if (locationString.compareTo("true") == 0) locationPermission = true;
                else locationPermission = false;

                if (activityString.compareTo("true") == 0)activityPermission = true;
                else activityPermission = false;

                Contact c = new Contact(name, phoneNumber, groupName, response, locationPermission, activityPermission);
                Log.d(TAG, getMethodName(0) +": "+ c.toString());
                range.add(c);

                result.moveToNext();
            }

            result.close();

            return range;
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not access cursor object from: " + query);
            return null;
        }
    }

    /////////////////////////
    //GROUP TABLE FUNCTIONS//
    /////////////////////////

    public int addGroup(Group newGroup){
        Log.d(TAG, getMethodName(0) + ": adding " + newGroup.toString() + "....");
        myDB.beginTransaction();
        try {

            //load columns in args
            ContentValues args = new ContentValues();
            args.put(DBHelper.GROUP_NAME[0], newGroup.getGroupName());
            args.put(DBHelper.GROUP_RESPONSE[0], newGroup.getResponse());
            args.put(DBHelper.GROUP_ACTIVITYPERM[0], convertBool(newGroup.isActivityPermission()));
            args.put(DBHelper.GROUP_LOCATIONPERM[0], convertBool(newGroup.isLocationPermission()));


            //add the row to the table and checks if insert was succesfull
            long insert = myDB.insertOrThrow(DBHelper.TABLE_GROUP, null, args);
            if (insert != -1) {
                myDB.setTransactionSuccessful();
                Log.d(TAG, getMethodName(0) + ": added"  + newGroup.toString());
            }
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName(0) + " failed");
            throw e;
        }
        finally {
            myDB.endTransaction();
        }

        return 0;
    }


    public int removeGroup(String groupName){
        Log.d(TAG, "removing " + groupName + " from groups....");
        return remove(DBHelper.TABLE_GROUP, DBHelper.GROUP_NAME[0] + "=\"" + groupName + "\"");
    }


    public int changeGroupName(String oldName, String newName){
        Log.d(TAG, "changing name of " + oldName + " to " + newName + ".....");
        return update(DBHelper.TABLE_GROUP, DBHelper.GROUP_NAME[0], oldName ,DBHelper.GROUP_NAME[0], newName);
    }


    public int setGroupLocationPermission(String groupName, boolean permission){
        Log.d(TAG, "changing activity of permission of " + groupName + " to " + permission + ".....");
        return update(DBHelper.TABLE_GROUP, DBHelper.GROUP_NAME[0], groupName ,DBHelper.GROUP_LOCATIONPERM[0], permission);
    }


    public int setGroupActivityPermission(String groupName, boolean permission){
        Log.d(TAG, "changing activity permission of " + groupName + " to " + permission + ".....");
        return update(DBHelper.TABLE_GROUP, DBHelper.GROUP_NAME[0], groupName ,DBHelper.GROUP_ACTIVITYPERM[0], permission);
    }


    public int setGroupResponse(String groupName, String response){
        Log.d(TAG, "setting group response of " + groupName + " to " + response + ".....");
        return update(DBHelper.TABLE_GROUP, DBHelper.GROUP_NAME[0], groupName ,DBHelper.GROUP_RESPONSE[0], response);
    }

    public Group getGroupInfo(String groupName){
        final String query =
                "SELECT * " +
                " FROM " + DBHelper.TABLE_GROUP +
                " WHERE " + DBHelper.GROUP_NAME[0] + "=" + "\"" + groupName + "\"";

        Cursor result = myDB.rawQuery(query, null);

        Group c = null;

        String name;
        String response;
        boolean locationPermission;
        boolean activityPermission;

        if ((result != null) && (result.moveToFirst())){

            name = result.getString(result.getColumnIndex(DBHelper.GROUP_NAME[0]));
            response = result.getString(result.getColumnIndex(DBHelper.GROUP_RESPONSE[0]));

            if (result.getString(result.getColumnIndex(DBHelper.GROUP_LOCATIONPERM[0])).compareTo("true") == 0) locationPermission = true;
            else locationPermission = false;

            if (result.getString(result.getColumnIndex(DBHelper.GROUP_ACTIVITYPERM[0])).compareTo("true") == 0)activityPermission = true;
            else activityPermission = false;

            c = new Group(name, response, locationPermission, activityPermission);

            result.close();

            Log.d(TAG, getMethodName(0) + ": retrieved " + c.toString());

            return c;
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not access cursor object from: " + query);
            return null;
        }
    }

    //returns sorted A-Z by group name
    public ArrayList<Group> getGroupList(){
        final String query =
                "SELECT * " +
                " FROM " + DBHelper.TABLE_GROUP +
                " ORDER BY (" + DBHelper.GROUP_NAME[0] + ") ASC";

        Cursor result = myDB.rawQuery(query, null);

        ArrayList<Group> range = new ArrayList<>();
        String name;
        String response;
        boolean locationPermission;
        boolean activityPermission;


        if ((result != null) && (result.moveToFirst())){

            for (int i = 0; i < result.getCount(); i++){
                name = result.getString(result.getColumnIndex(DBHelper.GROUP_NAME[0]));
                response = result.getString(result.getColumnIndex(DBHelper.GROUP_RESPONSE[0]));

                if (result.getString(result.getColumnIndex(DBHelper.GROUP_LOCATIONPERM[0])).compareTo("true") == 0) locationPermission = true;
                else locationPermission = false;

                if (result.getString(result.getColumnIndex(DBHelper.GROUP_ACTIVITYPERM[0])).compareTo("true") == 0) activityPermission = true;
                else activityPermission = false;

                Group g = new Group(name, response, locationPermission, activityPermission);
                Log.d(TAG, getMethodName(0) + ": " + g.toString());
                range.add(g);

                result.moveToNext();
            }

            result.close();

            return range;
        }
        else {
            Log.e(TAG, "ERROR: " + getMethodName(0) + ": could not access cursor object from: " + query);
            return null;
        }
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


    //convenience functions
    private static String getMethodName(int level) {
        return Thread.currentThread().getStackTrace()[3 + level].getMethodName();
    }

    private int update(String table, String matchColumn, String matchValue, String updateColumn, String updateValue){
        myDB.beginTransaction();
        try {
            //set criteria for selecting row
            String filter = matchColumn + "=" + "\"" + matchValue + "\"";

            //set new value for column to be updated
            ContentValues args = new ContentValues();
            args.put(updateColumn, updateValue);

            //update column and check to make sure only 1 row was updated
            myDB.setTransactionSuccessful();
            Log.d(TAG, getMethodName(1) + ": update succeeded");
            return  myDB.update(table, args, filter, null);
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName(1) + " failed");
            throw e;
        }
        finally {
            myDB.endTransaction();
        }
    }

    private int update(String table, String matchColumn, String matchValue, String updateColumn, boolean updateValue){
        myDB.beginTransaction();
        try {
            //set criteria for selecting row
            String filter = matchColumn + "=" + "\"" + matchValue + "\"";

            String value;

            if (updateValue) value = "true";
            else value = "false";

            //set new value for column to be updated
            ContentValues args = new ContentValues();
            args.put(updateColumn, value);

            //update column and check to make sure only 1 row was updated
            myDB.setTransactionSuccessful();
            Log.d(TAG, getMethodName(1) + ": update succeeded");
            return  myDB.update(table, args, filter, null);
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName(1) + " failed");
            throw e;
        }
        finally {
            myDB.endTransaction();
        }
    }

    private int update(String table, String matchColumn, String matchValue, String updateColumn, int updateValue){
        myDB.beginTransaction();
        try {
            //set criteria for selecting row
            String filter = matchColumn + "=" + "\"" + matchValue + "\"";

            //set new value for column to be updated
            ContentValues args = new ContentValues();
            args.put(updateColumn, Integer.toString(updateValue));

            //update column and check to make sure only 1 row was updated
            myDB.setTransactionSuccessful();
            Log.d(TAG, getMethodName(1) + ": update succeeded");
            return  myDB.update(table, args, filter, null);
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName(1) + " failed");
            throw e;
        }
        finally {
            myDB.endTransaction();
        }
    }

    private int remove(String table, String whereClause){
        myDB.beginTransaction();
        try {
            //update column and check to make sure only 1 row was updated
            int count =  myDB.delete(table, whereClause, null);
            myDB.setTransactionSuccessful();
            Log.d(TAG, getMethodName(1) + ": removal succeeded("+count+")");
            return count;
        }
        catch (Exception e){
            Log.e(TAG, "ERROR: " + getMethodName(1) + " failed");
            throw e;
        }
        finally {
            myDB.endTransaction();
        }
    }

    private String convertBool(boolean b){
        if (b) return "true";
        else return "false";
    }

    private boolean convertToBool(String b){
        if (b.compareTo("true") == 0) return true;
        else return false;
    }
}
