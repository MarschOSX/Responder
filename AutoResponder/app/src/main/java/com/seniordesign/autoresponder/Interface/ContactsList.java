package com.seniordesign.autoresponder.Interface;

import android.app.ListActivity;
import android.content.Context;
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
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;

import com.seniordesign.autoresponder.DataStructures.Contact;
import com.seniordesign.autoresponder.Persistance.DBInstance;
import com.seniordesign.autoresponder.Persistance.DBProvider;
import com.seniordesign.autoresponder.R;

import java.util.ArrayList;

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
        this.db = DBProvider.getInstance(false, getApplicationContext());
        updateContactListView();
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

    public void updateContactListView() {
        // storing string resources into Array
        int numberOfContacts = 0;
        ArrayList<Contact> rawContacts = db.getContactList();
        if(rawContacts != null){
            numberOfContacts = rawContacts.size();
        }

        String[] contactsNames = new String[numberOfContacts];
        final HashMap<String, String> contactInfo = new HashMap<>();


        for(int i = 0; i < numberOfContacts; i++){
            contactsNames[i] = rawContacts.get(i).getName();//this is for the ListView
            contactInfo.put(rawContacts.get(i).getName(), rawContacts.get(i).getPhoneNumber());//This is for SingleContact activity
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactsNames);
        final ListView contactList = (ListView)findViewById(R.id.contactList);
        contactList.setAdapter(adapter);

        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nameSelectedFromList = (String) contactList.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), SingleContact.class);
                //Based on selection from list view, open new activity based on that contact
                if(contactInfo.containsKey(nameSelectedFromList)){
                    String number = contactInfo.get(nameSelectedFromList);
                    intent.putExtra("SINGLE_CONTACT_NAME", nameSelectedFromList);
                    intent.putExtra("SINGLE_CONTACT_NUMBER", number);
                    Log.v("ContactList hast Name", nameSelectedFromList);
                    Log.v("ContactList hash Number", number);
                    startActivity(intent);
                }



            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == CONTACT_PICKER_RESULT) {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            CharSequence toastText;
            Toast toast;
            String id = "";
            String name = "";
            String phoneNumber = "";
            Cursor cursor = null;
            try{
                Uri contact = data.getData();
                int idx;
                cursor = getContentResolver().query(contact, null, null, null, null);
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
                        }
                    }
                }
                Log.v("ContactList ID", id);
                Log.v("ContactList Name", name);
                Log.v("ContactList PhoneNumber", phoneNumber);
            }catch (Exception e){
                Log.v("ContactList", "Failed to get Contact Info");
            }finally {
                if (cursor != null) {
                    cursor.close();
                }

                if (name == null || name.matches("")) {//name is blank or empty
                    Log.v("ContactList", "Contact has bad name!");
                    toastText = "This Contact has an empty Name!";
                    toast = Toast.makeText(context, toastText, duration);
                    toast.show();
                }else if(phoneNumber == null || phoneNumber.matches("")){//TODO we might need to check exact phone number validity???
                    Log.v("ContactList", "Contact has a Bad Phone Number");
                    toastText = "This Contact has a bad Phone Number!";
                    toast = Toast.makeText(context, toastText, duration);
                    toast.show();
                }else{
                    Log.v("ContactList", "Successfully got Contact Info, now to push to DB");
                    //take the information you recieved and update the ContactList
                    Contact contact = new Contact(name, phoneNumber, "", "", false, false);
                    //Only get the name and phone number, everything else we will set later
                    try {
                        db.addContact(contact);
                        Log.v("ContactList", "Successfully added Contact to DB");
                    } catch (Exception e) {
                        Log.v("ContactList", "Contact already in DB!");
                        toastText = "This Contact is already in your List!";
                        toast = Toast.makeText(context, toastText, duration);
                        toast.show();
                    }
                }
                updateContactListView();
            }

        } else {
            Log.v("ContactList", "Fail onActivity handler");
        }
    }


}
