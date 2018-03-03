package com.example.project494;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class Trip extends AppCompatActivity {

    ListView listView;
    String userID, username, phonenumber,date_ride, id, distance, cost;
    static ArrayList<Tripentry> tripList = new ArrayList<>();
    Tripentry trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        listView = (ListView) findViewById(R.id.lv);

        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        userID = sharedPref.getString("id", "");
        username = sharedPref.getString("username", "");
        phonenumber = sharedPref.getString("phonenumber", "");

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jArray = new JSONArray(response);
                    if(jArray.length() == 0){
                        Toast.makeText(Trip.this,"Doesn't have any record yet",Toast.LENGTH_SHORT).show();
                    }else {
                        for (int i = 0; i < jArray.length(); i++) {
                            try {
                                JSONObject oneObject = jArray.getJSONObject(i);
                                date_ride = oneObject.getString("date_ride");
                                id = oneObject.getString("bike_id");
                                distance = oneObject.getString("distance");
                                cost = oneObject.getString("cost");
                                trip = new Tripentry(date_ride, id, distance, cost);
                                tripList.add(trip);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        ThreeColumn_ListAdapter adapter = new ThreeColumn_ListAdapter(Trip.this, R.layout.list_adapter_view, tripList);
                        listView.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };



        TripRequest tripRequest = new TripRequest(userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(Trip.this);
        queue.add(tripRequest);



        /*Toast.makeText(Trip.this,tripList.size()+"", Toast.LENGTH_LONG).show();
        ThreeColumn_ListAdapter adapter = new ThreeColumn_ListAdapter(Trip.this, R.layout.list_adapter_view, tripList);
        listView.setAdapter(adapter);*/

    }

    public static void main(String[] args){
        System.out.println(tripList.size());
    }
}
class Tripentry {

    private String date;
    private String id;
    private String distance;
    private String cost;
    public Tripentry(String date_ride,String id_ride, String distance_ride, String cost_ride){
        date = date_ride;
        id = id_ride;
        distance = distance_ride;
        cost = cost_ride;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

}
class TripRequest extends StringRequest {
    private static final String LOGIN_REQUEST_URL = "http://cgi.soic.indiana.edu/~yaokzhan/team32/riding.php";
    private Map<String, String> params;

    public TripRequest(String id, Response.Listener<String> listener) {
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("userID", id);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}