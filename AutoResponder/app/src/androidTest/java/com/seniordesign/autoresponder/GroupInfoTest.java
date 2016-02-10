package com.seniordesign.autoresponder;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.seniordesign.autoresponder.Interface.Groups.GroupInfo;

public class GroupInfoTest extends ActivityInstrumentationTestCase2<GroupInfo> {
    public GroupInfoTest() {
        super(GroupInfo.class);
    }

    public void testActivityExists() {
        GroupInfo activity = getActivity();
        assertNotNull(activity);
    }

    @UiThreadTest
    public void testHeaders(){
        GroupInfo activity = getActivity();
        assertNotNull(activity);
        //activity.setContentView(R.layout.activity_single_contact);

        //Test Headers
        TextView groupName = (TextView) activity.findViewById(R.id.singleGroupName);
        groupName.setText("Hello World");
        assertSame(groupName.getText().toString(), "Hello World");

        //Test Set Response Message
        EditText setTextEdit  = (EditText)activity.findViewById(R.id.singleGroupResponseText);
        setTextEdit.setText("Im Busy JUNIT");
        assertTrue(setTextEdit.getText().toString().matches("Im Busy JUNIT"));
    }

    @UiThreadTest
    public void testOnOffToggle() {
        //Test Switches
        GroupInfo activity = getActivity();
        Switch location = (Switch)activity.findViewById(R.id.singleGroupLocationToggle);
        assertNotNull(location);
        location.setChecked(true);
        assertTrue(location.isChecked());
        location.setChecked(false);
        assertFalse(location.isChecked());

        Switch calendar = (Switch)activity.findViewById(R.id.singleGroupActivityToggle);
        assertNotNull(calendar);
        calendar.setChecked(true);
        assertTrue(calendar.isChecked());
        calendar.setChecked(false);
        assertFalse(calendar.isChecked());
    }

    @UiThreadTest
    public void testContactListView() {
        GroupInfo activity = getActivity();

        //Populate the List
        ListView contactList = (ListView)activity.findViewById(R.id.singleGroupsList);
        String[] contactsNames = {"Hello1", "Test2", "Goodbye3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, contactsNames);
        contactList.setAdapter(adapter);
        //Get info back from the list
        String nameSelectedFromList = (String) contactList.getItemAtPosition(1);
        assertSame(nameSelectedFromList, "Test2");
        String nameSelectedFromList2 = (String) contactList.getItemAtPosition(2);
        assertSame(nameSelectedFromList2, "Goodbye3");
        String nameSelectedFromList3 = (String) contactList.getItemAtPosition(0);
        assertSame(nameSelectedFromList3, "Hello1");
        //clear list
        contactList.clearChoices();
    }

}