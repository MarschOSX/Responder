package com.seniordesign.autoresponder;

import android.test.AndroidTestCase;
import android.util.Log;

import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.DataStructures.Group;
import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import java.sql.Date;
import java.util.ArrayList;

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

        //in case test failed
        database.removeContact("+17172223333");
        database.removeContact("+17172223334");
        database.removeContact("+17172223335");
        database.removeContact("+17172223336");
        database.removeContact("+17172223337");
        database.removeContact("+17172223338");
        database.removeContact("+17172223339");
        database.removeContact("+17172223310");

        ArrayList<Contact> contactTable = new ArrayList<>();

        //load into arrayList in A to Z order
        contactTable.add(new Contact("testSubjectA", "+17172223333", Group.DEFAULT_GROUP, "response", false, false));
        contactTable.add(new Contact("testSubjectB", "+17172223334", Group.DEFAULT_GROUP, "response", false, false));
        contactTable.add(new Contact("testSubjectC", "+17172223335", Group.DEFAULT_GROUP, "response", false, false));
        contactTable.add(new Contact("testSubjectD", "+17172223336", Group.DEFAULT_GROUP, "response", false, false));
        contactTable.add(new Contact("testSubjectE", "+17172223337", Group.DEFAULT_GROUP, "response", false, false));
        contactTable.add(new Contact("testSubjectF", "+17172223338", Group.DEFAULT_GROUP, "response", false, false));
        contactTable.add(new Contact("testSubjectG", "+17172223339", Group.DEFAULT_GROUP, "response", false, false));
        contactTable.add(new Contact("testSubjectH", "+17172223310", Group.DEFAULT_GROUP, "response", false, false));

        //add to db out of order
        database.addContact(contactTable.get(7));
        database.addContact(contactTable.get(2));
        database.addContact(contactTable.get(0));
        database.addContact(contactTable.get(1));
        database.addContact(contactTable.get(6));
        database.addContact(contactTable.get(3));
        database.addContact(contactTable.get(5));
        database.addContact(contactTable.get(4));

        ArrayList<Contact> contactTableFromDB = database.getContactList();

        for (int i = 0; i < contactTable.size(); i++){
            Log.d("FINDME", contactTableFromDB.get(i).toString());
            assertTrue(contactTableFromDB.get(i).getName().compareTo(contactTable.get(i).getName()) == 0);
        }

        database.removeContact("+17172223333");
        database.removeContact("+17172223334");
        database.removeContact("+17172223335");
        database.removeContact("+17172223336");
        database.removeContact("+17172223337");
        database.removeContact("+17172223338");
        database.removeContact("+17172223339");
        database.removeContact("+17172223310");
    }

    public boolean checker(Object A, Object B){
        if (A == B){
            return true;
        }
        return false;
    }

}