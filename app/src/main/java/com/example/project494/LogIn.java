package com.example.project494;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class LogIn extends AppCompatActivity {

    EditText Username, Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);


        Username = (EditText) findViewById(R.id.bar_userName);
        Password = (EditText) findViewById(R.id.ConfirmPassord);
        Button bLogin = (Button) findViewById(R.id.Register);
        TextView registerLink = (TextView) findViewById(R.id.haveAccount);

        registerLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent registerIntent = new Intent(LogIn.this, MainActivity.class);
                LogIn.this.startActivity(registerIntent);
            }
        });
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = Username.getText().toString();
                final String password = Password.getText().toString();

                // Response received from the server
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success && !username.equals("") && !password.equals("")) {
                                String name = jsonResponse.getString("username");
                                String phone = jsonResponse.getString("phone_number");
                                String id = jsonResponse.getString("userID");

                                String email = jsonResponse.getString("email");
                                String gender = jsonResponse.getString("gender");
                                String dob = jsonResponse.getString("dob");

                                Intent intent = new Intent(LogIn.this, Bar.class);
                                LogIn.this.startActivity(intent);

                                SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

                                intent.putExtra("username", name);
                                LogIn.this.startActivity(intent);

                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("username", name);
                                editor.putString("phonenumber", phone);
                                editor.putString("id", id);
                                editor.putString("email", email);
                                editor.putString("gender", gender);
                                editor.putString("dob", dob);

                                editor.apply();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LogIn.this);
                                if (username.equals("")) {
                                    builder.setMessage("Please Enter Username")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                } else if (password.equals("")) {
                                    builder.setMessage("Please Enter Password")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                } else {
                                    builder.setMessage("Login Failed")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(username, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LogIn.this);
                queue.add(loginRequest);
            }
        });
    }
}
