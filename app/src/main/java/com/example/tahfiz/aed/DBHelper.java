package com.example.tahfiz.aed;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tahfiz on 4/4/2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    //Version number to upgrade database version
    // each time CRUD
    private static final int DATABASE_VERSION = 1;

    // Label for Contact Database
    public static final String CONTACT_TABLE = "Contacts";
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONENUMBER = "phonenum";
    public static final String KEY_IMAGE = "image";

    //Label for Nearby Database
    public static final String NEARBY_TABLE = "Nearby";
    public static final String KEY_PLACEID = "_id";
    public static final String KEY_PLACENAME = "name";
    public static final String KEY_DISTANCE= "distance";
    public static final String KEY_DURATION = "time";

    //Label for Graph Database
    public static final String GRAPH_TABLE = "Graph";
    public static final String KEY_DATAID = "_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_AVERAGE = "average";
    public static final String KEY_HIGHEST= "highest";
    public static final String KEY_LOWEST = "lowest";

    //Database Name
    private static final String DATABASE_NAME = "AidApp.db";

    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Contacts Table
        String CREATE_TABLE_CONTACT = "CREATE TABLE " + CONTACT_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_PHONENUMBER + " TEXT,"
                + KEY_IMAGE + " TEXT )";

        // Create Nearby Table
        String CREATE_TABLE_NEARBY = "CREATE TABLE " + NEARBY_TABLE + "("
                + KEY_PLACEID + " INTEGER PRIMARY KEY,"
                + KEY_PLACENAME + " TEXT,"
                + KEY_DURATION + " DOUBLE,"
                + KEY_DISTANCE + " TEXT )";

        // Create Graph Table
        String CREATE_TABLE_GRAPH = "CREATE TABLE " + GRAPH_TABLE + "("
                + KEY_DATAID + " INTEGER PRIMARY KEY,"
                + KEY_DATE + " TEXT,"
                + KEY_AVERAGE + " INTEGER,"
                + KEY_HIGHEST + " INTEGER,"
                + KEY_LOWEST + " INTEGER )";

        db.execSQL(CREATE_TABLE_CONTACT);
        db.execSQL(CREATE_TABLE_NEARBY);
        db.execSQL(CREATE_TABLE_GRAPH);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + CONTACT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + NEARBY_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + GRAPH_TABLE);

        //Create table again
        onCreate(db);
    }
}
