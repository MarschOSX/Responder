package com.seniordesign.autoresponder.Interface;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.DataStructures.Group;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;

import java.util.ArrayList;
import java.util.HashMap;

public class SingleGroup extends AppCompatActivity {

    private DBInstance db;
    Button setTextButton;
    EditText setTextEdit;
    Group singleGroup;
    String groupName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_group);
        this.db = DBProvider.getInstance(false, getApplicationContext());
        Intent intent = getIntent();
        groupName = intent.getStringExtra("GROUP_NAME");
        if(groupName == null) {
            singleGroup = new Group("JUnit test", "JUnit Response", false,false);
        }else{
            singleGroup = db.getGroupInfo(groupName);

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
                        //push generalReply to DB
                        db.setGroupResponse(groupName, groupReply);

                    }
                });


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
            contactInfo.put(contactsInGroup.get(i).getName(), contactsInGroup.get(i).getPhoneNumber());//This is for SingleContact activity
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactNames);
        final ListView contactsList = (ListView)findViewById(R.id.singleGroupsList);
        contactsList.setAdapter(adapter);

        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nameSelectedFromList = (String) contactsList.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), SingleContact.class);
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
        AlertDialog.Builder alert = new AlertDialog.Builder(SingleGroup.this);
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
                Intent intentBack = new Intent(getApplicationContext(), ManageGroups.class);
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
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        CharSequence toastText;
        Toast toast;
        Log.v("SingleGroup", "Feature Coming Soon!");
        toastText = "Feature Coming Soon!";
        toast = Toast.makeText(context, toastText, duration);
        toast.show();
    }

}
