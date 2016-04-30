package com.example.tahfiz.aed.Contacts;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.example.tahfiz.aed.Drawer.BaseActivity;
import com.example.tahfiz.aed.R;

public class ContactActivity extends BaseActivity implements ContactPickFragment.ContactPickListener {

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ContactData contact;
    private ContactListFragment contactListFragment;
    private ContactRepo repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items); // load
        // titles
        // from
        // strings.xml

        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);// load icons from
        // strings.xml

        set(navMenuTitles, navMenuIcons);

        contactListFragment = new ContactListFragment();
        repo = new ContactRepo(this);

        setFragment(contactListFragment);
    }

    @Override
    public void sendContactData(Intent data) {
        Cursor cursor = null;
        Cursor phoneCursor = null;
        contact = new ContactData();

        try {
            String name = null;
            String phoneNum = null;
            String photo = null;

            Uri uri = data.getData();

            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();

            String phoneID = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

            String[] phoneProj = new String[]{
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
            };

            phoneCursor = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    phoneProj,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[]{phoneID},
                    null
            );

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            if (phoneCursor.moveToFirst()){
                phoneNum = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                photo = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            }
            cursor.close();
            phoneCursor.close();

            contact.setName(name);
            contact.setPhoneNum(phoneNum);
            contact.setPhoto(photo);

            //Check wheter Contact Data exists or nor
            if (contact != null && (!repo.checkContact(contact.getPhoneNum()))){
                repo.insertContact(contact);
                System.out.println("Contact has been insert");
            }else {
                Toast.makeText(this, "Contact already exist", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setFragment(Fragment fragment) {
        FragmentManager manager = getFragmentManager();

        if (manager.findFragmentById(R.id.listContainer) == null){
            manager.beginTransaction().add(R.id.listContainer,fragment).commit();
        }
    }
}