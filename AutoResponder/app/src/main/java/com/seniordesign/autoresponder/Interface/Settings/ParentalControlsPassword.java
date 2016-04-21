package com.seniordesign.autoresponder.Interface.Settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.seniordesign.autoresponder.Logging.PermissionsChecker;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;
import com.seniordesign.autoresponder.Receiver.SMSSender;
import com.seniordesign.autoresponder.Services.ParentalControlsWatcher;

public class ParentalControlsPassword extends AppCompatActivity {

    private String TAG = "ParentalControlsPassword";
    private ParentalControlsPassword me;
    private DBInstance db;
    EditText input;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        setContentView(R.layout.activity_parental_controls_password);
        this.db = DBProvider.getInstance(false, getApplicationContext());
        me = this;


        if(checkForExistingPassword()) {
            //Password Exists! Lets check the user input
            showPasswordBox();
        }else{
            //No Password! Must setup password
            setUpPassword();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parental_controls_password, menu);
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

    public void showPasswordBox(){
        //Build alert
        AlertDialog.Builder alert = new AlertDialog.Builder(me);
        alert.setTitle("Unlock Parental Controls");
        alert.setMessage("Please enter your password:");
        input = new EditText(me);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alert.setView(input);

        alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(TAG, "Proceed with Password Check");
                //is password correct?
                if(db.getParentalControlsPassword().matches(input.getText().toString())) {
                    //Password is correct! Sending Alert SMS (if possible)
                    if(!db.getParentalControlsNumber().matches("0") && PermissionsChecker.checkSendSMSPermission(ParentalControlsPassword.this, getApplicationContext(), 1)){
                        SMSSender sender = new SMSSender(db);
                        sender.sendSMS("This number is editing AutoResponder parental controls", "",
                                db.getParentalControlsNumber(), 0L, false, false, getApplicationContext());
                    }

                    if(intent.getStringExtra("CHANGING_PASSWORD").equals("true")){
                        //we are changing the password
                        setUpPassword();
                    }else {
                        // Moving to Parental Controls Set Up
                        final Intent intent = new Intent(getApplicationContext(), ParentalControlsSetUp.class);
                        startActivity(intent);
                        finish();
                    }
                }else {
                    //otherwise it is not correct
                    int duration = Toast.LENGTH_LONG;
                    CharSequence toastText;
                    Toast toast;
                    toastText = "Incorrect Password Entered";
                    toast = Toast.makeText(getApplicationContext(), toastText, duration);
                    toast.show();
                    finish();
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(TAG, "Canceled Password Check");
                finish();
            }
        });
        alert.show();
    }

    public void setUpPassword(){
        //build alert
        AlertDialog.Builder alert = new AlertDialog.Builder(me);
        alert.setTitle("Set Up Parental Controls");
        alert.setMessage("Please enter a password to be used for Parental Controls. Cannot be blank:");
        input = new EditText(me);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alert.setView(input);
        alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do your work here
                Log.v(TAG, "Proceed with Saving Password");
                String passwordEntered = input.getText().toString();
                Log.v(TAG, "Password entered: " + passwordEntered);
                if (passwordEntered.matches("") || passwordEntered.matches(" ")) {
                    int duration = Toast.LENGTH_LONG;
                    CharSequence toastText;
                    Toast toast;
                    toastText = "Invalid Password Entered! Cannot be blank!";
                    toast = Toast.makeText(getApplicationContext(), toastText, duration);
                    toast.show();
                    finish();
                } else {
                    db.setParentalControlsPassword(passwordEntered);

                    if (intent.getStringExtra("CHANGING_PASSWORD").equals("true")) {
                        finish();
                    }else {
                        //Password is entered! Moving to Parental Controls Set Up
                        final Intent intent = new Intent(getApplicationContext(), ParentalControlsSetUp.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(TAG, "Canceled Password Check");
                finish();
            }
        });

        alert.show();
    }

    public boolean checkForExistingPassword(){
        Log.v(TAG, "Looking at existing password");
        return db.getParentalControlsPassword() != null;
    }

}
