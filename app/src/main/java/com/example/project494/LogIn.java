package com.example.project494;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class LogIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
    }


    public void OnMainToMapButton(View view) {
        Intent mIntent = new Intent(this, Bar.class);
        startActivity(mIntent);

    }

    public void OnMainToRegisterTextView(View view) {
        Intent mIntent = new Intent(this, MainActivity.class);
        startActivity(mIntent);
    }
}