package com.seniordesign.autoresponder.Persistance;

import android.content.Context;
import android.util.Log;

/**
 * Created by Garlan on 10/5/2015.
 */
public class DBProvider{
    private static final String TAG = "DBProvider";
    private static DBInstance testDB;

    public static DBInstance getInstance(boolean testMode, Context context){
        if (testMode){
            /*if (testDB == null){
                Log.d(TAG, "creating new mock database");
                testDB = new TestDBInstance();
            }
            Log.d(TAG, "returning mock database");
            return testDB;*/
            Log.e(TAG, "test mode no longer available");
            return null;
        }
        else{
            Log.d(TAG, "accessing database");
            return new PermDBInstance(context);
        }
    }
}
