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
        String groupResponse = "test response2";
        String phoneNum = "+12345678901";
        String newPhoneNum = "+12345678902";
        String group = "testGroupA";
        String newGroup = "testGroupB";
        Contact contact = new Contact(name, phoneNum, group, groupResponse, true, true, false);
        Group testGroup = new Group(group, response, false, false);

        //clear db just in case prior test failed and did not clear test rows
        database.removeContact(phoneNum);
        database.removeContact(newPhoneNum);
        database.removeGroup(group);
        database.removeGroup(newGroup);

        assertTrue(database.addContact(contact) == -1);
        database.addGroup(testGroup);
        database.addContact(contact);

        Contact contactFromDb = database.getContactInfo(phoneNum);
        assertNotNull(contactFromDb);
        assertTrue(contact.getName().matches(contactFromDb.getName()));
        assertTrue(contact.getPhoneNumber().compareTo(contactFromDb.getPhoneNumber()) == 0);
        assertTrue(contact.getGroupName().matches(contactFromDb.getGroupName()));
        assertTrue(contact.getResponse().matches(contactFromDb.getResponse()));
        assertTrue((contact.isActivityPermission() == contactFromDb.isActivityPermission()) && contact.isActivityPermission());
        assertTrue((contact.isLocationPermission() == contactFromDb.isLocationPermission()) && contact.isLocationPermission());
        assertTrue((contact.isInheritance() == contactFromDb.isInheritance()) && !contact.isInheritance());

        name = "testSubjectB";
        database.setContactName(phoneNum, name);
        contactFromDb = database.getContactInfo(phoneNum);
        assertTrue(contactFromDb.getName().matches(name));

        database.setContactNumber(phoneNum, newPhoneNum);
        contactFromDb = database.getContactInfo(newPhoneNum);
        assertTrue(contactFromDb.getPhoneNumber().compareTo(newPhoneNum) == 0);

        assertTrue(database.setContactGroup(newPhoneNum, newGroup) == -1);
        database.changeGroupName(group, newGroup);
        database.setContactGroup(newPhoneNum, newGroup);
        contactFromDb = database.getContactInfo(newPhoneNum);
        assertTrue(contactFromDb.getGroupName().compareTo(newGroup) == 0);

        response = "busy";
        database.setContactResponse(newPhoneNum, response);
        contactFromDb = database.getContactInfo(newPhoneNum);
        assertTrue(contactFromDb.getResponse().compareTo(response) == 0);

        database.setContactActivityPermission(newPhoneNum, false);
        contactFromDb = database.getContactInfo(newPhoneNum);
        assertTrue(!contactFromDb.isActivityPermission());

        database.setContactLocationPermission(newPhoneNum, false);
        contactFromDb = database.getContactInfo(newPhoneNum);
        assertTrue(!contactFromDb.isLocationPermission());

        database.setContactInheritance(newPhoneNum, true);
        contactFromDb = database.getContactInfo(newPhoneNum);
        assertTrue(contactFromDb.isInheritance());
        assertTrue(contactFromDb.getResponse().matches(testGroup.getResponse()));
        assertTrue((contactFromDb.isActivityPermission() == testGroup.isActivityPermission()) && !contactFromDb.isActivityPermission());
        assertTrue((contactFromDb.isLocationPermission() == testGroup.isLocationPermission()) && !contactFromDb.isLocationPermission());
        database.setContactInheritance(newPhoneNum, false);

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
        database.removeContact("+17172223311");


        ArrayList<Contact> contactTable = new ArrayList<>();
        int[] sequence = {7, 2, 0, 1, 6, 3, 5, 4};

        //load into arrayList in A to Z order
        contactTable.add(new Contact("testSubjectA", "+17172223333", Group.DEFAULT_GROUP, "response", false, false, false));
        contactTable.add(new Contact("testSubjectB", "+17172223334", Group.DEFAULT_GROUP, "response", false, false, false));
        contactTable.add(new Contact("testSubjectC", "+17172223335", Group.DEFAULT_GROUP, "response", false, false, false));
        contactTable.add(new Contact("testSubjectD", "+17172223336", Group.DEFAULT_GROUP, "response", false, false, false));
        contactTable.add(new Contact("testSubjectE", "+17172223337", Group.DEFAULT_GROUP, "response", false, false, false));
        contactTable.add(new Contact("testSubjectF", "+17172223338", Group.DEFAULT_GROUP, "response", false, false, false));
        contactTable.add(new Contact("testSubjectG", "+17172223339", Group.DEFAULT_GROUP, "response", false, false, false));
        contactTable.add(new Contact("testSubjectH", "+17172223310", Group.DEFAULT_GROUP, "response", false, false, false));

        //add to db out of order
        for(int i : sequence) {
            database.addContact(contactTable.get(i));
        }

        ArrayList<Contact> contactTableFromDB = database.getContactList();

        //verifies that it gives to you out of order
        for (int i = 0; i < contactTable.size(); i++){
            //Log.d("FINDME", contactTableFromDB.get(i).toString());
            assertTrue(contactTableFromDB.get(i).getName().compareTo(contactTable.get(i).getName()) == 0);
        }

        database.addContact(new Contact("testSubjectI", "+17172223311", newGroup, "response", false, false, false));
        contactTableFromDB = database.getGroup(Group.DEFAULT_GROUP);
        //verifies that it gives to you out of order
        for (int i = 0; i < contactTable.size(); i++){
            //Log.d("FINDME", contactTableFromDB.get(i).toString());
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
        database.removeContact("+17172223311");
        database.removeGroup(group);
        database.removeGroup(newGroup);
    }

    public void testGroupTableFunctions() throws Exception{
        database = DBProvider.getInstance(false, getContext());

        String groupName = "testGroupA";
        String newGroupName = "testGroupB";
        String response = "group_test_response";
        Group group = new Group(groupName,response, true, true);
        Group groupFromDB;

        //in case a previously ran test failed
        database.removeGroup(groupName);
        database.removeGroup(newGroupName);

        database.addGroup(group);
        groupFromDB = database.getGroupInfo(groupName);
        assertNotNull(groupFromDB);
        assertTrue(group.getGroupName().matches(groupFromDB.getGroupName()));
        assertTrue(group.getResponse().matches(groupFromDB.getResponse()));
        assertTrue((group.isActivityPermission() == groupFromDB.isActivityPermission()) && group.isActivityPermission());
        assertTrue((group.isLocationPermission() == groupFromDB.isLocationPermission()) && group.isLocationPermission());

        database.changeGroupName(groupName, newGroupName);
        groupFromDB = database.getGroupInfo(newGroupName);
        assertTrue(groupFromDB.getGroupName().compareTo(newGroupName) == 0);

        response = "new_test_response";
        database.setGroupResponse(newGroupName, response);
        groupFromDB = database.getGroupInfo(newGroupName);
        assertTrue(groupFromDB.getResponse().compareTo(response) == 0);

        database.setGroupActivityPermission(newGroupName, false);
        groupFromDB = database.getGroupInfo(newGroupName);
        assertTrue(!groupFromDB.isActivityPermission());

        database.setGroupLocationPermission(newGroupName, false);
        groupFromDB = database.getGroupInfo(newGroupName);
        assertTrue(!groupFromDB.isLocationPermission());

        //remove test group and verify only 1 entry removed
        int count = database.removeGroup(newGroupName);
        assertTrue(count == 1);
        assertNull(database.getGroupInfo(newGroupName));

        ArrayList<Group> groupsTable = new ArrayList<>();
        int[] sequence = {7, 2, 0, 1, 6, 3, 5, 4};

        //in case previous unit test failed
        database.removeGroup("testSubjectA");
        database.removeGroup("testSubjectB");
        database.removeGroup("testSubjectC");
        database.removeGroup("testSubjectD");
        database.removeGroup("testSubjectE");
        database.removeGroup("testSubjectF");
        database.removeGroup("testSubjectG");
        database.removeGroup("testSubjectH");

        //load into arrayList in A to Z order
        groupsTable.add(new Group("testSubjectA", "response", false, false));
        groupsTable.add(new Group("testSubjectB", "response", false, false));
        groupsTable.add(new Group("testSubjectC", "response", false, false));
        groupsTable.add(new Group("testSubjectD", "response", false, false));
        groupsTable.add(new Group("testSubjectE", "response", false, false));
        groupsTable.add(new Group("testSubjectF", "response", false, false));
        groupsTable.add(new Group("testSubjectG", "response", false, false));
        groupsTable.add(new Group("testSubjectH", "response", false, false));

        //add to db out of order
        for(int i : sequence) {
            database.addGroup(groupsTable.get(i));
        }

        ArrayList<Group> groupTableFromDB = database.getGroupList();

        //verifies that it gives to you out of order
        for (int i = 0; i < groupsTable.size(); i++){
            //Log.d("FINDME", contactTableFromDB.get(i).toString());
            assertTrue(groupTableFromDB.get(i).getGroupName().compareTo(groupTableFromDB.get(i).getGroupName()) == 0);
        }

        database.removeGroup("testSubjectA");
        database.removeGroup("testSubjectB");
        database.removeGroup("testSubjectC");
        database.removeGroup("testSubjectD");
        database.removeGroup("testSubjectE");
        database.removeGroup("testSubjectF");
        database.removeGroup("testSubjectG");
        database.removeGroup("testSubjectH");
    }

    public boolean checker(Object A, Object B){
        if (A == B){
            return true;
        }
        return false;
    }

}