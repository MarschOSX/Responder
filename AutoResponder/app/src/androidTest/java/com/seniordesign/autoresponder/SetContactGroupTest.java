package com.seniordesign.autoresponder;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.seniordesign.autoresponder.Interface.SetContactGroup;
import com.seniordesign.autoresponder.Interface.SingleGroup;

public class SetContactGroupTest extends ActivityInstrumentationTestCase2<SetContactGroup> {
    public SetContactGroupTest() {
        super(SetContactGroup.class);
    }

    public void testActivityExists() {
        SetContactGroup activity = getActivity();
        assertNotNull(activity);
    }

    @UiThreadTest
    public void testHeaders(){
        SetContactGroup activity = getActivity();
        assertNotNull(activity);
        //activity.setContentView(R.layout.activity_single_contact);

        //Test Headers
        TextView groupName = (TextView) activity.findViewById(R.id.currentGroupName);
        groupName.setText("Hello World");
        assertSame(groupName.getText().toString(), "Hello World");
    }



}