package com.example.tahfiz.aed.Graph;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.tahfiz.aed.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by tahfiz on 28/3/2016.
 */
public class GraphFragment extends Fragment implements
        OnChartValueSelectedListener,
        OnChartGestureListener {

    private LineChart lineChart;
    private GraphRepo repo;

    GraphListener actvGraph;
    private Calendar cal;

    public interface GraphListener{
        public void sendGraphData(String date);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            actvGraph = (GraphListener) activity;

        }catch (ClassCastException e){
            throw  new ClassCastException(activity.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.graph_fragment,container,false);

        repo = new GraphRepo(getActivity());

        lineChart = (LineChart) view.findViewById(R.id.chart);
        lineChart.setOnChartGestureListener(this);
        lineChart.setOnChartValueSelectedListener(this);

        //Description
        //lineChart.setDescription("Heart analysis");
        lineChart.setNoDataTextDescription("You need to provide data for the chart");

        //enable touch gesture
        lineChart.setTouchEnabled(true);

        //enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MyMarkerView mv = new MyMarkerView(getActivity(),R.layout.custom_marker_view);

        //set the marker to the chart
        lineChart.setMarkerView(mv);

        //X-axis line
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setLabelsToSkip(13);// Skip X-axis labels

        //Y-axis line
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines();// reset all limit lines to avoid overlapping lines
        leftAxis.setDrawZeroLine(false);// Axis line


        lineChart.getAxisRight().setEnabled(false);

        setData();

        lineChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);

        Legend legend = lineChart.getLegend();

        legend.setForm(Legend.LegendForm.CIRCLE);

        return view;
    }

    public void setData(){
        cal = Calendar.getInstance();
        cal.set(Calendar.MONTH,2);//March
        cal.set(Calendar.DAY_OF_MONTH,1);//Day
        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        SimpleDateFormat df = new SimpleDateFormat("dd MMM");

        //Creating list of entry
        ArrayList<Entry> yVals = new ArrayList<>();
        int pos=0;

        for (GraphData data : repo.getDataList()){
            yVals.add(new Entry((float)data.getHighest(),pos++));
        }

       /* yVals.add(new Entry(18f,0));
        yVals.add(new Entry(29f,1));
        yVals.add(new Entry(10f,2));*/

        //Create dataset and give it a type
        LineDataSet dataset = new LineDataSet(yVals, "bpm");

        //set the line
        dataset.setColor(Color.RED);
        dataset.setCircleColor(Color.RED);
        dataset.setLineWidth(1f);
        dataset.setCircleRadius(3f);
        dataset.setDrawCircleHole(false);
        dataset.setDrawValues(false);

        // creating X-axis labels
        ArrayList<String> xLabels = new ArrayList<String>();

        for (int i=0; i<maxDay; i++){
            cal.set(Calendar.DAY_OF_MONTH,i+1);
            xLabels.add(df.format(cal.getTime()));
        }
        System.out.println(maxDay);

        LineData data = new LineData(xLabels,dataset);

        lineChart.setData(data); // set the data and list of lables into chart
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            lineChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("X Label:", lineChart.getXValue(e.getXIndex()));

        String xVal = lineChart.getXValue(e.getXIndex());
        String[] part = xVal.split(" ");
        String day = part[0];
        String month = part[1];

        Log.i("Day: ", day);
        Log.i("Month: ", month);
        String date = cal.get(Calendar.YEAR) + "-" + month + "-" + day;
        actvGraph.sendGraphData(date);
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    /*private int convertMon(String month){
        switch (month){
            case "Jan":
                return 1;

            case "Feb":
                return 2;

            case "Mar":
                return 3;

            case "Apr":
                return 4;

            case "May":
                return 5;

            case "Jun":
                return 6;

            case "Jul":
                return 7;

            case "Aug":
                return 8;

            case "Sep":
                return 9;

            case "Oct":
                return 10;

            case "Nov":
                return 11;

            case "Dec":
                return 12;

            default:
                return 0;
        }
    }*/
}
