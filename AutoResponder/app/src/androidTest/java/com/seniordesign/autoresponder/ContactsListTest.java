package com.seniordesign.autoresponder;

import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;

import com.seniordesign.autoresponder.Interface.Contacts.ContactsList;
import com.seniordesign.autoresponder.Interface.Main;

public class ContactsListTest extends ActivityInstrumentationTestCase2<ContactsList> {
    public ContactsListTest() {
        super(ContactsList.class);
    }

    public void testActivityExists() {
        ContactsList activity = getActivity();
        assertNotNull(activity);
    }

    @UiThreadTest
    public void testContactListView() {
        ContactsList activity = getActivity();

        //Populate the List
        ListView contactList = (ListView)activity.findViewById(R.id.contactList);
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
        ContactsList activity = getActivity();
        Intent intent = activity.getIntent();
        String groupName = intent.getStringExtra("ADD_CONTACT_TO_SINGLE_GROUP");
        assertNull(groupName);
    }
}