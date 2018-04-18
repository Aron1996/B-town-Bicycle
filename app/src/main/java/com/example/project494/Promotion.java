package com.example.project494;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Promotion extends AppCompatActivity {

    EditText promCode;
    Button submit;
    int moneyleft;
    String promotion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);

        promCode = (EditText)findViewById(R.id.code);
        submit = (Button)findViewById(R.id.submit);

        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        final String userID = sharedPref.getString("id", "");


            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    promotion = promCode.getText().toString();
                    if (promotion.equals("Y02Rjade")){
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if (success) {
                                        Toast.makeText(Promotion.this, "2 dollar add in wallet", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        paymentADD paymentaDD = new paymentADD(userID, "2", "1",responseListener);
                        moneyleft+=10;
                        RequestQueue queue = Volley.newRequestQueue(Promotion.this);
                                    queue.add(paymentaDD);
                    }else{
                        Toast.makeText(Promotion.this, "Not a valid promotion code", Toast.LENGTH_SHORT).show();
                    }

                    promCode.setText("");
                }
            });
    }
}
