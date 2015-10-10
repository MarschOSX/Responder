package com.seniordesign.autoresponder.Persistance;

import android.content.Context;
import android.util.Log;

/**
 * Created by Garlan on 10/5/2015.
 */
public class DBProvider{
    private static final String TAG = "DBProvider";
    private static DBInstance testDB;
    private static DBInstance permDB;

    public static DBInstance getInstance(boolean testMode, Context context){
        if (testMode){
            if (testDB == null){
                testDB = new TestDBInstance();
            }
            Log.d(TAG, "providing testing database");
            return testDB;
        }
        else{
            Log.d(TAG, "providing permanent database");
            permDB = new PermDBInstance(context);
            return permDB;
        }
    }
}
