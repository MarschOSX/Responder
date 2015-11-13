package com.seniordesign.autoresponder.Interface;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.DataStructures.Group;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;

public class NewGroup extends AppCompatActivity {

    private DBInstance db;
    Button setNameButton;
    Button setResponseTextButton;
    EditText groupNameEditText;
    EditText groupResponseEditText;

    String groupName;
    String groupResponse;
    Boolean groupLocation = false;
    Boolean groupActivity = false;
    int duration = Toast.LENGTH_LONG;
    CharSequence toastText;
    Toast toast;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        this.db = DBProvider.getInstance(false, getApplicationContext());


        final Context context = getApplicationContext();
        setNameButton = (Button)findViewById(R.id.setNewGroupName);
        setResponseTextButton = (Button)findViewById(R.id.setNewGroupResponseText);
        groupNameEditText = (EditText)findViewById(R.id.newGroupName);
        groupResponseEditText = (EditText)findViewById(R.id.newGroupResponseText);

        setNameButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        Group doesGroupExist = db.getGroupInfo(groupNameEditText.getText().toString());
                        if (groupNameEditText.getText().toString().matches("") || doesGroupExist != null) {//Its blank, get default hint
                            Log.v("NewGroup", "New Group has bad name!");
                            toastText = "This Group has an Empty Name or Already Exists!";
                            toast = Toast.makeText(context, toastText, duration);
                            toast.show();
                        }else{
                            groupName = groupNameEditText.getText().toString();
                        }
                        Log.v("NewGroup", "New Group Name is: " + groupName);

                    }
                });

        setResponseTextButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        if (groupResponseEditText.getText().toString().matches("")) {//Its blank, get default hint
                            groupResponse = db.getReplyAll();
                            groupResponseEditText.setHint(groupResponse);
                        } else {
                            groupResponse = groupResponseEditText.getText().toString();
                        }
                        Log.v("NewGroup", "New Group Response is: " + groupResponse);

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_group, menu);
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

    public void setNewGroupToggles(View view) {
    // Is the button now checked?
        boolean isToggled = ((Switch) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.newGroupLocationToggle:
                Log.v("NewGroupLocationToggle:", java.lang.Boolean.toString(isToggled));
                groupLocation = isToggled;
                break;
            case R.id.newGroupActivityToggle:
                Log.v("NewGroupActivityToggle:", java.lang.Boolean.toString(isToggled));
                groupActivity = isToggled;
                break;
        }
    }

    public void createNewGroup(View view) {
        Context context = getApplicationContext();
        Group doesGroupExist = db.getGroupInfo(groupName);

        if(groupName == null || groupName.matches("")){
            Log.v("NewGroup", "New Group has bad name!");
            toastText = "This Group has an empty Name!";
            toast = Toast.makeText(context, toastText, duration);
            toast.show();
        }else if(doesGroupExist != null){
            Log.v("NewGroup", "New Group has a name in the DB!");
            toastText = "This Group Name Already Exists!";
            toast = Toast.makeText(context, toastText, duration);
            toast.show();
        }
        if(groupResponse == null || groupResponse.matches("")){
            groupResponse = db.getReplyAll();
        }

        Group newGroup = new Group(groupName, groupResponse, groupLocation, groupActivity);
        try {
            db.addGroup(newGroup);
            Log.v("NewGroup", "Group Added Successfully to DB!");
            Log.v("NewGroup", "Group Info: "+groupName+" "+groupResponse+" "+groupLocation+" "+groupActivity);

        }catch (Exception e){
            Log.v("NewGroup", "Group Failed to add to DB!");
        }
        Intent intentBack = new Intent(getApplicationContext(), ManageGroups.class);
        startActivity(intentBack);

    }
}
