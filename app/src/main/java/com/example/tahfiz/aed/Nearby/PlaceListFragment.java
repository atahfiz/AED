package com.example.tahfiz.aed.Nearby;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tahfiz.aed.R;

import java.util.List;

/**
 * Created by tahfiz on 9/4/2016.
 */
public class PlaceListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<PlaceData>>{

    public static final String TAG = "PlaceListFagment";
    private static final int INITIAL_LOADER = 10;

    private RecyclerView placeRV;
    private PlaceAdapter adapter;
    private TextView emptyMsg;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.place_list_fragment,container,false);

        placeRV = (RecyclerView) view.findViewById(R.id.placeRV);
        emptyMsg = (TextView) view.findViewById(R.id.emptyMsg);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new PlaceAdapter();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        placeRV.addItemDecoration(new DividerItemDecoration(getActivity(),LinearLayoutManager.VERTICAL));
        placeRV.setLayoutManager(layoutManager);
        placeRV.setAdapter(adapter);

        //Initialize the loader
        getLoaderManager().initLoader(INITIAL_LOADER,null,this).forceLoad();
    }

    @Override
    public Loader<List<PlaceData>> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "Fragment in onCreateLoder");
        return new PlaceListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<PlaceData>> loader, List<PlaceData> data) {
        adapter.setPlaceData(data);
        Log.i(TAG, "Fragment in onLoadFinished");

        if(adapter.getItemCount() > 0){
            placeRV.setVisibility(View.VISIBLE);
            emptyMsg.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<PlaceData>> loader) {
        adapter.setPlaceData(null);
    }
}
