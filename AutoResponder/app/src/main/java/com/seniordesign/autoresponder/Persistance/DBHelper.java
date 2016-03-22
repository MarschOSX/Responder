package com.seniordesign.autoresponder.Persistance;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.seniordesign.autoresponder.DataStructures.Group;
import com.seniordesign.autoresponder.DataStructures.Setting;

/**
 * Created by Garlan on 9/29/2015.
 */
public class DBHelper extends SQLiteOpenHelper{
    private static final String TAG = "DBHelper";
    public static final String DATABASE_NAME = "autoResponder.db";
    public static final int DATABASE_VERSION = 7; //**IMPORTANT** increment version if you make changes to table structure or add a new table

    //TABLE INFO HERE:
    //Column info is stored as a 2 element array with 0 the title and 1 the datatype
    //follow the below format for creating tables


    public static final String TABLE_SETTINGS = "settings";
    public static final String[] SETTING_NAME = {"setting_name", "VARCHAR(30) UNIQUE"};
    public static final String[] SETTING_VALUE = {"value", "VARCHAR(30)"};
    private static final String CREATE_SETTINGS = "CREATE TABLE " + TABLE_SETTINGS +
            "(" + SETTING_NAME[0] + " " + SETTING_NAME[1] + ", " +
            SETTING_VALUE[0] + " " + SETTING_VALUE[1] + ");";

    public static final String TABLE_DEVLOG = "developer_log";
    public static final String[] DEVLOG_TIMESTAMP = {"time_stamp" , "DATE"};
    public static final String[] DEVLOG_ENTRY = {"entry", "TEXT"};
    public static final String[] DEVLOG_ACTION = {"action", "TEXT"};
    private static final String CREATE_DEVLOG = "CREATE TABLE " + TABLE_DEVLOG +
            "(" + DEVLOG_TIMESTAMP[0] + " " + DEVLOG_TIMESTAMP[1] + ", " +
            DEVLOG_ACTION[0] + " " + DEVLOG_ACTION[1] + ", " +
            DEVLOG_ENTRY[0]+ " " + DEVLOG_ENTRY[1] + ");";

    public static final String TABLE_RESPONSELOG = "response_log";
    public static final String[] RESPONSELOG_TIMERECEIVED = {"time_received" , "DATE NOT NULL"};
    public static final String[] RESPONSELOG_SENDERNUM = {"sender_phoneNumber", "VARCHAR(16) NOT NULL"};
    public static final String[] RESPONSELOG_MESSAGERCV = {"message_received", "VARCHAR(144) NOT NULL"};
    public static final String[] RESPONSELOG_MESSAGESNT = {"message_sent", "VARCHAR(144) NOT NULL"};
    public static final String[] RESPONSELOG_TIMESENT = {"time_sent" , "DATE NOT NULL"};
    public static final String[] RESPONSELOG_LOCATIONSHARED = {"location_shared", "BOOLEAN NOT NULL"};
    public static final String[] RESPONSELOG_ACTIVITYSHARED = {"activity_shared", "BOOLEAN NOT NULL"};

    public static final String CREATE_RESPONSELOG = "CREATE TABLE " + TABLE_RESPONSELOG +
            "(" + RESPONSELOG_TIMERECEIVED[0] + " " + RESPONSELOG_TIMERECEIVED[1] + ", " +
            RESPONSELOG_TIMESENT[0]+ " " + RESPONSELOG_TIMESENT[1] + ", " +
            RESPONSELOG_SENDERNUM[0]+ " " + RESPONSELOG_SENDERNUM[1] + ", " +
            RESPONSELOG_MESSAGERCV[0] + " " + RESPONSELOG_MESSAGERCV[1] + ", " +
            RESPONSELOG_MESSAGESNT[0]+ " " + RESPONSELOG_MESSAGESNT[1] + ", " +
            RESPONSELOG_LOCATIONSHARED[0]+ " " + RESPONSELOG_LOCATIONSHARED[1] + ", " +
            RESPONSELOG_ACTIVITYSHARED[0] + " " + RESPONSELOG_ACTIVITYSHARED[1] + ");";

