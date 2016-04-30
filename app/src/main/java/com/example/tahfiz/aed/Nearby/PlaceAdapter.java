package com.example.tahfiz.aed.Nearby;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tahfiz.aed.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tahfiz on 5/3/2016.
 */
public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.MyViewHolder>{

    private List<PlaceData> placeList = new ArrayList<PlaceData>();

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView time;
        private final TextView distance;
        private final TextView name;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.placeName);
            distance = (TextView) view.findViewById(R.id.placeDistance);
            time = (TextView) view.findViewById(R.id.placeTime);
        }
    }

    public void setPlaceData(List<PlaceData> data){
        placeList.clear();

        if (data != null){
            for (PlaceData placeEntry : data){
                placeList.add(placeEntry);
            }
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_row,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PlaceData place = placeList.get(position);

        holder.name.setText(place.getPlaceName());
        holder.distance.setText(place.getDistance());

        double min = place.getDuration()/60;

        String hours = "<h3>" + (int)(min/60) + "</h3> h";
        String minute = "<h3>" + (int)(min%60) + "</h3> mins";

        if ((int)(min/60) != 0)
            holder.time.setText(Html.fromHtml(hours + minute));
        else {
            holder.time.setText(Html.fromHtml(minute));
        }
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }
}
