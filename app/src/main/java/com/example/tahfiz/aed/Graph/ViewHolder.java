package com.example.tahfiz.aed.Graph;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.tahfiz.aed.R;

/**
 * Created by tahfiz on 28/3/2016.
 */
public class ViewHolder extends RecyclerView.ViewHolder {
    public ViewHolder(View itemView) {
        super(itemView);
    }
}

class DateViewHolder extends  ViewHolder{

    TextView date;

    public DateViewHolder(View itemView) {
        super(itemView);
        this.date = (TextView) itemView.findViewById(R.id.dateTxtView);
    }
}

class AverageViewHolder extends ViewHolder{

    TextView avgHR;

    public AverageViewHolder(View itemView) {
        super(itemView);
        this.avgHR = (TextView) itemView.findViewById(R.id.avgTxtView);
    }
}

class HighestViewHolder extends ViewHolder{

    TextView highHR;

    public HighestViewHolder(View itemView) {
        super(itemView);
        this.highHR = (TextView) itemView.findViewById(R.id.highTxtView);
    }
}

class LowestViewHolder extends ViewHolder{
    TextView lowHR;

    public LowestViewHolder(View v){
        super(v);
        this.lowHR = (TextView) v.findViewById(R.id.lowTxtView);
    }
}
