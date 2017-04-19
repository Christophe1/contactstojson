package com.example.chris.contactstojson;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    // this is the php file name where to select from the database, the user's phone number
    private static final String CHECKPHONENUMBER_URL = "http://www.populisto.com/checkcontact.php";

    //we are posting phoneNo, which in PHP is phonenumber
    public static final String KEY_PHONENUMBER = "phonenumber";

    //alContacts is a list of all the phone numbers
    public static final ArrayList<String> alContacts = new ArrayList<String>();

    Button buttonCheck;
    String phoneNo;

    EditText editNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         buttonCheck = (Button) findViewById(R.id.buttonCheck);

        editNo = (EditText) findViewById(R.id.editNo);



        //get the names and phone numbers of all contacts in phone book
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {


                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //replace numbers starting with 00 with +
                        if (phoneNo.startsWith("00")) {
                            System.out.println(phoneNo = phoneNo.replaceFirst("00", "+"));
                        }
                        // remove splaces between phone numbers
                        phoneNo = phoneNo.replaceAll("\\s+", "");

                        //all phone numbers not starting with +, make them E.164 format,
                        //for Irish phones
                        if (!phoneNo.startsWith("+")) {


                            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                            try {
                                Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNo, "IE");
                                phoneNo = phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
                                //Since you know the country you can format it as follows:
                                //System.out.println(phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164));
                            } catch (NumberParseException e) {
                                System.err.println("NumberParseException was thrown: " + e.toString());
                            }
                        }

                        System.out.println("Name: " + name);
                        System.out.println("Phone No: " + phoneNo);

                        alContacts.add(phoneNo);
                       // break;
                    }
                    pCur.close();

                }
            }
        }

        System.out.println("Print the contacts array : ");
        System.out.println(alContacts);

        //I need to check if a contact in the user's phone contacts is already a user of populisto.
        //If yes, then in the contacts table put in the user's user_id
        //and the contact's user id in contacts_id

        buttonCheck.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                System.out.println("Print the contacts array : ");
                System.out.println(alContacts);
               // System.out.println("you clicked it");
              //  phoneNo = editNo.getText().toString();
             //    phoneNo = "545774";
                 CheckifUserisContact();
            }
        });

    }



    private void CheckifUserisContact() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, CHECKPHONENUMBER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("failure")) {
                            Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "succeeded", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();

                    }

                }) {
          //  JSONObject jsonObject=new JSONObject();
            JSONArray jsonArray = new JSONArray();
          //  jsonObject.put("alContacts",jsonArray);
            for(int i=0;i<=alContacts.size;i++){}

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_PHONENUMBER, jsonArray.toString());
                return params;

              //  JsonObjectRequest request_json = new JsonObjectRequest(CHECKPHONENUMBER_URL, new JSONObject(map);

            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }
}

