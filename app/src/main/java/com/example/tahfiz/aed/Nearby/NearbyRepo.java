package com.example.tahfiz.aed.Nearby;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tahfiz.aed.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tahfiz on 5/4/2016.
 */
public class NearbyRepo {

    private DBHelper dbHelper;

    public NearbyRepo(Context context){
        dbHelper = new DBHelper(context);
    }

    public void insertPlaces(List<PlaceData> data){

        if (checkExist()){
            deleteAll();
        }

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        for (PlaceData placeData : data){
            values.put(DBHelper.KEY_PLACENAME,placeData.getPlaceName());
            values.put(DBHelper.KEY_DISTANCE,placeData.getDistance());
            values.put(DBHelper.KEY_DURATION,placeData.getDuration());
            //values.put(DBHelper.KEY_LATITUDE,placeData.getLatitude());
            //values.put(DBHelper.KEY_LONGITUDE,placeData.getLongitude());

            db.insert(DBHelper.NEARBY_TABLE,null,values);
        }
        db.close();
    }

    private boolean checkExist() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + DBHelper.NEARBY_TABLE + " LIMIT 1";

        Cursor cursor = db.rawQuery(selectQuery,null);

        if (cursor.getCount() > 0){
            return true;
        }else {
            return false;
        }
    }

    //Delete all existing data
    private void deleteAll() {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(DBHelper.NEARBY_TABLE,null,null);
        //db.execSQL("DELETE FROM " + DBHelper.NEARBY_TABLE);
        db.close();
    }

    public PlaceData getNearestPlace(){

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT " +
                DBHelper.KEY_PLACENAME + "," +
                "MIN(" + DBHelper.KEY_DISTANCE + ")" +
                " FROM " +DBHelper.NEARBY_TABLE +
                " ASC LIMIT 1";

        Cursor cursor = db.rawQuery(selectQuery,null);

        if (cursor != null){
            if (cursor.moveToFirst()){
                PlaceData place = new PlaceData();
                place.setPlaceName(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PLACENAME)));
                place.setDistance(cursor.getString(cursor.getColumnIndex("MIN(" + DBHelper.KEY_DISTANCE + ")")));
                return place;
            }
        }
        return null;
    }

    public int getNearbyCount(){
        String countQuery = "SELECT * FROM " + DBHelper.NEARBY_TABLE;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery,null);

        return cursor.getCount();
    }

    //Retrieve all Nearby Places
    public ArrayList<PlaceData> getPlaceList(){

        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT " +
                DBHelper.KEY_PLACEID + "," +
                DBHelper.KEY_PLACENAME + "," +
                DBHelper.KEY_DISTANCE + "," +
                DBHelper.KEY_DURATION +
               // DBHelper.KEY_LATITUDE +
               // DBHelper.KEY_LONGITUDE +
                " FROM " + DBHelper.NEARBY_TABLE +
                " ORDER BY " + DBHelper.KEY_DISTANCE;

        //ContactData contacts = new ContactData()
        ArrayList<PlaceData> placeList = new ArrayList<PlaceData>();

        Cursor cursor = db.rawQuery(selectQuery,null);

        //Loop through all rows and adding to list
        if (cursor.moveToFirst()){
            do {
                PlaceData place = new PlaceData();
                place.setPlaceID(cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_PLACEID)));
                place.setPlaceName(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PLACENAME)));
                place.setDistance(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_DISTANCE)));
                place.setDuration(cursor.getDouble(cursor.getColumnIndex(DBHelper.KEY_DURATION)));
                //place.setLatitude(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_LATITUDE)));
                //place.setLongitude(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_LONGITUDE)));
                placeList.add(place);

            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return placeList;
    }
}
