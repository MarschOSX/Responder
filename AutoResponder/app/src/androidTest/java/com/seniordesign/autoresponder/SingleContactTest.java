package com.seniordesign.autoresponder;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.seniordesign.autoresponder.Interface.SingleContact;

public class SingleContactTest extends ActivityInstrumentationTestCase2<SingleContact> {
    public SingleContactTest() {
        super(SingleContact.class);
    }

    public void testActivityExists(){
        SingleContact activity = getActivity();
        assertNotNull(activity);
    }

    @UiThreadTest
    public void testHeaders(){
        SingleContact activity = getActivity();
        assertNotNull(activity);
        //activity.setContentView(R.layout.activity_single_contact);

        //Test Headers
        TextView contactName = (TextView) activity.findViewById(R.id.singleContactNameOfContact);
        TextView contactNumber = (TextView) activity.findViewById(R.id.contactPhoneNumberTextView);
        contactName.setText("Hello World");
        contactNumber.setText("8888888888");
        assertSame(contactName.getText().toString(), "Hello World");
        assertSame(contactNumber.getText().toString(), "8888888888");

        //Test Set Response Message
        EditText setTextEdit  = (EditText)activity.findViewById(R.id.contactResponse_text);
        setTextEdit.setText("Im Busy JUNIT");
        assertTrue(setTextEdit.getText().toString().matches("Im Busy JUNIT"));
    }

    @UiThreadTest
    public void testOnOffToggle() {
        //Test Switches
        SingleContact activity = getActivity();
        Switch location = (Switch)activity.findViewById(R.id.contactLocationToggle);
        assertNotNull(location);
        location.setChecked(true);
        assertTrue(location.isChecked());
        location.setChecked(false);
        assertFalse(location.isChecked());

        Switch calendar = (Switch)activity.findViewById(R.id.contactActivityToggle);
        assertNotNull(calendar);
        calendar.setChecked(true);
        assertTrue(calendar.isChecked());
        calendar.setChecked(false);
        assertFalse(calendar.isChecked());
    }


}