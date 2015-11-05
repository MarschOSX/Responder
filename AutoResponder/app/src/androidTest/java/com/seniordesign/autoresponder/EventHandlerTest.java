package com.seniordesign.autoresponder;
import android.test.InstrumentationTestCase;

import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.DataStructures.DeveloperLog;
import com.seniordesign.autoresponder.DataStructures.Group;
import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Receiver.EventHandler;
import org.mockito.Mock;
import java.sql.Date;
import java.util.ArrayList;

/**
 * By MarschOSX on 10/24/2015
 */

public class EventHandlerTest extends InstrumentationTestCase {
    @Mock
    private DBInstance dbi;

    public void testRespondToText() throws Exception {
        dbi = new DBInstance() {
            @Override
            public void setReplyAll(String reply) {

            }

            @Override
            public String getReplyAll() {
                return "I am busy right now JUNIT";
            }

            @Override
            public void setDelay(int minutes) {

            }

            @Override
            public int getDelay() {
                return 0;
            }

            @Override
            public void setResponseToggle(boolean toggle) {

            }

            @Override
            public boolean getResponseToggle() {
                return true;
            }

            @Override
            public void addToResponseLog(ResponseLog newLog) {

            }

            @Override
            public ResponseLog getFirstResponse() {
                return null;
            }

            @Override
            public ResponseLog getLastResponse() {
                return null;
            }

            @Override
            public ResponseLog getResponse(int index) {
                return null;
            }

            @Override
            public ResponseLog getLastResponseByNum(String phoneNum) {
                Date testDate = new Date(0);
                ResponseLog updateLog = new ResponseLog("JUNIT MESSAGE SENT",
                        "JUNIT MESSAGE RECIEVED", "+18568327320", testDate);
                return updateLog;
            }

            @Override
            public ArrayList<ResponseLog> getResponseByDateRange(Date start, Date end) {
                return null;
            }

            @Override
            public ArrayList<ResponseLog> getResponseRange(int start, int end) {
                return null;
            }

            public int addContact(Contact newContact) { return -1;}

            public int removeContact(String phoneNum) { return -1;}

            public int setContactName(String phoneNum, String newName) { return -1;}

            public int setContactNumber(String oldPhoneNum, String newPhoneNum) { return -1;}

            public int setContactResponse(String phoneNum, String response) { return -1;}

            public int setContactLocationPermission(String phoneNum, boolean permission) { return -1;}

            public int setContactActivityPermission(String phoneNum, boolean permission) { return -1;}

            public int setContactGroup(String phoneNum, String groupName) { return -1;}

            public Contact getContactInfo(String phoneNum) { return null;}

            //returns sorted A - Z by name
            public ArrayList<Contact> getContactList() { return null;}

            public ArrayList<Contact> getGroup(String groupName) { return null;}

            /////////////////////////
            //GROUP TABLE FUNCTIONS//
            /////////////////////////

            public int addGroup(Group newGroup) { return -1;}

            public int removeGroup(String groupName) { return -1;}

            public int changeGroupName(String oldName, String newName) { return -1;}

            public int setGroupResponse(String groupName, String response) { return -1;}

            public int setGroupLocationPermission(String groupName, boolean permission) { return -1;}

            public int setGroupActivityPermission(String groupName, boolean permission) { return -1;}

            public Group getGroupInfo(String groupName) { return null;}

            //returns sorted A-Z by group name
            public ArrayList<Group> getGroupList() { return null;}

            /////////////////////////////////
            //DEVELOPER LOG TABLE FUNCTIONS//
            /////////////////////////////////

            public void addDevLog(Date timeStamp, String entry){

            }

            public DeveloperLog getDevLog(int index) { return null;}

            public ArrayList<DeveloperLog> getDevLogRange(int first, int last) { return null;}

            public ArrayList<DeveloperLog> getDevLogRangeByDate(Date start, Date end) { return null;}
        };

        //uncomment this to annoy martin
        //for(int i = 0; i < 100; i++) {
        assertNotNull(dbi);
        EventHandler ev = new EventHandler(dbi);
        assertSame(ev.respondToText(null, "", 0L, true), -1);
        assertSame(ev.respondToText("", "", 0L, true), -1);
        assertSame(ev.respondToText("1", "", 0L, true), -1);
        assertSame(ev.respondToText("123z135", "", 0L, true), -1);
        assertSame(ev.respondToText("85683273201111", "", 0L, true), -1);
        assertSame(ev.respondToText("+18568327320", "", -1L, true), -1);
        assertSame(ev.respondToText("+18568327320", "", 0L, true), 0);
        //}
    }
}