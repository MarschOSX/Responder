package com.seniordesign.autoresponder.Interface.Contacts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.DataStructures.Group;
import com.seniordesign.autoresponder.Interface.Groups.GroupInfo;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;

public class ContactInfo extends AppCompatActivity {

    private DBInstance db;
    Button setTextButton;
    EditText setTextEdit;
    Contact singleContact;
    String phoneNumber;
    String fromSingleGroup = null;
    TextView changeContactName;
    private String TAG = "ContactInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_contact);
        Intent intent = getIntent();
        this.db = DBProvider.getInstance(false, getApplicationContext());
        phoneNumber = intent.getStringExtra("SINGLE_CONTACT_NUMBER");
        fromSingleGroup = intent.getStringExtra("FROM_SINGLE_GROUP");

        if(phoneNumber == null) {//i am unit testing
            singleContact = new Contact("Test","555", "Default", "Hello JUnit", false, false, false);
        }else{
            singleContact = db.getContactInfo(phoneNumber);
        }

        setUpContactInfo(singleContact);

        setTextButton = (Button)findViewById(R.id.setContactTextButton);
        setTextEdit   = (EditText)findViewById(R.id.contactResponse_text);


        setTextButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        String contactReply = setTextEdit.getText().toString();
                        Log.v("Contact Reply:", contactReply);

                        if (contactReply.matches("")) {//Its blank, get default hint
                            contactReply = setTextEdit.getHint().toString();
                        }
                        setTextEdit.setText(null);
                        db.setContactResponse(phoneNumber, contactReply);
                        Log.v("Single Contact:", "Set " + phoneNumber + " reply to " + contactReply);
                        singleContact = db.getContactInfo(phoneNumber);
                        setUpContactInfo(singleContact);
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_single_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setUpContactInfo(Contact singleContact){
        boolean isInheriting = db.getContactInfo(singleContact.getPhoneNumber()).isInheritance();

        //Contact Info
        TextView contactName = (TextView) findViewById(R.id.singleContactNameOfContact);
        TextView contactNumber = (TextView) findViewById(R.id.contactPhoneNumberTextView);
        contactName.setText(singleContact.getName());
        contactNumber.setText(singleContact.getPhoneNumber());

        //Reply to this contact
        String contactReply = singleContact.getResponse();


        EditText contactResponse = (EditText)findViewById(R.id.contactResponse_text);
        contactResponse.setHint(contactReply);
        //contactResponse.setFocusable(false);


        //Permission Switches
        Switch location = (Switch)findViewById(R.id.contactLocationToggle);
        Switch calendar = (Switch)findViewById(R.id.contactActivityToggle);
        if(isInheriting) {
            if (db.getGroupInfo(singleContact.getGroupName()).isLocationPermission()) {
                location.setChecked(true);
            } else {
                location.setChecked(false);
            }
            if (db.getGroupInfo(singleContact.getGroupName()).isActivityPermission()) {
                calendar.setChecked(true);
            } else {
                calendar.setChecked(false);
            }
        }else{
            if(singleContact.isLocationPermission()){
                location.setChecked(true);
            }else{
                location.setChecked(false);
            }
            if(singleContact.isActivityPermission()){
                calendar.setChecked(true);
            }else{
                calendar.setChecked(false);
            }
        }





        //Contact Group
        TextView contactsGroup = (TextView) findViewById(R.id.contactGroupName);
        Log.v("Single Contact:", "Group Name is: " + singleContact.getGroupName());
        if(!singleContact.getGroupName().matches(Group.DEFAULT_GROUP)){
            Log.v("ContactInfo", "reached");
            contactsGroup.setHint(singleContact.getGroupName());
        }else{
            Log.v("ContactInfo", "default reached");
            contactsGroup.setHint("Default");
        }

        //to determine if greyed out text based on Inheritance from group info
        Switch inheritance = (Switch)findViewById(R.id.SingleContactInheritance);
        if(isInheriting){
            Button setContactTextButton = (Button) findViewById(R.id.setContactTextButton);
            contactResponse.setEnabled(false);
            setContactTextButton.setEnabled(false);
            contactResponse.setEnabled(false);
            location.setEnabled(false);
            calendar.setEnabled(false);
            inheritance.setChecked(true);
        }else{
            Button setContactTextButton = (Button) findViewById(R.id.setContactTextButton);
            contactResponse.setEnabled(true);
            setContactTextButton.setEnabled(true);
            contactResponse.setEnabled(true);
            location.setEnabled(true);
            calendar.setEnabled(true);
            inheritance.setChecked(false);
        }



    }



