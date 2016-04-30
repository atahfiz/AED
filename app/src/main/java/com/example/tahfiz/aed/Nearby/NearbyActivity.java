package com.example.tahfiz.aed.Nearby;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.TypedArray;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tahfiz.aed.Drawer.BaseActivity;
import com.example.tahfiz.aed.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NearbyActivity extends BaseActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private static final int PLAY_SERVICE_RESOLUTION_REQUEST = 1000;

    private Button buttonGet;
    private PlaceListFragment placeListFrag;

    private GoogleApiClient googleApiClient;
    private Location lastKnownLocation;
    private LocationRequest locationRequest;
    private boolean requestingLocationUpdates = false;
    private GooglePlaces googlePlaces;
    private Places places;

    //Location Update Interval
    private static int UPDATE_INTERVAL = 10000; //10sec
    private static int FASTEST_INTERVAL = 5000; //5sec
    private static final int DISPLACEMENT = 10;//10meters

    private double latitude;
    private double longitude;
    private FragmentManager manager;
    private NearbyRepo nearbyRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items); // load
        // titles
        // from
        // strings.xml

        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);// load icons from
        // strings.xml

        set(navMenuTitles, navMenuIcons);

        buttonGet = (Button) findViewById(R.id.get_data_button);

        googlePlaces = new GooglePlaces();
        places = new Places();
        nearbyRepo = new NearbyRepo(this);
        placeListFrag = new PlaceListFragment();
        setFragment(placeListFrag);


        //Google Play Services Availability
        if (checkPlayServices()){
            buildGoogleApiClient();

            createLocationRequest();
        }

        buttonGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLocation();
                sendRequestNearby(latitude, longitude);
            }
        });
    }

    //First Request Volley for search nearby places
    private void sendRequestNearby(double latitude, double longitude) {

        String URL_NEARBY = googlePlaces.searchNearby(latitude, longitude);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL_NEARBY, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        try {
                            if (jsonObject.getString("status").equals("OK")){
                                sendRequestDist(places.parse(jsonObject));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        handleVolleyError(volleyError);
                    }
                });

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }


    // Second Volley Request for distance and time matrix
    private void sendRequestDist(final List<PlaceData> data) {

        String URL_DIST = googlePlaces.distanceMatrix(data, latitude, longitude);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL_DIST, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        try {
                            if (jsonObject.getString("status").equals("OK")){
                                nearbyRepo.insertPlaces(places.parseDistTime(jsonObject, data));
                                manager.beginTransaction().replace(R.id.placeContainer,placeListFrag).commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        handleVolleyError(volleyError);
                    }
                });
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    //Error Handler for Volley Request
    private void handleVolleyError(VolleyError volleyError) {
        if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError){
            Toast.makeText(this, "No Connection", Toast.LENGTH_LONG).show();

        }else if (volleyError instanceof AuthFailureError){
            Toast.makeText(this, "Authentication Failure",Toast.LENGTH_LONG).show();

        }else if (volleyError instanceof ServerError){
            Toast.makeText(this,"Response Error",Toast.LENGTH_LONG).show();

        }else if (volleyError instanceof NetworkError){
            Toast.makeText(this,"Network Error",Toast.LENGTH_LONG).show();

        }else if (volleyError instanceof ParseError){
            Toast.makeText(this,"Cannot Parse",Toast.LENGTH_LONG).show();
        }
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void displayLocation() {

        try {
            lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            if (lastKnownLocation != null){
                latitude = lastKnownLocation.getLatitude();
                longitude = lastKnownLocation.getLongitude();

                buttonGet.setText(latitude + "," + longitude);

            }else {
                Toast.makeText(this,"Cannot find location",Toast.LENGTH_LONG).show();
            }

        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    //Create Google API Client object
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private boolean checkPlayServices() {
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (result != ConnectionResult.SUCCESS){
            if (GooglePlayServicesUtil.isUserRecoverableError(result)){
                GooglePlayServicesUtil.getErrorDialog(result,this,PLAY_SERVICE_RESOLUTION_REQUEST).show();

            }else {
                Toast.makeText(getApplicationContext(), "This device is not supported", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null){
            googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();

        //resuming the periodic location updates
        if (googleApiClient.isConnected() && requestingLocationUpdates){
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    protected void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        displayLocation();

        if (requestingLocationUpdates){
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        //Assign new location
        lastKnownLocation = location;

        Toast.makeText(getApplicationContext(),"Location Changed",Toast.LENGTH_SHORT).show();

        displayLocation();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void setFragment(Fragment fragment) {
        manager = getFragmentManager();

        if (manager.findFragmentById(R.id.placeContainer) == null){
            manager.beginTransaction().add(R.id.placeContainer,fragment).commit();
        }
    }
}
