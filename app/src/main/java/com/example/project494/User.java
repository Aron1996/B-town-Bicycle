package com.example.project494;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class User extends AppCompatActivity {

    String userID, username, phonenumber, email, gender, dob;
    TextView user;
    EditText e_mail, phone_num, gder, dateob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        userID = sharedPref.getString("id", "");
        username = sharedPref.getString("username", "");
        phonenumber = sharedPref.getString("phonenumber", "");
        email = sharedPref.getString("email", "");
        gender = sharedPref.getString("gender", "");
        dob = sharedPref.getString("dob", "");

        user = (TextView)findViewById(R.id.username);
        user.setText(username);

        e_mail = (EditText)findViewById(R.id.email);
        phone_num = (EditText)findViewById(R.id.phone);
        gder = (EditText)findViewById(R.id.gender);
        dateob = (EditText)findViewById(R.id.dob);


        e_mail.setFocusableInTouchMode(false);
        phone_num.setFocusableInTouchMode(false);
        //gder.setFocusableInTouchMode(false);
        gder.setFocusableInTouchMode(false);
        dateob.setFocusableInTouchMode(false);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success ) {
                        phonenumber = jsonResponse.getString("phone");
                        email = jsonResponse.getString("email");
                        gender = jsonResponse.getString("gender");
                        dob = jsonResponse.getString("dob");
                        e_mail.setText(email);
                        phone_num.setText(phonenumber);
                        gder.setText(gender);
                        dateob.setText(dob);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        UserRequest userRequest = new UserRequest("1",userID, "", "","" ,"", responseListener);
        RequestQueue queue = Volley.newRequestQueue(User.this);
        queue.add(userRequest);
    }

    public void editButtonClick(View view){
        e_mail.setFocusableInTouchMode(true);
        gder.setFocusableInTouchMode(true);
        dateob.setFocusableInTouchMode(true);
        phone_num.setFocusableInTouchMode(true);
    }

    public void saveButtonClick(View view){

        final String E_mail = e_mail.getText().toString();
        final String Phone_num = phone_num.getText().toString();
        final String Gder = gder.getText().toString();
        final String Dateob = dateob.getText().toString();


        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success ) {
                        Toast.makeText(User.this , "record submitted", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        UserRequest userRequest = new UserRequest("2",userID, E_mail, Phone_num,Gder ,Dateob, responseListener);
        RequestQueue queue = Volley.newRequestQueue(User.this);
        queue.add(userRequest);
        e_mail.setFocusableInTouchMode(false);
        e_mail.setFocusable(false);
        phone_num.setFocusableInTouchMode(false);
        phone_num.setFocusable(false);
        gder.setFocusableInTouchMode(false);
        gder.setFocusable(false);
        dateob.setFocusableInTouchMode(false);
        dateob.setFocusable(false);
    }


}

class UserRequest extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "http://cgi.soic.indiana.edu/~yaokzhan/team32/User.php";
    private Map<String, String> params;

    public UserRequest(String state, String userID, String e_mail, String phone_num, String gder, String dateob, Response.Listener<String> listener) {
        super(Request.Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("state", state);
        params.put("userID", userID);
        params.put("e_mail", e_mail);
        params.put("phone_num", phone_num);
        params.put("gder", gder);
        params.put("dateob", dateob);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

