package com.example.tahfiz.aed.Graph;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tahfiz.aed.R;

/**
 * Created by tahfiz on 28/3/2016.
 */
public class DetailAdapter extends RecyclerView.Adapter<ViewHolder> {

    public static final int DATE = 0;
    public static final int AVERAGE = 1;
    public static final int HIGHEST = 2;
    public static final int LOWEST = 3;
    private final String date;
    private final int average;
    private final int highest;
    private final int lowest;
    private final int[] mDataType;

    public DetailAdapter(String date,int average,int highest,int lowest,int[] dataType){
        this.date = date;
        this.average = average;
        this.highest = highest;
        this.lowest = lowest;
        this.mDataType = dataType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        switch (viewType){
            case DATE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.date_card,parent,false);
                return new DateViewHolder(v);

            case AVERAGE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.average_card,parent,false);
                return new AverageViewHolder(v);

            case  HIGHEST:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.highest_card,parent,false);
                return new HighestViewHolder(v);

            case LOWEST:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.lowest_card,parent,false);
                return new LowestViewHolder(v);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        if (viewHolder.getItemViewType() == DATE){
            DateViewHolder holder = (DateViewHolder) viewHolder;
            holder.date.setText(date);

        }else if (viewHolder.getItemViewType() == AVERAGE){
            AverageViewHolder holder = (AverageViewHolder) viewHolder;
            String avg = "<b>"+ average +"</b> beats/minute";
            holder.avgHR.setText(Html.fromHtml(avg));

        }else if (viewHolder.getItemViewType() == HIGHEST){
            HighestViewHolder holder = (HighestViewHolder) viewHolder;
            String high = "<b>"+ highest +"</b> beats/minute";
            holder.highHR.setText(Html.fromHtml(high));

        }else{
            LowestViewHolder holder = (LowestViewHolder) viewHolder;
            String low = "<b>"+ lowest +"</b> beats/minute";
            holder.lowHR.setText(Html.fromHtml(low));
        }

    }

    @Override
    public int getItemCount() {
        return mDataType.length;
    }

    @Override
    public int getItemViewType(int position) {
        return mDataType[position];
    }
}
