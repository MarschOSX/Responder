package com.seniordesign.autoresponder;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import com.seniordesign.autoresponder.Interface.Settings.UserSettings;

//TODO WHAT IS THIS FOR?

public class SettingsTest extends ActivityInstrumentationTestCase2<UserSettings> {
    public SettingsTest() {
        super(UserSettings.class);
    }

    public void testActivityExists() {
        UserSettings activity = getActivity();
        assertNotNull(activity);
    }
    @UiThreadTest
    public void testGroupListView() {
        UserSettings activity = getActivity();

        //Populate the List
        //ListView testList = (ListView)activity.findViewById(R.id.settingsListView);
       /* String[] testPopulation = {"Hello1", "Test2", "Goodbye3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, testPopulation);
        assertNotNull(adapter);
        testList.setAdapter(adapter);
        //Get info back from the list
        String nameSelectedFromList = (String) testList.getItemAtPosition(1);
        assertSame(nameSelectedFromList, "Test2");
        String nameSelectedFromList2 = (String) testList.getItemAtPosition(2);
        assertSame(nameSelectedFromList2, "Goodbye3");
        String nameSelectedFromList3 = (String) testList.getItemAtPosition(0);
        assertSame(nameSelectedFromList3, "Hello1");
        //clear list
        testList.clearChoices();*/
    }
}