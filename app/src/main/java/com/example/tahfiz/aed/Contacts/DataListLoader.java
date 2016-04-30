package com.example.tahfiz.aed.Contacts;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by tahfiz on 8/3/2016.
 */
public class DataListLoader extends AsyncTaskLoader<List<ContactData>> {

    ContactRepo repo;
    private static List<ContactData> contact;

    public DataListLoader(Context context){
        super(context);

        repo = new ContactRepo(context);
    }

    @Override
    public List<ContactData> loadInBackground() {

        List<ContactData> entries;
        entries = repo.getContactList();

        return entries;
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override
    public void deliverResult(List<ContactData> listOfData) {

        if (isReset()){
            //An async query came in while the loader stop
            if (listOfData != null){
                onReleaseResources(listOfData);
            }
        }
        List<ContactData> oldEntries = listOfData;
        contact = listOfData;

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

        if (contact != null){
            //If we currently have a result available, deliver it
            deliverResult(contact);
        }

        if (takeContentChanged() || contact != null){
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
    public void onCanceled(List<ContactData> data) {
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

        if (contact != null){
            onReleaseResources(contact);
            contact = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<ContactData> data){}
}
