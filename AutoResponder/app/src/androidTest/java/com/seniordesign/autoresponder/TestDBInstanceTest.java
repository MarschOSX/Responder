package com.seniordesign.autoresponder;

import android.test.AndroidTestCase;

import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;

import java.sql.Date;

/**
 * By MarschOSX on 10/24/2015
 */

public class TestDBInstanceTest extends AndroidTestCase {
    //Perm database
    private DBInstance database;

    public void testDBisNotNull()throws Exception {
        database = DBProvider.getInstance(true, getContext());
        assertNotNull(database);
    }

    public void testDbSetsAndGetsIssues()throws Exception {
        database = DBProvider.getInstance(true, getContext());
        String toSend = "HelloJUnit";
        database.setReplyAll(toSend);
        String fromDB = database.getReplyAll();
        /**
         * assertEquals does not work for DB calls
         * which is why we do it this way. We can uncomment for proof
         */
        //assertEquals(toSend, fromDB);

        if(fromDB.matches(toSend)){
            assertTrue(true);
        }else{
            assertTrue(false);
        }
    }

    public void testDbSetsAndGets()throws Exception {
        database = DBProvider.getInstance(false, getContext());

        //Test Response Toggle
        database.setResponseToggle(true);
        assertTrue(checker(database.getResponseToggle(), true));

        database.setResponseToggle(false);
        assertTrue(checker(database.getResponseToggle(), false));

        //Test delay set
        database.setDelay(15);
        assertTrue(checker(database.getDelay(), 15));
        database.setDelay(0);
        assertTrue(checker(database.getDelay(), 0));
        database.setDelay(15);
        assertTrue(checker(database.getDelay(), 15));
    }

    public void testDbSetsAndGetsResponseLogs()throws Exception {
        database = DBProvider.getInstance(false, getContext());

        //Create ResponseLog to test
        String phoneNum = "+18568327320";
        long millis = System.currentTimeMillis() % 1000;
        ResponseLog responseLog = new ResponseLog("JUnitA","JUnitB",phoneNum, new Date(millis));

        database.addToResponseLog(responseLog);
        ResponseLog responseLogFromDb = database.getLastResponseByNum(phoneNum);
        assertNotNull(responseLogFromDb);

        assertTrue(responseLog.getMessageReceived().matches(responseLogFromDb.getMessageReceived()));
        assertTrue(responseLog.getMessageSent().matches(responseLogFromDb.getMessageSent()));
    }

    public boolean checker(Object A, Object B){
        if (A == B){
            return true;
        }
        return false;
    }

}