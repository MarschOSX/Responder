package com.seniordesign.autoresponder.Interface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.util.Log;

import com.seniordesign.autoresponder.Interface.Contacts.ContactsList;
import com.seniordesign.autoresponder.Interface.Groups.ManageGroups;
import com.seniordesign.autoresponder.Interface.Settings.UserSettings;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;


public class Main extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        setToggleSwitch();
    }

    //checks DB for the current toggle on/off setting
    private void setToggleSwitch(){
        DBInstance db = DBProvider.getInstance(false, getApplicationContext());
        boolean isActive = db.getResponseToggle();
        Switch toggleButton = (Switch)findViewById(R.id.autoRespond_switch);
        toggleButton.setChecked(isActive);
        //Add other switches here when implemented....
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onBackPressed() {//on main screen, pressing back will take back to home!
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    /** Called when the user clicks the General Response Button*/
    public void gotoGeneralResponse(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, GeneralResponse.class);
        startActivity(intent);
    }

    public void gotoSettings(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, UserSettings.class);
        startActivity(intent);
    }

    public void gotoContactsList(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, ContactsList.class);
        startActivity(intent);
    }

    public void gotoManageGroups(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, ManageGroups.class);
        startActivity(intent);
    }

    // Called when the user selects a time delay radio button
    public void switchChecker(View view) {
        // Is the button now checked?
        boolean autoRespondOffOn = ((Switch) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.autoRespond_switch:
                    Log.v("AutoResponder Active? ", java.lang.Boolean.toString(autoRespondOffOn));
                    //push Toggle on/off to DB
                    DBInstance db = DBProvider.getInstance(false, getApplicationContext());
                    db.setResponseToggle(autoRespondOffOn);
                break;
        }
    }
}
