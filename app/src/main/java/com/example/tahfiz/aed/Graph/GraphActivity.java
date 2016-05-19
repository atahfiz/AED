package com.example.tahfiz.aed.Graph;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.tahfiz.aed.Drawer.BaseActivity;
import com.example.tahfiz.aed.R;

import java.util.Random;

public class GraphActivity extends BaseActivity implements GraphFragment.GraphListener{

    private static final String TAG = GraphActivity.class.getSimpleName();
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private GraphRepo graphRepo;
    private GraphData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items); // load
        // titles
        // from
        // strings.xml

        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);// load icons from
        // strings.xml

        set(navMenuTitles, navMenuIcons);

        graphRepo = new GraphRepo(this);
        data = new GraphData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_graph, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_data:
                insertData();
                break;
            case android.R.id.home:
                NavDrawerToggle();

        }
        return true;
    }

    public void setFragment(Fragment frag){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.detail_fragment, frag).commit();
    }

    private void insertData(){

        Random random = new Random();

        GraphRepo repo = new GraphRepo(this);

        GraphData graphData = new GraphData();
        graphData.setDate("2016-Mar-01");
        graphData.setAverage(random.nextInt(100));
        graphData.setHighest(random.nextInt(100));
        graphData.setLowest(random.nextInt(100));

        GraphData secondData = new GraphData();
        secondData.setDate("2016-Mar-02");
        secondData.setAverage(random.nextInt(100));
        secondData.setHighest(random.nextInt(100));
        secondData.setLowest(random.nextInt(100));

        repo.insertData(graphData);
        repo.insertData(secondData);
    }

    @Override
    public void sendGraphData(String date) {
        Log.d(TAG,"Date: " + date);
        data = graphRepo.getData(date);

        if (data != null){
            DetailFragment detailFragment = DetailFragment.newInstance(data);
            setFragment(detailFragment);
            Log.d(TAG,"Retrieve data from database: " + data.getHighest());
        }
    }
}
