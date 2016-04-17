package com.seniordesign.autoresponder.Interface.Settings;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.seniordesign.autoresponder.DataStructures.Setting;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.Persistance.PermDBInstance;
import com.seniordesign.autoresponder.R;
import com.seniordesign.autoresponder.Services.DrivingDetectionService;

import java.security.InvalidParameterException;

public class UserSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        loadSettings();
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

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        DBInstance db =  DBProvider.getInstance(false, getApplicationContext());

        switch (requestCode){
            case SettingListAdapter.DRIVING_DETECTION:
                // if permissions are granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (db != null) db.setDrivingDetectionToggle(true);
                    getApplicationContext().startService(new Intent(getApplicationContext(), DrivingDetectionService.class));
                }
                // if permissions denied
                else{
                    if (db != null) db.setDrivingDetectionToggle(false);
                    loadSettings();
                }
                break;
            default:
                throw new InvalidParameterException("Unknown request code: " + requestCode);
        }
    }*/

    private void loadSettings(){
        final ListView listView = (ListView) findViewById(R.id.listView);
        final SettingListAdapter adapter = new SettingListAdapter(this.getApplicationContext(), Setting.settingUIList, this);

        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume(){
        super.onResume();
        loadSettings();
    }
}
