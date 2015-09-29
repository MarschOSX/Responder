package com.seniordesign.autoresponder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;

public class GeneralResponse extends AppCompatActivity {


    Button setTextButton;
    EditText setTextEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_response);


        setTextButton = (Button)findViewById(R.id.setTextButton);
        setTextEdit   = (EditText)findViewById(R.id.generalResponse_text);

        //This sends the General Response editText field into a string
        //Only prints out to Log.V currently
        setTextButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        Log.v("EditText", setTextEdit.getText().toString());

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_general_response, menu);
        return true;
    }



    /*
     Called when the user clicks the General Response Button
    public void gotoGeneralResponse(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, GeneralResponse.class);
        startActivity(intent);
    }
     */

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
}
