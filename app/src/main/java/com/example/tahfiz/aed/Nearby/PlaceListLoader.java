package com.example.tahfiz.aed.Nearby;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by tahfiz on 8/3/2016.
 */
public class PlaceListLoader extends AsyncTaskLoader<List<PlaceData>> {

    NearbyRepo nearbyRepo;
    private static List<PlaceData> place;

    public PlaceListLoader(Context context){
        super(context);

        nearbyRepo = new NearbyRepo(context);
    }

    @Override
    public List<PlaceData> loadInBackground() {

        List<PlaceData> entries;
        entries = nearbyRepo.getPlaceList();

        return entries;
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override
    public void deliverResult(List<PlaceData> listOfData) {

        if (isReset()){
            //An async query came in while the loader stop
            if (listOfData != null){
                onReleaseResources(listOfData);
            }
        }
        List<PlaceData> oldEntries = listOfData;
        place = listOfData;

        if (isStarted()){
            //If the Loader is currently started
            super.deliverResult(listOfData);
        }

        //Release the data
        if (oldEntries != null){
            onReleaseResources(oldEntries);
        }
    }

    @Override
    protected void onStartLoading() {

        if (place != null){
            //If we currently have a result available, deliver it
            deliverResult(place);
        }

        if (takeContentChanged() || place != null){
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(List<PlaceData> data) {
        super.onCanceled(data);

        onReleaseResources(data);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();

        //Ensure the loader is stopped
        onStartLoading();

        if (place != null){
            onReleaseResources(place);
            place = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<PlaceData> data){}
}
