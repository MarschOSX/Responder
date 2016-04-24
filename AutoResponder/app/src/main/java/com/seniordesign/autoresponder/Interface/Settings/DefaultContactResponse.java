package com.seniordesign.autoresponder.Interface.Settings;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.seniordesign.autoresponder.DataStructures.Group;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;

public class DefaultContactResponse extends ActionBarActivity {
    private DBInstance db;
    Button setTextButton;
    EditText setTextEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_contact_response);

        //initialize vars
        this.db = DBProvider.getInstance(false, getApplicationContext());

        //load hint into editText View
        setEditText();

        //build switches
        buildSwitches();
        setTextButton = (Button)findViewById(R.id.button);
        setTextEdit   = (EditText)findViewById(R.id.editText);

        setTextButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        String generalReply = setTextEdit.getText().toString();
                        Log.v("General Reply:", generalReply);

                        if(generalReply == null || generalReply.matches("")){//Its blank, get default hint
                            generalReply = setTextEdit.getHint().toString();
                        }
                        //push generalReply to DB
                        db.setGroupResponse(Group.DEFAULT_GROUP, generalReply);

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_settings, menu);
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

    public void buildSwitches(){

        final Switch defaultGroupActivity = (Switch)findViewById(R.id.default_group_activity);
        defaultGroupActivity.setChecked(db.getGroupInfo(Group.DEFAULT_GROUP).isActivityPermission());
        defaultGroupActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.setGroupActivityPermission(Group.DEFAULT_GROUP, !db.getGroupInfo(Group.DEFAULT_GROUP).isActivityPermission());
            }
        });


        final Switch defaultGroupLocation = (Switch)findViewById(R.id.default_group_location);
        defaultGroupLocation.setChecked(db.getGroupInfo(Group.DEFAULT_GROUP).isLocationPermission());

        defaultGroupLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.setGroupLocationPermission(Group.DEFAULT_GROUP, !db.getGroupInfo(Group.DEFAULT_GROUP).isLocationPermission());

            }
        });
    }

    private void setEditText(){
        DBInstance db = DBProvider.getInstance(false, getApplicationContext());
        String replyAll = db.getGroupInfo(Group.DEFAULT_GROUP).getResponse();
        TextView generalResponse = (TextView) findViewById(R.id.editText);
        generalResponse.setHint(replyAll);
    }
}
