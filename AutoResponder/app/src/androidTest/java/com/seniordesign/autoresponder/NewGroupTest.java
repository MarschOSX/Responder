package com.seniordesign.autoresponder;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.seniordesign.autoresponder.Interface.Groups.NewGroup;
import com.seniordesign.autoresponder.Interface.Groups.SingleGroup;

public class NewGroupTest extends ActivityInstrumentationTestCase2<NewGroup> {
    public NewGroupTest() {
        super(NewGroup.class);
    }

    public void testActivityExists() {
        NewGroup activity = getActivity();
        assertNotNull(activity);
    }

    @UiThreadTest
    public void testHeaders(){
        NewGroup activity = getActivity();
        assertNotNull(activity);
        //activity.setContentView(R.layout.activity_single_contact);

        //Test Headers
        TextView groupName = (TextView) activity.findViewById(R.id.groupNameHeader);
        groupName.setText("Hello World");
        assertSame(groupName.getText().toString(), "Hello World");

        //Test Set Response Message
        EditText setTextEdit  = (EditText)activity.findViewById(R.id.newGroupName);
        setTextEdit.setText("Im Busy JUNIT");
        assertTrue(setTextEdit.getText().toString().matches("Im Busy JUNIT"));

        //Test Set Response Message
        EditText setTextEdit2  = (EditText)activity.findViewById(R.id.newGroupResponseText);
        setTextEdit2.setText("Im Busy JUNIT");
        assertTrue(setTextEdit2.getText().toString().matches("Im Busy JUNIT"));
    }

    @UiThreadTest
    public void testOnOffToggle() {
        //Test Switches
        NewGroup activity = getActivity();
        Switch location = (Switch)activity.findViewById(R.id.newGroupLocationToggle);
        assertNotNull(location);
        location.setChecked(true);
        assertTrue(location.isChecked());
        location.setChecked(false);
        assertFalse(location.isChecked());

        Switch calendar = (Switch)activity.findViewById(R.id.newGroupActivityToggle);
        assertNotNull(calendar);
        calendar.setChecked(true);
        assertTrue(calendar.isChecked());
        calendar.setChecked(false);
        assertFalse(calendar.isChecked());
    }

}