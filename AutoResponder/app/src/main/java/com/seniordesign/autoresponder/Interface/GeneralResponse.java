package com.seniordesign.autoresponder.Interface;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.EditText;
import android.view.View;

import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;

import java.lang.reflect.InvocationTargetException;

public class GeneralResponse extends AppCompatActivity {


    Button setTextButton;
    EditText setTextEdit;
    EditText setDelayNum;
    int responseDelay = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_response);


        setTextButton = (Button)findViewById(R.id.setTextButton);
        setTextEdit   = (EditText)findViewById(R.id.generalResponse_text);
        setDelayNum   = (EditText)findViewById(R.id.customMin);
        setDelayNum.requestFocus();


        //This sends the General Response editText field into a string
        setTextButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        String generalReply = setTextEdit.getText().toString();
                        Log.v("General Reply:", generalReply);

                        if(generalReply == ""){//Its blank, get default hint
                            generalReply = setTextEdit.getHint().toString();
                        }
                        //push generalReply to DB
                        DBInstance db = DBProvider.getInstance(false, getApplicationContext());
                        db.setReplyAll(generalReply);

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_general_response, menu);
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

    // Called when the user selects a time delay radio button
    public void radioButtonDelaySet(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.fiveMin_radioButton:
                if (checked)
                    responseDelay = 5;
                    break;
            case R.id.twentyMin_radioButton:
                if (checked)
                    responseDelay = 20;
                    break;
            case R.id.oneHour_radioButton:
                if (checked)
                    responseDelay = 60;
                    break;
            case R.id.custom_option:
                if (checked)
                   try{
                       responseDelay = Integer.parseInt(setDelayNum.getText().toString());
                   }catch(NumberFormatException e){
                       responseDelay = Integer.parseInt(setDelayNum.getHint().toString());
                   }
            break;
        }
        Log.v("Time Delay:", Integer.toString(responseDelay));
        //TODO push responseDelay to DB
        //Default is 20 and the RadioButton is set to this
        //DBInstance db = DBProvider.getInstance(false, getApplicationContext());
       // db.setDelay(responseDelay);
    }

}
