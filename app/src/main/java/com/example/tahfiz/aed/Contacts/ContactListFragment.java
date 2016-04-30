package com.example.tahfiz.aed.Contacts;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tahfiz.aed.R;

import java.util.List;

/**
 * Created by tahfiz on 4/4/2016.
 */
public class ContactListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<List<ContactData>>{

    private static final String TAG = ContactListFragment.class.getSimpleName();

    private static final int INITIAL_LOADER = 0;

    private ContactAdapter adapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_list_fragment,container,false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new ContactAdapter(getActivity());
        setListAdapter(adapter);

        //Initialize the loader
        getLoaderManager().initLoader(INITIAL_LOADER,null,this).forceLoad();
    }

    @Override
    public Loader<List<ContactData>> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "Fragment in onCreateLoder");
        return new DataListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<ContactData>> loader, List<ContactData> data) {
        adapter.setData(data);
        Log.i(TAG,"Fragment in onLoadFinished");
    }

    @Override
    public void onLoaderReset(Loader<List<ContactData>> loader) {
        adapter.setData(null);
    }
}
