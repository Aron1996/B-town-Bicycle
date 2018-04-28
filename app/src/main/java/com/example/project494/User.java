package com.example.project494;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User extends AppCompatActivity {


    String userID, username, phonenumber, email, gender, dob;
    TextView user;
    EditText e_mail, phone_num, gder, dateob;
    ImageView profile;
    int RESULT_LOAD_IMAGE;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    Bitmap image;
    Uri filePath;
    Bitmap downloadImage;


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
        profile = (ImageView)findViewById(R.id.profile);


        e_mail.setFocusableInTouchMode(false);
        phone_num.setFocusableInTouchMode(false);
        //gder.setFocusableInTouchMode(false);
        gder.setFocusableInTouchMode(false);
        dateob.setFocusableInTouchMode(false);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Picasso.get().load(new File("/storage/emulated/0/FitnessGirl.jpg")).into(profile);


//        downLoadImage();

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

    public void selectClick(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            filePath = data.getData();
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                //upload
                if(filePath != null)
                {
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();

                    StorageReference ref = storageReference.child("images/"+ username);
                    ref.putFile(filePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Toast.makeText(User.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(User.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                            .getTotalByteCount());
                                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                                }
                            });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(image, 100);
            profile.setImageBitmap(circularBitmap);
        }
    }

    public void downLoadImage(){

        StorageReference ref = storage.getReference().child("images/"+username);
        try {
            final File localFile = File.createTempFile("Images", "bmp");
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener< FileDownloadTask.TaskSnapshot >() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap my_image = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    downloadImage = ImageConverter.getRoundedCornerBitmap(my_image, 100);
                    profile.setImageBitmap(downloadImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(User.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

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



