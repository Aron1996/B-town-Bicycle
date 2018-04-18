package com.example.project494;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Bar extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener
{

    AlertDialog.Builder builder;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    ImageView user;
    TextView userName;
    String status, unlock, userID, value;
    SharedPreferences sharedPref;
    Double lat, longi;
    LatLng now, now2, now3, start, end ;
    private Marker marker, marker2, marker3;
    private static final int mFlag = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
    LinearLayout topbar, topbar2;
    int BICYCLESTATE = 0, balance = 0, finalBalance;
    TextView con;
    Button useBicycle, stopuseBicycle;
    Chronometer myChronometer;
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Location");


        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        userID = intent.getStringExtra("userID");

        topbar = (LinearLayout) findViewById(R.id.topbar);
        topbar2 = (LinearLayout) findViewById(R.id.topbar2);
        con = (TextView) findViewById(R.id.condition);
        useBicycle = (Button) findViewById(R.id.use);
        stopuseBicycle = (Button) findViewById(R.id.stop);
        myChronometer = (Chronometer) findViewById(R.id.chronometer);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        user = (ImageView) hView.findViewById(R.id.userHead);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(Bar.this, User.class);
                mIntent.putExtra("flag", "disable");
                startActivity(mIntent);
            }
        });
        userName = (TextView) hView.findViewById(R.id.bar_userName);
        userName.setText(username);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("username", "none");
            editor.apply();
            Intent intent = new Intent(Bar.this, LogIn.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.trips) {
            Intent mIntent = new Intent(this, Trip.class);
            mIntent.addFlags(mFlag);
            startActivity(mIntent);
        } else if (id == R.id.wallet) {
            Intent mIntent = new Intent(this, Wallet.class);
            startActivity(mIntent);

        } else if (id == R.id.promotion) {
            Intent mIntent = new Intent(this, Promotion.class);
            startActivity(mIntent);

        } else if (id == R.id.report) {
            Intent mIntent = new Intent(this, Report.class);
            startActivity(mIntent);

        } else if (id == R.id.contact) {
            Intent mIntent = new Intent(this, Contact.class);
            startActivity(mIntent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public Bitmap resizeMapIcons(int width, int height) {
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.ic_bike_round);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(b, width, height, false);
        return resizedBitmap;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                value = dataSnapshot.getValue(String.class);
                Log.d("value", value);
                String [] separated = value.split(",");
                String lati = separated[0].trim();
                String longi2 = separated[1].trim();
                lat = Double.parseDouble(lati);
                longi = Double.parseDouble(longi2);
                now = new LatLng(lat, longi);
                marker.remove();
                Float zoomLevel = mMap.getCameraPosition().zoom;
                marker = mMap.addMarker(new MarkerOptions()
                        .position(now)
                        .title("Bike")
                        .snippet("Avaliable to use ")
                        .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(Math.round(zoomLevel)*6, Math.round(zoomLevel) * 6))));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        now = new LatLng(0, 0);
        marker = mMap.addMarker(new MarkerOptions()
                .position(now)
                .title("Bike")
                .snippet("Avaliable to use ")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.batman)));

        now2 = new LatLng(39.167078, -86.518249);
        marker2 = mMap.addMarker(new MarkerOptions()
                .position(now2)
                .title("Bike")
                .snippet("Avaliable to use ")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike_round)));
        now3 = new LatLng(39.1566812, -86.491012);
        marker3 = mMap.addMarker(new MarkerOptions()
                .position(now3)
                .title("Bike")
                .snippet("Avaliable to use ")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bike_round)));


        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            Float lastZoomLevel = 1f;

            @Override
            public void onCameraIdle() {
                Float zoomLevel = mMap.getCameraPosition().zoom;
                if (zoomLevel != lastZoomLevel) {
                    lastZoomLevel = zoomLevel;
                    marker.remove();
                    marker2.remove();
                    marker3.remove();
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(now)
                            .title("Bike")
                            .snippet(bikeStatus(1)+" to use ")
                            .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(Math.round(lastZoomLevel)*6, Math.round(lastZoomLevel) * 6))));
                    marker2 = mMap.addMarker(new MarkerOptions()
                            .position(now2)
                            .title("Bike")
                            .snippet(bikeStatus(2)+" to use ")
                            .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(Math.round(lastZoomLevel)*6, Math.round(lastZoomLevel) * 6))));
                    marker3 = mMap.addMarker(new MarkerOptions()
                            .position(now3)
                            .title("Bike")
                            .snippet(bikeStatus(3)+" to use ")
                            .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(Math.round(lastZoomLevel)*6, Math.round(lastZoomLevel) * 6))));

                }
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(now));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                topbar.setVisibility(View.INVISIBLE);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

