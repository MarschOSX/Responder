package com.seniordesign.autoresponder;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.seniordesign.autoresponder.Interface.Groups.GroupList;

public class GroupListTest extends ActivityInstrumentationTestCase2<GroupList> {
    public GroupListTest() {
        super(GroupList.class);
    }

    public void testActivityExists() {
        GroupList activity = getActivity();
        assertNotNull(activity);
    }


    @UiThreadTest
    public void testGroupListView() {
        GroupList activity = getActivity();

        //Populate the List
        ListView contactList = (ListView)activity.findViewById(R.id.groupsList);
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

    @UiThreadTest
    public void testIntent() {
        GroupList activity = getActivity();
        Intent intent = activity.getIntent();
        String groupName = intent.getStringExtra("CONTACT_NUMBER");
        assertNull(groupName);
    }
}