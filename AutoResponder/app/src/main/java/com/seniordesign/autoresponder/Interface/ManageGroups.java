package com.seniordesign.autoresponder.Interface;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.DataStructures.Group;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ManageGroups extends AppCompatActivity {

    private DBInstance db;
    boolean pickerFlag = false;
    String contactNumber = null;
    int duration = Toast.LENGTH_LONG;
    CharSequence toastText;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_groups);
        Intent intent = getIntent();
        this.db = DBProvider.getInstance(false, getApplicationContext());
        contactNumber = intent.getStringExtra("CONTACT_NUMBER");
        if(contactNumber != null){
            pickerFlag = true;
        }
        updateGroupsListView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_groups, menu);
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

    public void doLaunchAddNewGroup(View view) {
        Context context = getApplicationContext();
        // Do something in response to button
        if(!pickerFlag) {
            Intent intent = new Intent(this, NewGroup.class);
            startActivity(intent);
        }else{
            toastText = "Please Select From Current Groups!";
            toast = Toast.makeText(context, toastText, duration);
            toast.show();
        }

    }

    public void updateGroupsListView() {
        // storing string resources into Array
        int numberOfGroups = 0;
        ArrayList<Group> rawGroups = db.getGroupList();
        if(rawGroups != null){
            numberOfGroups = rawGroups.size() - 1;
        }

        String[] groupNames = new String[numberOfGroups];

        for(int i = 0; i < numberOfGroups; i++){
            String groupName = rawGroups.get(i).getGroupName();
            if(!groupName.matches(Group.DEFAULT_GROUP)) {
                groupNames[i] = groupName;//this is for the ListView
            }else{
                rawGroups.remove(i);
                i--;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groupNames);
        final ListView groupsList = (ListView)findViewById(R.id.groupsList);
        groupsList.setAdapter(adapter);

        groupsList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String groupName = (String) groupsList.getItemAtPosition(position);

                if(pickerFlag){
                    db.setContactGroup(contactNumber, groupName);
                    Intent intent = new Intent(getApplicationContext(), SingleContact.class);
                    intent.putExtra("SINGLE_CONTACT_NUMBER", contactNumber);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getApplicationContext(), SingleGroup.class);
                    //Based on selection from list view, open new activity based on that contact
                    intent.putExtra("GROUP_NAME", groupName);
                    Log.v("GroupName Selected: ", groupName);
                    startActivity(intent);
                }
            }
        });
    }
}
