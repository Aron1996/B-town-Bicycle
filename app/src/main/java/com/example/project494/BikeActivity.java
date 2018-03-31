package com.example.project494;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangyaokun on 3/24/18.
 */

public class BikeActivity extends StringRequest {

    private static final String REGISTER_REQUEST_URL = "http://cgi.soic.indiana.edu/~yaokzhan/team32/bike.php";
    private Map<String, String> params;

    public BikeActivity(String bikeID, Response.Listener<String> listener) {
        super(Request.Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("bikeID", bikeID);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

