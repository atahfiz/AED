package com.example.tahfiz.aed.Nearby;

import android.util.Log;

import java.util.List;

/**
 * Created by tahfiz on 19/3/2016.
 */
public class GooglePlaces {

    //Google API KEY
    private static final String GOOGLE_API_KEY = "AIzaSyCjyNJbZkyHcU3qA9dG3ml5BlskTEc6IBU";

    private int PROXIMITY_RADIUS = 5000; //5.0KM
    private String NAME = "hospital";// emergency+hospital
    private String TYPE = "hospital";

    private StringBuilder googlePlacesUrl;
    private StringBuilder googleDistTimeURL;

    public String searchNearby(double latitude, double longitude) {

        try {

            googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            googlePlacesUrl.append("location=" + latitude + "," + longitude);
            //googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
            googlePlacesUrl.append("&rankby=distance");
            googlePlacesUrl.append("&name=" + NAME);
            googlePlacesUrl.append("&type=" + TYPE);
            googlePlacesUrl.append("&sensor=true");
            googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);

            if (googlePlacesUrl.length() > 0)
                System.out.println("Exist GooglePlacesURL: " + googlePlacesUrl.toString());

        }catch (Exception e){
            e.printStackTrace();
        }

        return googlePlacesUrl.toString();
    }

    public String distanceMatrix(List<PlaceData> googlePlace,double latitude, double longitude){

        try {

        googleDistTimeURL = new StringBuilder("https://maps.googleapis.com/maps/api/distancematrix/json?");
        googleDistTimeURL.append("origins=" + latitude + "," + longitude);
        googleDistTimeURL.append("&destinations=");

        int counter = 0;

        for (PlaceData place:googlePlace){
            if (counter != (googlePlace.size()-1)){
                googleDistTimeURL.append(place.getLatitude() + "," + place.getLongitude() + "|");
            }else {
                googleDistTimeURL.append(place.getLatitude() + "," + place.getLongitude());
            }
            counter++;
        }
        //googleDistTimeURL.append("&traffic_model=" + traffic_model);
        googleDistTimeURL.append("&key=" + GOOGLE_API_KEY);

            if (googleDistTimeURL.length() > 0)
                System.out.println("Exist GooglePlacesURL: " + googleDistTimeURL.toString());

    }catch (Exception e){
        Log.e("Exception", e.toString());
    }

        return googleDistTimeURL.toString();
    }
}
