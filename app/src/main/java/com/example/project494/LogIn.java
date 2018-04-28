package com.example.project494;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;


public class LogIn extends AppCompatActivity {

    EditText Username, Password;
    SharedPreferences sharedPref ;
    String name;
    private FirebaseStorage storage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        if (!sharedPref.getString("username","none").equals("none")){
            Intent intent = new Intent(LogIn.this, Bar.class);

            String name = sharedPref.getString("username",null);
            String id = sharedPref.getString("id",null);
            intent.putExtra("username", name);
            intent.putExtra("userID", id);
            startActivity(intent);
        }


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
                                name = jsonResponse.getString("username");
                                String phone = jsonResponse.getString("phone_number");
                                String id = jsonResponse.getString("userID");

                                String email = jsonResponse.getString("email");
                                String gender = jsonResponse.getString("gender");
                                String dob = jsonResponse.getString("dob");

                                Intent intent = new Intent(LogIn.this, Bar.class);


                                intent.putExtra("username", name);
                                intent.putExtra("userID", id);


                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("username", name);
                                editor.putString("phonenumber", phone);
                                editor.putString("id", id);
                                editor.putString("email", email);
                                editor.putString("gender", gender);
                                editor.putString("dob", dob);
//                                editor.putString("image", encodeFileToBase64Binary());

                                editor.apply();
                                LogIn.this.startActivity(intent);
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
                                            .setNegativeButton("Please Retry", null)
                                            .create()
                                            .show();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Internet available but you have to sign in.", Toast.LENGTH_SHORT).show();

                        }
                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                // Show timeout error message
                                Toast.makeText(getApplicationContext(),
                                        "Oops. Timeout error!",
                                        Toast.LENGTH_LONG).show();
                            } else if (error instanceof AuthFailureError) {
                                //TODO
                                Toast.makeText(getApplicationContext(),
                                        "Oops. AuthFailureError error!",
                                        Toast.LENGTH_LONG).show();
                            } else if (error instanceof ServerError) {
                                //TODO
                                Toast.makeText(getApplicationContext(),
                                        "Oops. ServerError error!",
                                        Toast.LENGTH_LONG).show();
                            } else if (error instanceof NetworkError) {
                                //TODO
                                Toast.makeText(getApplicationContext(),
                                        "Oops. NetworkError error!",
                                        Toast.LENGTH_LONG).show();
                            } else if (error instanceof ParseError) {
                                //TODO
                                Toast.makeText(getApplicationContext(),
                                        "Oops. ParseError error!",
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                    }

                };

                LoginRequest loginRequest = new LoginRequest(username, password, responseListener,errorListener);
                RequestQueue queue = Volley.newRequestQueue(LogIn.this);
                queue.add(loginRequest);
            }
        });
    }

//    private static String encodeFileToBase64Binary(File file){
//        String encodedFile = null;
//        try {
//            FileInputStream fileInputStreamReader = new FileInputStream(file);
//            byte[] bytes = new byte[(int)file.length()];
//            fileInputStreamReader.read(bytes);
//            encodedFile = Base64.getEncoder().encodeToString(bytes);
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        return encodedFile;
//    }
}
