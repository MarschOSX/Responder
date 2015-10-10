package com.seniordesign.autoresponder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.util.Log;




public class Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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


    /** Called when the user clicks the General Response Button*/
    public void gotoGeneralResponse(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, GeneralResponse.class);
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
                    //TODO push Toggle on/off to DB
                break;
        }
    }
}
