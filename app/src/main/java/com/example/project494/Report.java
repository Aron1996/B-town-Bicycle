package com.example.project494;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Report extends AppCompatActivity {

    EditText bikeID, broken_part, description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);


        bikeID = (EditText)findViewById(R.id.bikeID);
        broken_part = (EditText)findViewById(R.id.broken_part);
        description = (EditText)findViewById(R.id.email);
        Button report = (Button) findViewById(R.id.report);

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String BikeID = bikeID.getText().toString();
                final String Broken_part = broken_part.getText().toString();
                final String Description = description.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success && !(BikeID.equals("")||Broken_part.equals("")||Description.equals(""))) {
                                Toast.makeText(Report.this , "record submitted", Toast.LENGTH_LONG).show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Report.this);
                                if (BikeID.equals("")){
                                    builder.setMessage("Please enter BikeID")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                }
                                else if(Broken_part.equals("")){
                                    builder.setMessage("Please enter phone Broken_part")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                }
                                else if(Description.equals("")){
                                    builder.setMessage("Please enter Description")
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

                ReportRequest reportRequest = new ReportRequest(BikeID, Broken_part, Description, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Report.this);
                queue.add(reportRequest);
            }
        });
    }
}

class ReportRequest extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "http://cgi.soic.indiana.edu/~yaokzhan/team32/report.php";
    private Map<String, String> params;

    public ReportRequest(String bikeID, String part, String description, Response.Listener<String> listener) {
        super(Request.Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("bikeID", bikeID);
        params.put("part", part);
        params.put("description", description);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

