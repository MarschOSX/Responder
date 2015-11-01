package com.seniordesign.autoresponder.Interface;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Email;

import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.R;

/**
 * By MarschOSX 11/1/2015
 *
 * This class was build with help from the following sources:
 * http://www.androidhive.info/2011/10/android-listview-tutorial/
 * https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
 * http://code.tutsplus.com/tutorials/android-essentials-using-the-contact-picker--mobile-2017
 * http://android-er.blogspot.com/2012/11/get-phone-number-from-contacts-database.html
 */

public class ContactsList extends AppCompatActivity {

    private DBInstance db;
    private static final int CONTACT_PICKER_RESULT = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);
        // storing string resources into Array
        /*TODO generate contacts name list from DB instead of this test_list
        * probably need a for loopto get the name from each contact object, and
        * pass this into the String[] array below--->
        */
        String[] contactsNames = getResources().getStringArray(R.array.test_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactsNames);
        ListView contactList = (ListView)findViewById(R.id.contactList);
        contactList.setAdapter(adapter);

        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getApplicationContext(), SingleContact.class);
                        startActivity(intent);
                    }
                });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contacts_list, menu);
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

    public void doLaunchContactPicker(View view) {
        // Do something in response to button

        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == CONTACT_PICKER_RESULT) {
            String id = "";
            String name = "";
            String phoneNumber = "";
            Cursor cursor = null;
            try{
                Uri contact = data.getData();
                int idx;
                cursor = getContentResolver().query(contact, null, null, null, null);
                //TODO handle when a contact is missing particular data
                if (cursor.moveToFirst()) {
                    //Get ID of Contact
                    idx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                    id = cursor.getString(idx);

                    //Get Name of Contact
                    idx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    name = cursor.getString(idx);

                    //Get Phone Number of Contact with Contact ID
                    idx = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                    if(cursor.getString(idx).matches("1")) {
                        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);
                        if (cursor.moveToNext()) {
                            idx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                            phoneNumber = cursor.getString(idx);
                        } else {
                            //TODO Handle the absence of data
                            Log.v("ContactList PhoneNumber", "No PhoneNumber Found");
                        }
                    }
                }
                Log.v("ContactList ID", id);
                Log.v("ContactList Name", name);
                Log.v("ContactList PhoneNumber", phoneNumber);


            }catch (Exception e){
                Log.v("ContactList", "Failed to get Contact Info");
            }finally {
                if(cursor != null){
                    cursor.close();
                }
                //TODO take the information you recieved and update the ContactList
            }

        } else {
            Log.v("ContactList", "Fail onActivity handler");
        }
    }


}
