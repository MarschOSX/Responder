package com.seniordesign.autoresponder.Interface.Settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.DataStructures.Group;
import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.Interface.Contacts.ContactsList;
import com.seniordesign.autoresponder.Interface.Groups.GroupInfo;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ResponseLogList extends AppCompatActivity {
    private DBInstance db;
    public static final String TAG = "ResponseLogList";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response_log_list);
        this.db = DBProvider.getInstance(false, getApplicationContext());

        //Update the ExpandableListView with ResponseLog information
        updateResponseLogListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_response_log_list, menu);
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

    public void updateResponseLogListView() {
        int numberOfResponses = 0;
        ExpandableListAdapter listAdapter;

        //Get List of Logs
        ArrayList<ResponseLog> rawResponseLogs = db.getResponseLogList();
        if(rawResponseLogs != null){
            numberOfResponses = rawResponseLogs.size();
        }
        ArrayList<String> parentItems = new ArrayList<>();
        ArrayList<Object> childItems = new ArrayList<>();

        //Insert Parent Title Data, and Child Data relating to the parent
        //From most recent down...
        for(int i = numberOfResponses - 1; i >= 0; i--){
            //Get each responseLog
            try {
                ResponseLog responseLog = rawResponseLogs.get(i);

                //get the name of the contact for the header
                Contact contact = db.getContactInfo(responseLog.getSenderNumber());
                if(contact == null){
                    contact = new Contact(responseLog.getSenderNumber(), responseLog.getSenderNumber(), Group.DEFAULT_GROUP, db.getReplyAll(), false, false, true);
                }

                //Both Location and Calendar Info are Shared
                if (responseLog.getLocationShared() && responseLog.getActivityShared()) {
                    parentItems.add(contact.getName() + ", Sent Location and Calendar Info");
                } else if (responseLog.getLocationShared()) {
                    parentItems.add(contact.getName() + ", Sent Location");
                } else if (responseLog.getActivityShared()) {
                    parentItems.add(contact.getName() + ", Sent Calendar Info");
                } else {
                    parentItems.add(contact.getName() + ", Sent Message");
                }

                //get the children information
                List<String> child = new ArrayList<>();
                String timeRecievedReadable = getDate(Long.parseLong(responseLog.getTimeReceived()));
                String timeSentReadable = getDate(Long.parseLong(responseLog.getTimeSent()));
                child.add("Phone Number:   " + responseLog.getSenderNumber());
                child.add("Message Recieved:   " + responseLog.getMessageReceived());
                child.add("Message Recieved At:   " + timeRecievedReadable);
                child.add("Message Sent:   " + responseLog.getMessageSent());
                child.add("Message Sent At:   " + timeSentReadable);
                child.add("Location Shared:   " + responseLog.getLocationShared());
                child.add("Calendar Event Shared:   " + responseLog.getActivityShared());

                //add child data
                childItems.add(child);
                android.util.Log.v(TAG, "Successfully Added a Log to the ELV");
            }catch(NullPointerException e){
                android.util.Log.v(TAG, "ResponseLog was null");
            }
        }

        //Add information to ExpandableListView using the custom adapter
        ExpandableListView expListView = (ExpandableListView) findViewById(R.id.ResponseLogListView);
        listAdapter = new com.seniordesign.autoresponder.Interface.Settings.ExpandableListAdapter(parentItems,childItems, (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
        expListView.setAdapter(listAdapter);
    }


    public void clearResponseLogs(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(ResponseLogList.this);
        alert.setTitle("Clear All Response Logs");
        alert.setMessage("Are you sure you want to delete all history? This cannot be undone.");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(TAG, "YES clear History");
                db.deleteResponseLogs();
                updateResponseLogListView();
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(TAG, "NO do not clear history");
                dialog.dismiss();
            }
        });
        alert.show();
    }

    //convert milliseconds into a date readable format
    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "MM/dd/yyyy hh:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


}
