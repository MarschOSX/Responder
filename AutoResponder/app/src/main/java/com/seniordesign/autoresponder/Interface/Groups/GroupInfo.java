package com.seniordesign.autoresponder.Interface.Groups;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.DataStructures.Group;
import com.seniordesign.autoresponder.Interface.Contacts.ContactInfo;
import com.seniordesign.autoresponder.Interface.Contacts.ContactsList;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupInfo extends AppCompatActivity {

    private DBInstance db;
    private String TAG = "GroupInfo";
    Button setTextButton;
    EditText setTextEdit;
    Group singleGroup;
    String groupNameString = null;
    String newGroupName = null;
    EditText changeGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_group);
        this.db = DBProvider.getInstance(false, getApplicationContext());
        Intent intent = getIntent();
        groupNameString = intent.getStringExtra("GROUP_NAME");
        if(groupNameString == null) {
            singleGroup = new Group("JUnit test", "JUnit Response", false,false);
        }else{
            singleGroup = db.getGroupInfo(groupNameString);

        }
        setUpGroupInfo(singleGroup);

        setTextButton = (Button)findViewById(R.id.setSingleGroupResponseTextButton);
        setTextEdit   = (EditText)findViewById(R.id.singleGroupResponseText);

        setTextButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        String groupReply = setTextEdit.getText().toString();
                        Log.v("Group Reply:", groupReply);

                        if (groupReply.matches("")) {//Its blank, get default hint
                            groupReply = setTextEdit.getHint().toString();
                        }
                        setTextEdit.setText(null);

                        //push generalReply to DB
                        db.setGroupResponse(groupNameString, groupReply);

                    }
                });


    }

    @Override
    protected void onResume(){
        super.onResume();
        setUpGroupInfo(singleGroup);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_single_group, menu);
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

    public void setUpGroupInfo(Group singleGroup) {
        //Set Group Name
        final TextView groupName = (TextView) findViewById(R.id.singleGroupName);
        groupName.setText(singleGroup.getGroupName());


        //Set Single Group Response
        EditText groupResponse = (EditText) findViewById(R.id.singleGroupResponseText);
        groupResponse.setHint(singleGroup.getResponse());

        //Permission Switches
        Switch location = (Switch)findViewById(R.id.singleGroupLocationToggle);
        Switch calendar = (Switch)findViewById(R.id.singleGroupActivityToggle);
        if(singleGroup.isLocationPermission()){
            location.setChecked(true);
        }else{
            location.setChecked(false);
        }
        if(singleGroup.isActivityPermission()){
            calendar.setChecked(true);
        }else{
            calendar.setChecked(false);
        }

        // storing string resources into Array
        int numberOfContactsInGroup = 0;
        ArrayList<Contact> contactsInGroup = db.getGroup(singleGroup.getGroupName());
        if(contactsInGroup != null){
            numberOfContactsInGroup = contactsInGroup.size();
        }

        String[] contactNames = new String[numberOfContactsInGroup];
        final HashMap<String, String> contactInfo = new HashMap<>();

        for(int i = 0; i < numberOfContactsInGroup; i++){
            contactNames[i] = contactsInGroup.get(i).getName();//this is for the ListView
            Log.v("ContactNames: ", contactsInGroup.get(i).getName());
            contactInfo.put(contactsInGroup.get(i).getName(), contactsInGroup.get(i).getPhoneNumber());//This is for ContactInfo activity
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactNames);
        final ListView contactsList = (ListView)findViewById(R.id.singleGroupsList);
        contactsList.setAdapter(adapter);

        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nameSelectedFromList = (String) contactsList.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), ContactInfo.class);
                //Based on selection from list view, open new activity based on that contact
                if(contactInfo.containsKey(nameSelectedFromList)){
                    String number = contactInfo.get(nameSelectedFromList);
                    intent.putExtra("SINGLE_CONTACT_NUMBER", number);
                    intent.putExtra("FROM_SINGLE_GROUP", groupName.getText());
                    Log.v("ContactList hast Name", nameSelectedFromList);
                    Log.v("ContactList hash Number", number);
                    startActivity(intent);
                }
            }
        });

    }

    public void singleGroupSwitchSetter(View view) {
        // Is the button now checked?
        boolean isToggled = ((Switch) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.singleGroupLocationToggle:
                Log.v("SingleGroupLoc:", java.lang.Boolean.toString(isToggled));
                db.setGroupLocationPermission(singleGroup.getGroupName(), isToggled);
                break;
            case R.id.singleGroupActivityToggle:
                Log.v("SingleGroupAct:", java.lang.Boolean.toString(isToggled));
                db.setGroupActivityPermission(singleGroup.getGroupName(), isToggled);
                break;
        }
    }

    public void deleteSingleGroup(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(GroupInfo.this);
        alert.setTitle("Delete Group: "+singleGroup.getGroupName());
        alert.setMessage("Are you sure you want to delete this group?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do your work here
                Log.v("SingleGroupDelete:", "YES");
                // storing string resources into Array
                int numberOfContactsInGroup = 0;
                ArrayList<Contact> contactsInGroup = db.getContactList();
                if(contactsInGroup != null){
                    numberOfContactsInGroup = contactsInGroup.size();
                }

                for(int i = 0; i < numberOfContactsInGroup; i++){
                    Log.v("Contact: ", contactsInGroup.get(i).getName());
                    Log.v("Contact Group : ", contactsInGroup.get(i).getGroupName());
                    if(contactsInGroup.get(i).getGroupName().matches(singleGroup.getGroupName())) {
                        Log.v("Group", "This will be changed!!");
                        db.setContactGroup(contactsInGroup.get(i).getPhoneNumber(), Group.DEFAULT_GROUP);
                    }
                    Log.v("Contact: ", contactsInGroup.get(i).getName());
                    Log.v("Contact Group : ", contactsInGroup.get(i).getGroupName());
                }
                db.removeGroup(singleGroup.getGroupName());
                dialog.dismiss();
                Intent intentBack = new Intent(getApplicationContext(), GroupList.class);
                intentBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentBack);
                finish();
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v("SingleGroupDelete:", "NO");
                dialog.dismiss();
            }
        });

        alert.show();
    }

    public void singleGroupAddContact(View view){
        Intent intent = new Intent(getApplicationContext(), ContactsList.class);
        intent.putExtra("ADD_CONTACT_TO_SINGLE_GROUP", singleGroup.getGroupName());
        startActivity(intent);
    }

    public void changeSingleGroupName(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Change Group Name");
        alert.setMessage("Please enter a new name for this group. Cannot be blank or have spaces:");
        changeGroupName = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        changeGroupName.setLayoutParams(lp);
        alert.setView(changeGroupName);
        alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do your work here
                Log.v(TAG, "Proceed with changing group name");
                newGroupName = changeGroupName.getText().toString();
                Log.v(TAG, "New Group Name entered: " + newGroupName);
                if (newGroupName == null || newGroupName.matches("") || newGroupName.contains(" ")) {
                    int duration = Toast.LENGTH_LONG;
                    CharSequence toastText;
                    Toast toast;
                    toastText = "Invalid Name Entered! Cannot be blank or have spaces!";
                    toast = Toast.makeText(getApplicationContext(), toastText, duration);
                    toast.show();
                } else {
                    if (db.getGroupInfo(newGroupName) == null) {
                        db.changeGroupName(singleGroup.getGroupName(), newGroupName);
                        int duration = Toast.LENGTH_LONG;
                        CharSequence toastText;
                        Toast toast;
                        toastText = "Group Name Changed!";
                        toast = Toast.makeText(getApplicationContext(), toastText, duration);
                        toast.show();
                        finish();
                    } else {
                        int duration = Toast.LENGTH_LONG;
                        CharSequence toastText;
                        Toast toast;
                        toastText = "Group Name Already Exists! Cannot Have 2 of the same Group!";
                        toast = Toast.makeText(getApplicationContext(), toastText, duration);
                        toast.show();
                        finish();
                    }
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(TAG, "Canceled Group Name Change");
            }
        });
        alert.show();
    }

}
