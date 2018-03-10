package com.example.project494;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    EditText PhoneNumber, Username, Password, ConfirmPass;
    TextView HaveAccount;
    private static final boolean USE_FLAG = true;
    private static final int mFlag = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Username = (EditText)findViewById(R.id.bar_userName);
        PhoneNumber = (EditText)findViewById(R.id.phoneNumber);
        Password = (EditText)findViewById(R.id.password);
        ConfirmPass = (EditText)findViewById(R.id.ConfirmPassord);
        Button bRegister = (Button) findViewById(R.id.Register);
        HaveAccount = findViewById(R.id.haveAccount);
        HaveAccount.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                Intent registerIntent = new Intent(MainActivity.this, LogIn.class);
                MainActivity.this.startActivity(registerIntent);
            }
        });

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = Username.getText().toString();
                final String phone = PhoneNumber.getText().toString();
                final String password = Password.getText().toString();
                final String confirmPass = ConfirmPass.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success && !(username.equals("")||phone.equals("")||password.equals("")||!password.equals(confirmPass))) {
                                Intent intent = new Intent(MainActivity.this, LogIn.class);
                                MainActivity.this.startActivity(intent);

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                if (username.equals("")){
                                    builder.setMessage("Please enter username")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                }
                                else if(phone.equals("")){
                                    builder.setMessage("Please enter phone number")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                }
                                else if(password.equals("")){
                                    builder.setMessage("Please enter password")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                }
                                else if (!password.equals(confirmPass)){
                                    builder.setMessage("The two passwords you enter don't match")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                }
                                else{
                                    builder.setMessage("Register Failed")
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

                RegisterRequest registerRequest = new RegisterRequest(username, phone, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(registerRequest);
            }
        });
    }

    public void OnMainToLoginTextView(View view) {
        Intent mIntent = new Intent(this, LogIn.class);
        startActivity(mIntent);
    }
}