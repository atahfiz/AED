package com.example.tahfiz.aed.Nearby;

/**
 * Created by tahfiz on 29/2/2016.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Places {

    // Search nearby API
    public List<PlaceData> parse(JSONObject jsonObject) {
        JSONArray jsonArrayPlace = null;

        try {
            jsonArrayPlace = jsonObject.getJSONArray("results");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaces(jsonArrayPlace);
    }

    private List<PlaceData> getPlaces(JSONArray jsonArray) {
        int placesCount = jsonArray.length();
        List<PlaceData> placesList = new ArrayList<PlaceData>();
        PlaceData placeMap = null;

        for (int i = 0; i < placesCount; i++) {
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placesList;
    }

    private PlaceData getPlace(JSONObject googlePlaceJson) {
        PlaceData googlePlaceMap = new PlaceData();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";

        try {
            if (!googlePlaceJson.isNull("name")) {
                placeName = googlePlaceJson.getString("name");
            }
            if (!googlePlaceJson.isNull("vicinity")) {
                vicinity = googlePlaceJson.getString("vicinity");
            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJson.getString("reference");
            googlePlaceMap.setPlaceName(placeName);
            //googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.setLatitude(latitude);
            googlePlaceMap.setLongitude(longitude);
            //googlePlaceMap.put("reference", reference);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaceMap;
    }

    //Search Distance and Time Matriz API
     public List<PlaceData> parseDistTime(JSONObject jsonObject,List<PlaceData> dataPlaceList) {
        JSONArray jsonArray = null;
        JSONArray jsonArrayDistTime = null;

        try {
            jsonArray = jsonObject.getJSONArray("rows");
            int numOfItemResp = jsonArray.length();

            for (int i=0; i<numOfItemResp; i++){
                jsonArrayDistTime = jsonArray.getJSONObject(i).getJSONArray("elements");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlacesDistTime(jsonArrayDistTime,dataPlaceList);
    }

    private List<PlaceData> getPlacesDistTime(JSONArray jsonArray,List<PlaceData> dataPlaceList) {
        int placesCount = jsonArray.length();
        List<PlaceData> placesList = new ArrayList<PlaceData>();
        PlaceData placeMap = null;
        PlaceData data = null;

        for (int i = 0; i < placesCount; i++) {
            try {
                placeMap = getPlaceDistTime((JSONObject) jsonArray.get(i));
                data = dataPlaceList.get(i);
                placeMap.setPlaceName(data.getPlaceName());
                placesList.add(placeMap);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placesList;
    }

    private PlaceData getPlaceDistTime(JSONObject googlePlaceJson) {
        PlaceData googlePlaceMap = new PlaceData();
        String duration = "";
        String distance = "";

        try {
            duration = googlePlaceJson.getJSONObject("duration").getString("value");
            distance = googlePlaceJson.getJSONObject("distance").getString("text");
            googlePlaceMap.setDistance(distance);
            googlePlaceMap.setDuration(Double.parseDouble(duration));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaceMap;
    }
}
