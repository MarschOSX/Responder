package com.seniordesign.autoresponder.Interface.Contacts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.seniordesign.autoresponder.DataStructures.Group;
import com.seniordesign.autoresponder.Interface.Groups.ManageGroups;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;

public class SetContactGroup extends AppCompatActivity {

    String phoneNumber;
    private DBInstance db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_contact_group);
        Intent intent = getIntent();
        this.db = DBProvider.getInstance(false, getApplicationContext());
        phoneNumber = intent.getStringExtra("SINGLE_CONTACT_NUMBER");
        setUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_contact_group, menu);
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

    public void setUp(){
        TextView groupNameTextView = (TextView)findViewById(R.id.currentGroupName);
        groupNameTextView.setText(db.getContactInfo(phoneNumber).getGroupName());
    }

    public void addToGroup(View view) {
        Intent intent = new Intent(getApplicationContext(), ManageGroups.class);
        intent.putExtra("CONTACT_NUMBER", phoneNumber);
        startActivity(intent);
    }

    public void removeFromGroup(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(SetContactGroup.this);
        alert.setTitle("Remove Contact From Current Group");
        alert.setMessage("Are you sure you want to remove the contact from this group? Neither the contact or the group will be deleted");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do your work here
                Log.v("SingleGroupDelete:", "YES");
                db.setContactGroup(phoneNumber, Group.DEFAULT_GROUP);

                TextView groupNameTextView = (TextView)findViewById(R.id.currentGroupName);
                groupNameTextView.setText(db.getContactInfo(phoneNumber).getGroupName());
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
}
