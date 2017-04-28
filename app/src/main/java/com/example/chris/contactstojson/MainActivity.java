package com.example.chris.contactstojson;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
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

public class MainActivity extends AppCompatActivity {

    // this is the php file we are contacting with Volley
    private static final String CHECKPHONENUMBER_URL = "http://www.populisto.com/checkcontact.php";

    //we are posting phoneNo, which in PHP is phonenumber
    public static final String KEY_PHONENUMBER = "phonenumber";

    //alContacts is a list of all the phone numbers
    public static final ArrayList<String> alContacts = new ArrayList<String>();

   // JSONObject dataToSend = new JSONObject();
    JSONArray jsonArrayContacts = new JSONArray();


    Button buttonCheck;
    TextView textView;
    String phoneNo;
    //String the_contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonCheck = (Button) findViewById(R.id.buttonCheck);
        textView = (TextView) findViewById(R.id.textView);



        //get the names and phone numbers of all contacts in phone book
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.moveToFirst()) {
            while (cur.moveToNext()) {


                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        if(!alContacts.contains(phoneNo))
                            alContacts.add(phoneNo);
                        // break;
                    }



                    //alContacts.add(phoneNo);
                    pCur.close();

                }
               // System.out.println("the amount of phoneNo is :" + pCur.getCount());
                 System.out.println("the amount of phoneNo is :" + cur.getCount());
                System.out.println("the amount of alContacts is :" + alContacts.size());

            }


        }

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
}