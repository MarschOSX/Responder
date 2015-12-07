package com.seniordesign.autoresponder.Interface.Settings;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;

public class TimeDelay extends AppCompatActivity {
    private static final String TAG = "TimeDelay";
    int responseDelay = 20;
    private DBInstance db;
    EditText setDelayNum;

    RadioButton fiveMinRB;
    RadioButton twentyMinRB;
    RadioButton sixtyMinRB;
    RadioButton customRB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_delay);

        this.db = DBProvider.getInstance(false, getApplicationContext());

        this.fiveMinRB = (RadioButton) findViewById(R.id.radioButton_5);
        this.twentyMinRB = (RadioButton) findViewById(R.id.radioButton_20);
        this.sixtyMinRB = (RadioButton) findViewById(R.id.radioButton_60);
        this.customRB = (RadioButton) findViewById(R.id.radioButton_custom);

        //set listener to be called when text is changed
        setDelayNum   = (EditText)findViewById(R.id.customVal);
        setDelayNum.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (actionId == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press

                    try{
                        responseDelay = Integer.parseInt(setDelayNum.getText().toString());
                    }catch(NumberFormatException e){
                        responseDelay = Integer.parseInt(setDelayNum.getHint().toString());
                    }

                    db.setDelay(responseDelay);
                    return true;
                }
                return false;
            }
        });
        /*setDelayNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String time = s.toString();
                if (!time.matches("")){
                    customRB.setChecked(true);
                    db.setDelay(Integer.parseInt(time));
                }
            }
        });*/

        setTimeRadioButton();
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

    private void setTimeRadioButton(){
        //DBInstance db = DBProvider.getInstance(false, getApplicationContext());
        Integer timeDelay = db.getDelay();
        if(timeDelay == 5){
            fiveMinRB.setChecked(true);
        }else if(timeDelay == 20){
            twentyMinRB.setChecked(true);
        }else if(timeDelay == 60){
            sixtyMinRB.setChecked(true);
        }else{
            customRB.setChecked(true);
            EditText customMinText = (EditText) findViewById(R.id.customVal);
            customMinText.setHint(timeDelay.toString());
        }

    }

    public void radioButtonDelaySet(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton_5:
                if (checked)
                    responseDelay = 5;
                break;
            case R.id.radioButton_20:
                if (checked)
                    responseDelay = 20;
                break;
            case R.id.radioButton_60:
                if (checked)
                    responseDelay = 60;
                break;
            case R.id.radioButton_custom:
                if (checked)
                    try{
                        responseDelay = Integer.parseInt(setDelayNum.getText().toString());
                    }catch(NumberFormatException e){
                        responseDelay = Integer.parseInt(setDelayNum.getHint().toString());
                    }
                break;
        }
        Log.v(TAG, "setting delay to: " + Integer.toString(responseDelay));
        //Default is 20 and the RadioButton is set to this
        //DBInstance db = DBProvider.getInstance(false, getApplicationContext());
        db.setDelay(responseDelay);
    }
}
