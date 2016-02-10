package com.seniordesign.autoresponder.Interface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.EditText;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;

public class UniversalReply extends AppCompatActivity {

    private DBInstance db;
    Button setTextButton;
    EditText setTextEdit;
    Switch universalReplyToggle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_universal_reply);
        setEditText();
        buildSwitches();
        this.db = DBProvider.getInstance(false, getApplicationContext());

        setTextButton = (Button)findViewById(R.id.setUniversalReplyEditText);
        setTextEdit   = (EditText)findViewById(R.id.universalReply_editText);


        //This sends the Universal Response editText field into a string
        setTextButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        String universalReply = setTextEdit.getText().toString();
                        Log.v("Universal Reply:", universalReply);

                        if (universalReply.matches("")) {//Its blank, get default hint
                            universalReply = setTextEdit.getHint().toString();
                        }
                        //push universalReply to DB
                        db.setUniversalReply(universalReply);

                    }
                });
    }

    private void setEditText(){
        DBInstance db = DBProvider.getInstance(false, getApplicationContext());
        String universalReply = db.getUniversalReply();
        TextView universalResponse = (TextView) findViewById(R.id.universalReply_editText);
        universalResponse.setHint(universalReply);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_universal_reply, menu);
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

    //build the switches and add the listeners
    private void buildSwitches(){
        DBInstance db = DBProvider.getInstance(false, getApplicationContext());

        universalReplyToggle = (Switch)findViewById(R.id.universalReplyToggle);
        universalReplyToggle.setChecked(db.getUniversalToggle());
        universalReplyToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBInstance db = DBProvider.getInstance(false, getApplicationContext());
                db.setUniversalToggle(universalReplyToggle.isChecked());
            }
        });

    }

}