    public static final String TABLE_CONTACT = "contact_table";
    public static final String[] CONTACT_NAME = {"name", "TEXT NOT NULL"};
    public static final String[] CONTACT_PHONENUM = {"phonenumber", "TEXT NOT NULL UNIQUE"};
    public static final String[] CONTACT_GROUP = {"group_name", "TEXT NOT NULL"};
    public static final String[] CONTACT_RESPONSE = {"response", "VARCHAR(144)"};
    public static final String[] CONTACT_LOCATIONPERM = {"location_permission", "BOOLEAN"};
    public static final String[] CONTACT_ACTIVITYPERM = {"activity_permission", "BOOLEAN"};
    public static final String[] CONTACT_INHERITANCE = {"inheritance", "BOOLEAN"};
    private static final String CREATE_CONTACT = "CREATE TABLE " + TABLE_CONTACT +
            "(" + CONTACT_NAME[0] + " " + CONTACT_NAME[1]
            + ", " + CONTACT_PHONENUM[0] + " " + CONTACT_PHONENUM[1]
            + ", " + CONTACT_GROUP[0] + " " + CONTACT_GROUP[1]
            + ", " + CONTACT_RESPONSE[0] + " " + CONTACT_RESPONSE[1]
            + ", " + CONTACT_LOCATIONPERM[0] + " " + CONTACT_LOCATIONPERM[1]
            + ", " + CONTACT_ACTIVITYPERM[0] + " " + CONTACT_ACTIVITYPERM[1]
            + ", " + CONTACT_INHERITANCE[0] + " " + CONTACT_INHERITANCE[1] + ")";

    public static final String TABLE_GROUP = "group_table";
    public static final String[] GROUP_NAME = {"name", "TEXT NOT NULL UNIQUE"};
    public static final String[] GROUP_RESPONSE = {"response", "VARCHAR(144) NOT NULL"};
    public static final String[] GROUP_LOCATIONPERM = {"location_permission", "BOOLEAN NOT NULL"};
    public static final String[] GROUP_ACTIVITYPERM = {"activity_permission", "BOOLEAN NOT NULL"};
    private static final String CREATE_GROUP = "CREATE TABLE " + TABLE_GROUP +
            "(" + GROUP_NAME[0] + " " + GROUP_NAME[1]
            + ", " + GROUP_RESPONSE[0] + " " + GROUP_RESPONSE[1]
            + ", " + GROUP_LOCATIONPERM[0] + " " + GROUP_LOCATIONPERM[1]
            + ", " + GROUP_ACTIVITYPERM[0] + " " + GROUP_ACTIVITYPERM[1] + ")";

    //all tables must be added to this list
    public static final String[] TABLE_LIST = {TABLE_SETTINGS, TABLE_RESPONSELOG, TABLE_CONTACT, TABLE_GROUP, TABLE_DEVLOG};

    //as well as all creation statements to this list
    private static final String[] CREATION_LIST = {CREATE_SETTINGS, CREATE_RESPONSELOG, CREATE_CONTACT, CREATE_GROUP, CREATE_DEVLOG};


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //initializes tables
        Log.d(TAG, "called onCreate");
        for(String create : CREATION_LIST){
            db.execSQL(create);
        }

        //initialize settings
        for(String setting[] : Setting.DEFAULT_SETTINGS){
            db.beginTransaction();
            try {
                ContentValues insertSetting = new ContentValues();
                insertSetting.put(SETTING_NAME[0], setting[0]);
                insertSetting.put(SETTING_VALUE[0], setting[1]);
                if (db.insert(TABLE_SETTINGS, null, insertSetting) == -1){
                    Log.d(TAG, setting[0] + " " + setting[1] + " could not be added");
                }
                else{
                    Log.d(TAG, setting[0] + " " + setting[1] + " was added");
                }
                db.setTransactionSuccessful();
            }
            catch (Exception e){
                db.endTransaction();
                throw e;
            }
            finally {
                db.endTransaction();
            }
        }

        //add default group

        db.beginTransaction();
        try {
            ContentValues insertSetting = new ContentValues();
            insertSetting.put(GROUP_NAME[0], Group.DEFAULT_GROUP);
            insertSetting.put(GROUP_RESPONSE[0], Setting.REPLY_ALL_DEF);
            insertSetting.put(GROUP_ACTIVITYPERM[0], "false");
            insertSetting.put(GROUP_LOCATIONPERM[0], "false");
            if (db.insert(TABLE_GROUP, null, insertSetting) == -1){
                Log.d(TAG, "could not create " + Group.DEFAULT_GROUP);
            }
            else{
                Log.d(TAG, Group.DEFAULT_GROUP + " was added");
            }
            db.setTransactionSuccessful();
        }
        catch (Exception e){
            db.endTransaction();
            throw e;
        }
        finally {
            db.endTransaction();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(String table : TABLE_LIST){
            db.execSQL("IF OBJECT_ID(\'" + table + "\', \'U\')IS NOT NULL DROP TABLE " + table);
        }
    }

}
