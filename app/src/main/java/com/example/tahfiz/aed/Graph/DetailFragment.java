package com.example.tahfiz.aed.Graph;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tahfiz.aed.R;

/**
 * Created by tahfiz on 28/3/2016.
 */
public class DetailFragment extends Fragment {

    private static final String TAG = DetailFragment.class.getSimpleName();
    private static final String DATE = "date";
    private static final String AVERAGE = "average";
    private static final String HIGHEST = "highest";
    private static final String LOWEST = "lowest";

    public static final int DATETYPE = 0;
    public static final int AVG = 1;
    public static final int HIGH = 2;
    public static final int LOW = 3;

    private int mDatasetTypes[] = {DATETYPE,AVG,HIGH,LOW}; //view types

    private RecyclerView rv;
    private  RecyclerView.LayoutManager layoutManager;
    private DetailAdapter adapter;
    private String date;
    private int avg;
    private int high;
    private int low;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public static DetailFragment newInstance(GraphData data){
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(DATE, data.getDate());
        args.putInt(AVERAGE, data.getAverage());
        args.putInt(HIGHEST, data.getHighest());
        args.putInt(LOWEST, data.getLowest());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            date = getArguments().getString(DATE);
            avg = getArguments().getInt(AVERAGE);
            high = getArguments().getInt(HIGHEST);
            low = getArguments().getInt(LOWEST);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.detail_fragment,container,false);

        Log.d(TAG, "Receive data from GraphActivity");

        rv = (RecyclerView) view.findViewById(R.id.recycleview);
        rv.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new DetailAdapter(date,avg,high,low,mDatasetTypes);
        rv.setAdapter(adapter);
    }
}
