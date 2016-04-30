package com.example.tahfiz.aed.Nearby;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;

/**
 * Created by tahfiz on 18/3/2016.
 */
public class GPSTracker extends JobService implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    Location currentLocation;
    Context context;
    LocationRequest locationRequest;
    GoogleApiClient googleApiClient;


    double latitude;
    double longitude;

    protected void createLocationRequest(){
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    public GPSTracker(Context context){
        this.context = context;
        buildGoogleApiClient();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        createLocationRequest();

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            if (currentLocation != null){
                latitude = currentLocation.getLatitude();
                longitude = currentLocation.getLongitude();
            }

        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    public double getLatitude(){
        if (currentLocation != null){
            latitude = currentLocation.getLatitude();
        }
        return latitude;
    }

    public double getLongitude(){
        if (currentLocation != null){
            longitude = currentLocation.getLongitude();
        }
        return longitude;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        new PlaceTask(this).execute(jobParameters);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    private static class PlaceTask extends AsyncTask<JobParameters,Void,JobParameters>{

        GPSTracker gps;

        PlaceTask(GPSTracker gps){
            this.gps = gps;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JobParameters doInBackground(JobParameters... params) {
            Log.d("PlaceTask","Location: " + gps.getLatitude() + "," + gps.getLongitude());
            return params[0];
        }

        @Override
        protected void onPostExecute(JobParameters jobParameters) {
            gps.jobFinished(jobParameters,false);
        }
    }
}
