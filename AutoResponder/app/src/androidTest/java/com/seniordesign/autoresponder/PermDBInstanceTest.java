package com.seniordesign.autoresponder;

import android.test.AndroidTestCase;

import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import java.sql.Date;

/**
 * By MarschOSX on 10/24/2015
 */

public class PermDBInstanceTest extends AndroidTestCase {
    //Perm database
    private DBInstance database;

    public void testDBisNotNull()throws Exception {
        database = DBProvider.getInstance(false, getContext());
        assertNotNull(database);
    }

    public void testDbSetsAndGetsIssues()throws Exception {
        database = DBProvider.getInstance(false, getContext());
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

    public void testDBContactTableFunctions()throws Exception {
        database = DBProvider.getInstance(false, getContext());

        //Create Contact to test
        String name = "testSubjectA";
        String response = "test response";
        String phoneNum = "+12345678901";
        String newPhoneNum = "+12345678902";
        String group = "testGroupA";
        Contact contact = new Contact(name, phoneNum, group, response, true, true);

        //clear db just in case prior test failed and did not clear test rows
        database.removeContact(phoneNum);
        database.removeContact(newPhoneNum);

        database.addContact(contact);
        Contact contactFromDb = database.getContactInfo(phoneNum);
        assertNotNull(contactFromDb);
        assertTrue(contact.getName().matches(contactFromDb.getName()));
        assertTrue(contact.getPhoneNumber().compareTo(contactFromDb.getPhoneNumber()) == 0);
        assertTrue(contact.getGroupName().matches(contactFromDb.getGroupName()));
        assertTrue(contact.getResponse().matches(contactFromDb.getResponse()));
        assertTrue((contact.isActivityPermission() == contactFromDb.isActivityPermission()) && contact.isActivityPermission());
        assertTrue((contact.isLocationPermission() == contactFromDb.isLocationPermission()) && contact.isLocationPermission());

        name = "testSubjectB";
        database.setContactName(phoneNum, name);
        contactFromDb = database.getContactInfo(phoneNum);
        assertTrue(contactFromDb.getName().matches(name));

        database.setContactNumber(phoneNum, newPhoneNum);
        contactFromDb = database.getContactInfo(newPhoneNum);
        assertTrue(contactFromDb.getPhoneNumber().compareTo(newPhoneNum) == 0);

        group = "testGroupB";
        database.setContactGroup(newPhoneNum, group);
        contactFromDb = database.getContactInfo(newPhoneNum);
        assertTrue(contactFromDb.getGroupName().compareTo(group) == 0);

        database.setContactActivityPermission(newPhoneNum, false);
        contactFromDb = database.getContactInfo(newPhoneNum);
        assertTrue(!contactFromDb.isActivityPermission());

        database.setContactLocationPermission(newPhoneNum, false);
        contactFromDb = database.getContactInfo(newPhoneNum);
        assertTrue(!contactFromDb.isLocationPermission());

        int count = database.removeContact(newPhoneNum);
        assertTrue(count == 1);
        contactFromDb = database.getContactInfo(newPhoneNum);
        assertTrue(contactFromDb == null);
    }

    public boolean checker(Object A, Object B){
        if (A == B){
            return true;
        }
        return false;
    }

}