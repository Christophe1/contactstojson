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
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity {

    // this is the php file name where to select from the database, the user's phone number
   // private static final String CHECKPHONENUMBER_URL = "http://www.populisto.com/phone_number.json";

    //we are posting phoneNo, which in PHP is phonenumber
    public static final String KEY_PHONENUMBER = "phonenumber";

    //alContacts is a list of all the phone numbers
    public static final ArrayList<String> alContacts = new ArrayList<String>();

    Button buttonCheck;
    //for experimentation purposes
    TextView textView;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonCheck = (Button) findViewById(R.id.buttonCheck);
        textView = (TextView) findViewById(R.id.textView);
        requestQueue = Volley.newRequestQueue(this);


        //get the names and phone numbers of all contacts in phone book
    /*    ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
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
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        alContacts.add(phoneNo);
                        // break;
                    }
                    pCur.close();

                }
            }
        }*/

        buttonCheck.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //  System.out.println("Print the contacts array : ");
                // System.out.println(alContacts);

                //CheckifUserisContact();

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://populisto.com/phone_number.json", (JSONObject) null,


                        //StringRequest stringRequest = new StringRequest(Request.Method.POST, CHECKPHONENUMBER_URL,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {

                                    JSONArray jsonArray = new JSONArray();
                                   // JSONArray jsonArray = response.getJSONArray();

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject contact = jsonArray.getJSONObject(i);

                                        String phonenumber = contact.getString("phone_number");
                                        textView.append(phonenumber + " \n");


                                    }

                                } catch (JSONException e) {

                                    e.printStackTrace();
                                }
                            }

                        },


                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                  Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                               // Log.e("Volley", "Error");
                            }

                        }
                );
                //RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(jsonObjectRequest);
            }

      /*      @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_PHONENUMBER,);
                return params;*/

        });
        }}
        // RequestQueue requestQueue = Volley.newRequestQueue(this);
        // requestQueue.add(jsonObjectRequest);







  //  private void CheckifUserisContact() {






