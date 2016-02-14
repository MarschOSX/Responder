package com.seniordesign.autoresponder.Interface.Settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.DataStructures.ResponseLog;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResponseLogList extends AppCompatActivity {
    private DBInstance db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response_log_list);
        this.db = DBProvider.getInstance(false, getApplicationContext());
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
        /*int numberOfResponses = 0;
        ExpandableListAdapter listAdapter;
        ArrayList<ResponseLog> rawResponseLogs = db.getResponseLogList();
        if(rawResponseLogs != null){
            numberOfResponses = rawResponseLogs.size();
        }
        List<String> listDataHeader = new ArrayList<String>();
        HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();

        for(int i = 0; i < numberOfResponses; i++){
            ResponseLog responseLog = rawResponseLogs.get(i);

            //get the name of the contact for the header
            Contact contact = db.getContactInfo(responseLog.getSenderNumber());
            listDataHeader.add(contact.getName());

            //get the children information
            // Adding child data
            List<String> moreInfo = new ArrayList<String>();
            moreInfo.add("hello");


            listDataChild.put(listDataHeader.get(i), moreInfo);
        }



        ExpandableListView expListView = (ExpandableListView) findViewById(R.id.ResponseLogListView);
        //listAdapter = new BaseExpandableListAdapter (this, listDataHeader, listDataChild);
        //expListView.setAdapter(listAdapter);*/
    }
}
