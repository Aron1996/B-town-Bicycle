package com.example.project494;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void OnMainToLoginButton(View view) {
        Intent mIntent = new Intent(this, LogIn.class);
        startActivity(mIntent);
    }

    public void OnMainToLoginTextView(View view) {
        Intent mIntent = new Intent(this, LogIn.class);
        startActivity(mIntent);
    }
}