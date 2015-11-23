package com.seniordesign.autoresponder.Interface.Groups;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.seniordesign.autoresponder.DataStructures.Group;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;

public class NewGroup extends AppCompatActivity {

    private DBInstance db;
    EditText groupNameEditText;
    EditText groupResponseEditText;


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

    public void createNewGroup(View view) {//returns -1 on fail, 0 on success
        Context context = getApplicationContext();

        //get info from EditTexts
        groupNameEditText = (EditText)findViewById(R.id.newGroupName);
        groupResponseEditText = (EditText)findViewById(R.id.newGroupResponseText);
        String groupName = groupNameEditText.getText().toString();
        String groupResponse = groupResponseEditText.getText().toString();

        //Does this group already exist?
        Group doesGroupExist = db.getGroupInfo(groupName);

        //Get NewGroup Name
        if (groupName.matches("")) {//Its blank, throw error
            Log.v("NewGroup", "Please fill out Group Name!");
            toastText = "Please fill out Group Name!";
            toast = Toast.makeText(context, toastText, duration);
            toast.show();
            return;
        }else if(doesGroupExist != null){//group already exists in DB, need new name!
            Log.v("NewGroup", "New Group has a name in the DB!");
            toastText = "Group with name " +doesGroupExist.getGroupName()+ " already exists!";
            toast = Toast.makeText(context, toastText, duration);
            toast.show();
            return;//fail
        }else{
            Log.v("NewGroup", "New Group Name is: " + groupName);
        }


        //Get NewGroup Response
        if (groupResponse.matches("")) {//Its blank, set it to default response
            groupResponse = db.getReplyAll();
        }
        Log.v("NewGroup", "New Group Response is: " + groupResponse);

        //create new group
        Group newGroup = new Group(groupName, groupResponse, groupLocation, groupActivity);

        //attempt to add to db
        try {
            db.addGroup(newGroup);
            Log.v("NewGroup", "Group Added Successfully to DB!");
            Log.v("NewGroup", "Group Info: "+groupName+" "+groupResponse+" "+groupLocation+" "+groupActivity);

        }catch (Exception e){
            Log.v("NewGroup", "Group Failed to add to DB!");
            toastText = "Group with name " + newGroup.getGroupName() + " failed to add!";
            toast = Toast.makeText(context, toastText, duration);
            toast.show();
            return;//fail
        }

        //group was added!
        Intent intentBack = new Intent(getApplicationContext(), ManageGroups.class);
        intentBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentBack);
        finish();
    }
}
