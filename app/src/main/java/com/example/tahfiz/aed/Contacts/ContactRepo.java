package com.example.tahfiz.aed.Contacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tahfiz.aed.DBHelper;

import java.util.ArrayList;


/**
 * Created by tahfiz on 15/11/2015.
 */
public class ContactRepo {
    private DBHelper dbHelper;
    public ContactRepo(Context context){
        dbHelper = new DBHelper(context);
    }

    public boolean checkContact(String phoneNum){

        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DBHelper.CONTACT_TABLE,
                new String[]{DBHelper.KEY_PHONENUMBER,DBHelper.KEY_NAME},
                DBHelper.KEY_PHONENUMBER + " = ?",
                new String[]{phoneNum},
                null,
                null,
                null,
                null);

        if (cursor != null){
            return false;
        }else {
            return true;
        }
    }


    public void insertContact(ContactData member){

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.KEY_NAME, member.getName());
        values.put(DBHelper.KEY_PHONENUMBER, member.getPhoneNum());
        values.put(DBHelper.KEY_IMAGE, member.getPhoto());

        //Insert row
        long contact_Id = db.insert(DBHelper.CONTACT_TABLE,null,values);
        db.close();
    }

    public void deleteContact(String contact_phoneNum){

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.CONTACT_TABLE,DBHelper.KEY_PHONENUMBER + "= ?",new String[]{ contact_phoneNum});
        db.close();
    }

    public ArrayList<ContactData> getContactList(){

        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT " +
                DBHelper.KEY_ID + "," +
                DBHelper.KEY_NAME + "," +
                DBHelper.KEY_PHONENUMBER + "," +
                DBHelper.KEY_IMAGE +
                " FROM " + DBHelper.CONTACT_TABLE +
                " ORDER BY " + DBHelper.KEY_NAME + " ASC";

        //ContactData contacts = new ContactData()
        ArrayList<ContactData> contactList = new ArrayList<ContactData>();

        Cursor cursor = db.rawQuery(selectQuery,null);

        //Loop through all rows and adding to list
        if (cursor.moveToFirst()){
            do {
                ContactData contact = new ContactData();
                contact.set_ID(cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_ID)));
                contact.setName(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_NAME)));
                contact.setPhoneNum(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PHONENUMBER)));
                contact.setPhoto(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_IMAGE)));
                contactList.add(contact);

            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contactList;
    }
}
