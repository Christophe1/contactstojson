package com.example.chris.contactstojson;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    // this is the php file we are contacting with Volley to see what contacts are using the App
    private static final String CHECKPHONENUMBER_URL = "http://www.populisto.com/checkcontact.php";

    //we are posting phoneNo, which in PHP is phonenumber
    public static final String KEY_PHONENUMBER = "phonenumber";

    //alContacts is a list of all the phone numbers in the user's contacts
    public static final ArrayList<String> alContacts = new ArrayList<String>();

   // JSONObject dataToSend = new JSONObject();
    JSONArray jsonArrayContacts = new JSONArray();


    Button buttonCheck;
    TextView textView;
    String phoneNo;
    Cursor cursor;
    String name;
    String phoneNumberofContact;
    String lookupkey;
    //String the_contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonCheck = (Button) findViewById(R.id.buttonCheck);
        textView = (TextView) findViewById(R.id.textView);

        //this is for the textview to scroll, just for development
        textView.setMovementMethod(new ScrollingMovementMethod());


        //get all the contacts on the user's phone
        getPhoneContacts();







        buttonCheck.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                try {
                  //  JSONObject dataToSend = new JSONObject();

                    // contacts
                  //  JSONArray jsonArrayContacts = new JSONArray();
                    //alContacts is our arraylist with all the phone numbers
                    for (int i = 0; i < alContacts.size(); i++)
                    {
                        // make each contact in alContacts into an individual JSON object called jsonObjectContact
                        JSONObject jsonObjectContact = new JSONObject();
                        // jsonObjectContact will be of the form {"phone_number":"123456789"}
                        jsonObjectContact.put("phone_number", alContacts.get(i));

                        // Add jsonObjectContact to contacts jsonArray
                        jsonArrayContacts.put(jsonObjectContact);

                    }
                    System.out.println("the amount in alContacts :" + alContacts.size());
                    // Add contacts jsonArray to jsonObject dataToSend
                   // dataToSend.put("contacts", jsonArrayContacts);

                    System.out.println("JSONarraycontacts: " + jsonArrayContacts.toString());
                    //System.out.println("JSON object datatoSend: " + dataToSend.toString());


                } catch (final JSONException e) {
                    Log.e("FAILED", "Json parsing error: " + e.getMessage());
                }

                CheckifUserisContact();
            }
        });

    }



    private void CheckifUserisContact() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, CHECKPHONENUMBER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                        textView.append(response + " \n");

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();

                    }

                }) {



         @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
             //The KEY, KEY_PHONENUMBER = "phonenumber" . In PHP we will have $_POST["phonenumber"]
             //The VALUE, phonenumber, will be of the form "12345678"
                params.put(KEY_PHONENUMBER,jsonArrayContacts.toString());
                System.out.println(Collections.singletonList(params));
               //System.out.println("contact is : " + jsonArrayContacts);
                return params;


        }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

    //get the names and phone numbers of all contacts in phone book, take out duplicates
    protected void getPhoneContacts() {
//          we have this here to avoid cursor errors
        if (cursor != null) {
            cursor.moveToFirst();

        }


        try {

//                get a handle on the Content Resolver, so we can query the provider,
            cursor = getApplicationContext().getContentResolver()
//                the table to query
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//               Null. This means that we are not making any conditional query into the contacts table.
//               Hence, all data is returned into the cursor.
//                                Projection - the columns you want to query
                            null,
//                                Selection - with this you are extracting records with assigned (by you) conditions and rules
                            null,
//                                SelectionArgs - This replaces any question marks (?) in the selection string
//                               if you have something like String[] args = { "first string", "second@string.com" };
                            null,
//                                display in ascending order
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC");

//                get the column number of the Contact_ID column, make it an integer.
//                I think having it stored as a number makes for faster operations later on.
            int Idx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
//                get the column number of the DISPLAY_NAME column
            int nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
//                 get the column number of the NUMBER column
            int phoneNumberofContactIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

//                ****
            int contactlookupkey = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY);
//                ****
//                cursor.moveToFirst();
//        String contactlookupkey2 = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY));


//                int photoIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI);


            cursor.moveToFirst();

//              We make a new Hashset to hold all our contact_ids, including duplicates, if they come up
            Set<String> ids = new HashSet<>();
            do {
                System.out.println("=====>in while");
//                  get a handle on the contactid, which is a string. Loop through all the contact_ids
                String contactid = cursor.getString(Idx);
//                  if our Hashset doesn't already contain the contactid string,
//                    then add it to the hashset
                if (!ids.contains(contactid)) {
                    ids.add(contactid);

                    HashMap<String, String> hashMap = new HashMap<String, String>();
//                        get a handle on the display name, which is a string
                    name = cursor.getString(nameIdx);
//                        get a handle on the phone number, which is a string
                    phoneNumberofContact = cursor.getString(phoneNumberofContactIdx);
//                        String image = cursor.getString(photoIdIdx);


                    //------------------------------------------------------
                    //replace numbers starting with 00 with +
                    //if (phoneNumberofContact.startsWith("00")) {
                    //    System.out.println(phoneNumberofContact = phoneNumberofContact.replaceFirst("00", "+"));
                    //}
                    // remove splaces between phone numbers
                    //phoneNumberofContact = phoneNumberofContact.replaceAll("\\s+", "");

                    //all phone numbers not starting with +, make them E.164 format,
                    //for Irish phones. Although it should really be for the country code of the User
                   // if (!phoneNumberofContact.startsWith("+")) {


                        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                        try {
                            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumberofContact, "IE");
                            phoneNumberofContact = phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
                            //Since you know the country you can format it as follows:
                            //System.out.println(phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164));
                        } catch (NumberParseException e) {
                            System.err.println("NumberParseException was thrown: " + e.toString());
                        }
                   // }

                    //----------------------------------------------------------

                    alContacts.add(phoneNumberofContact);

                    lookupkey = cursor.getString(contactlookupkey);

//                    System.out.println("Id--->"+contactid+"Name--->"+name);
                    System.out.println("Id--->" + contactid + " Name--->" + name);
                    System.out.println("Id--->" + contactid + " Numberbob--->" + phoneNumberofContact);
                    System.out.println("Id--->" + contactid + " lookupkey--->" + lookupkey);
//                        System.out.println("Id--->" + contactid + " lookupkey2--->" + contactlookupkey2);

//                        if (!phoneNumberofContact.contains("*")) {
//                            hashMap.put("contactid", "" + contactid);
//                            hashMap.put("name", "" + name);
//                            hashMap.put("phoneNumberofContact", "" + phoneNumberofContact);
//                            hashMap.put("image", "" + image);
                    // hashMap.put("email", ""+email);
//                            if (hashMapsArrayList != null) {
//                                hashMapsArrayList.add(hashMap);
//                            }
//                    hashMapsArrayList.add(hashMap);
//                        }

                    // SelectContact selectContact = new SelectContact();
//                    selectContact.setThumb(bit_thumb);
                    // selectContact.setName(name);
                    // selectContact.setPhone(phoneNumberofContact);
                    // selectContact.setLookup(lookupkey);
//                    selectContact.setCheckedBox(false);
                    // selectContacts.add(selectContact);
                }


            } while (cursor.moveToNext());


        } catch (Exception e) {
            e.printStackTrace();
            cursor.close();
        } finally {
//                if (cursor != null) {
            cursor.close();
//                }
        }
        ;
// cursor.close();
//return null;
    }
}