    // Called when the user selects a time delay radio button
    public void contactSwitchChecker(View view) {
        // Is the button now checked?
        boolean isToggled = ((Switch) view).isChecked();
        // Check which radio button was clicked
        if(!db.getContactInfo(phoneNumber).isInheritance()) {
            switch (view.getId()) {
                case R.id.contactLocationToggle:
                    Log.v("ContactLocationToggle:", java.lang.Boolean.toString(isToggled));
                    db.setContactLocationPermission(singleContact.getPhoneNumber(), isToggled);
                    break;
                case R.id.contactActivityToggle:
                    Log.v("ContactActivityToggle:", java.lang.Boolean.toString(isToggled));
                    db.setContactActivityPermission(singleContact.getPhoneNumber(), isToggled);
                    break;
            }
            singleContact = db.getContactInfo(phoneNumber);
            setUpContactInfo(singleContact);
        }else{
            Switch location = (Switch)findViewById(R.id.contactLocationToggle);
            Switch calendar = (Switch)findViewById(R.id.contactActivityToggle);
            location.setChecked(location.isChecked());
            calendar.setChecked(calendar.isChecked());
        }
    }

    public void deleteSingleContact(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(ContactInfo.this);
        alert.setTitle("Delete Contact: "+singleContact.getName());
        alert.setMessage("Are you sure you want to delete this contact?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do your work here
                Log.v("SingleContactDelete:", "YES");
                db.removeContact(singleContact.getPhoneNumber());
                dialog.dismiss();
                if (fromSingleGroup != null) {
                    Intent intentBack = new Intent(getApplicationContext(), GroupInfo.class);
                    intentBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentBack.putExtra("GROUP_NAME", fromSingleGroup);
                    startActivity(intentBack);
                    finish();
                }else{
                    Intent intentBack = new Intent(getApplicationContext(), ContactsList.class);
                    intentBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentBack);
                    finish();
                }
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v("SingleContactDelete:", "NO");
                dialog.dismiss();
            }
        });

        alert.show();
    }

    public void setGroup(View view) {
        Intent intent = new Intent(getApplicationContext(), SetContactGroup.class);
        intent.putExtra("SINGLE_CONTACT_NUMBER", phoneNumber);
        intent.putExtra("FROM_SINGLE_GROUP", fromSingleGroup);
        startActivity(intent);
    }

    public void setInheritance(View view) {
        boolean isToggled = ((Switch) view).isChecked();
        switch(view.getId()) {
            case R.id.SingleContactInheritance:
                Log.v("SingleContInheritance:", java.lang.Boolean.toString(isToggled));
                    db.setContactInheritance(phoneNumber, isToggled);
                    //singleContact.setInheritance(isToggled);
                    singleContact = db.getContactInfo(phoneNumber);
                    setUpContactInfo(singleContact);
                break;
        }
    }

    public void changeSingleContactName(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Change Contact Name");
        alert.setMessage("Please enter a new name for this contact. Cannot be blank or have spaces:");
        changeContactName = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        changeContactName.setLayoutParams(lp);
        alert.setView(changeContactName);
        alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do your work here
                Log.v(TAG, "Proceed with changing Contact name");
                String newContactName = changeContactName.getText().toString();
                Log.v(TAG, "New Contact Name entered: " + newContactName);
                if (newContactName.matches("")) {
                    int duration = Toast.LENGTH_LONG;
                    CharSequence toastText;
                    Toast toast;
                    toastText = "Invalid Name Entered! Cannot be blank or have spaces!";
                    toast = Toast.makeText(getApplicationContext(), toastText, duration);
                    toast.show();
                } else {
                    db.changeContactName(singleContact.getName(), newContactName);
                    int duration = Toast.LENGTH_LONG;
                    CharSequence toastText;
                    Toast toast;
                    toastText = "Contact Name Changed!";
                    toast = Toast.makeText(getApplicationContext(), toastText, duration);
                    toast.show();
                    finish();
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(TAG, "Canceled Contact Name Change");
            }
        });
        alert.show();
    }
}