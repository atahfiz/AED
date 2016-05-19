package com.example.tahfiz.aed.Graph;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tahfiz.aed.DBHelper;

import java.util.ArrayList;

/**
 * Created by tahfiz on 18/5/2016.
 */
public class GraphRepo {

    private DBHelper dbHelper;

    public GraphRepo(Context context){
        dbHelper = new DBHelper(context);
    }

    public void insertData(GraphData data){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.KEY_DATE,data.getDate());
        values.put(DBHelper.KEY_AVERAGE,data.getAverage());
        values.put(DBHelper.KEY_HIGHEST,data.getHighest());
        values.put(DBHelper.KEY_LOWEST,data.getLowest());

        db.insert(DBHelper.GRAPH_TABLE,null,values);
        db.close();
    }

    public int getDataCount(){
        String countQuery = "SELECT * FROM " + DBHelper.GRAPH_TABLE;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery,null);

        return cursor.getCount();
    }

    public ArrayList<GraphData> getDataList(){

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT " +
                DBHelper.KEY_DATAID + "," +
                DBHelper.KEY_DATE + "," +
                DBHelper.KEY_AVERAGE + "," +
                DBHelper.KEY_HIGHEST + "," +
                DBHelper.KEY_LOWEST +
                " FROM " + DBHelper.GRAPH_TABLE;

        ArrayList<GraphData> graphData = new ArrayList<GraphData>();

        Cursor cursor = db.rawQuery(selectQuery,null);

        if (cursor.moveToFirst()){
            do{
                GraphData data = new GraphData();
                data.set_ID(cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_DATAID)));
                //data.setDate(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_DATE)));
                data.setAverage(cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_AVERAGE)));
                data.setHighest(cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_HIGHEST)));
                data.setLowest(cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_LOWEST)));
                graphData.add(data);

            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return graphData;
    }

    public GraphData getData(String date){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DBHelper.GRAPH_TABLE,
                new String[]{
                        DBHelper.KEY_DATE,
                        DBHelper.KEY_AVERAGE,
                        DBHelper.KEY_HIGHEST,
                        DBHelper.KEY_LOWEST},
                DBHelper.KEY_DATE + "=?",
                new String[]{date},
                null,
                null,
                null,
                null);

        if (cursor != null){
            if (cursor.moveToFirst()){
                GraphData data = new GraphData();
                data.setDate(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_DATE)));
                data.setAverage(cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_AVERAGE)));
                data.setHighest(cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_HIGHEST)));
                data.setLowest(cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_LOWEST)));
                return data;
            }
        }
        return null;
    }
}