//                mMap.moveCamera(CameraUpdateFactory.newLatLng(now));
//                mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jArray = new JSONArray(response);
                            if (jArray.length() == 0) {
                            } else {
                                for (int i = 0; i < jArray.length(); i++) {
                                    try {
                                        JSONObject oneObject = jArray.getJSONObject(i);
                                        finalBalance = oneObject.getInt("balance");
                                        if (BICYCLESTATE == 0 && finalBalance >= 2){
                                            topbar.setVisibility(View.VISIBLE);
                                        }
                                        else if (finalBalance < 2){
                                            builder.setTitle("Not enough credit in wallet")
                                                    .setMessage("Click ok to exit")
                                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                paymentRequest paymentrequest = new paymentRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Bar.this);
                queue.add(paymentrequest);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(Bar.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(Bar.this);
                }

                if (bikeStatus(1).equals("yes")){
                    con.setText("Avaliable to use");
                    useBicycle.setVisibility(View.VISIBLE);

                    useBicycle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            start = now;
                            builder.setTitle("This trip will cost 2 dollars")
                                    .setMessage("Click ok to continue")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            builder.setTitle("The Code is 0803")
                                                    .setMessage("Click ok to continue")
                                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                            setMoney(2);
                                            BICYCLESTATE =1;
                                            topbar.setVisibility(View.INVISIBLE);
                                            topbar2.setVisibility(View.VISIBLE);
                                            myChronometer.setBase(SystemClock.elapsedRealtime());
                                            myChronometer.start();
                                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    Log.e("B",response);
                                                }

                                            };
                                            addTrip addtrip = new addTrip("1", userID, "", "1", "","3", Integer.toString(BICYCLESTATE),responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(Bar.this);
                                            queue.add(addtrip);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();

                        }
                    });
                    stopuseBicycle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BICYCLESTATE =0;
                            end = now;
                            long elapsedMillis = SystemClock.elapsedRealtime() - myChronometer.getBase();
                            float[] results = new float[1];
                            Location.distanceBetween(start.latitude, start.longitude,
                                    end.latitude, end.longitude,
                                    results);

                            myChronometer.stop();
                            myChronometer.setBase(SystemClock.elapsedRealtime());
                            topbar2.setVisibility(View.INVISIBLE);
                            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.e("B",response);
                                }

                            };
                            Toast.makeText(Bar.this, Float.toString(results[0]/1000), Toast.LENGTH_SHORT).show();
                            addTrip addtrip = new addTrip("1", userID, date, Float.toString(results[0]/1000), Long.toString(elapsedMillis/60000),"2", Integer.toString(BICYCLESTATE),responseListener);
                            RequestQueue queue = Volley.newRequestQueue(Bar.this);
                            queue.add(addtrip);
                            Toast.makeText(Bar.this, "You finish this trip!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    con.setText("Not avaliable to use");
                    useBicycle.setVisibility(View.INVISIBLE);

                }
                return true;
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        Log.e("location", Double.toString(location.getLatitude()) + " " + Double.toString(location.getLongitude()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    public String bikeStatus(int ID) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    status = jsonResponse.getString("status");
                    unlock = jsonResponse.getString("unlock_code");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        };
        BikeActivity bikeActivity = new BikeActivity(Integer.toString(ID), responseListener);
        RequestQueue queue = Volley.newRequestQueue(Bar.this);
        queue.add(bikeActivity);

        return status;
    }

    public void setMoney(int amount){

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success ) {
                        Toast.makeText(Bar.this, "Use money from wallet.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        paymentADD paymentaDD = new paymentADD(userID, Integer.toString(amount), "", responseListener);
        RequestQueue queue = Volley.newRequestQueue(Bar.this);
        queue.add(paymentaDD);
    }

}

class addTrip extends StringRequest {
    private static final String LOGIN_REQUEST_URL = "http://cgi.soic.indiana.edu/~yaokzhan/team32/addriding.php";
    private Map<String, String> params;

    public addTrip(String bike_id, String user_id, String time, String distance, String duration, String cost, String state, Response.Listener<String>listener) {
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("bike_id", bike_id);
        params.put("user_id", user_id);
        params.put("time", time);
        params.put("distance", distance);
        params.put("duration", duration);
        params.put("cost", cost);
        params.put("state", state);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}