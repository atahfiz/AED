package com.example.tahfiz.aed.Contacts;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tahfiz.aed.R;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by tahfiz on 4/4/2016.
 */
public class ContactAdapter extends ArrayAdapter<ContactData>{

    private final LayoutInflater inflater;

    Context context;
    ContactRepo repo;
    RoundImage roundedImage;

    public ContactAdapter(Context context) {
        super(context, R.layout.contact_row);
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        repo = new ContactRepo(context);
    }

    public void setData(List<ContactData> data){
        clear();

        if (data != null){
            for (ContactData contactEntry : data){
                add(contactEntry);
            }
        }
    }

    static class ViewHolder{
        protected TextView _ID;
        protected TextView name;
        protected TextView phoneNum;
        protected ImageView photo;
        protected Button delete;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        final ViewHolder holder;
        final ContactData contactMember = getItem(position);

        if (row == null){
            row = inflater.inflate(R.layout.contact_row,parent,false);
        }

        holder = new ViewHolder();

        //Init Items
        holder._ID = (TextView) row.findViewById(R.id.contact_id);
        holder.name = (TextView) row.findViewById(R.id.contact_name);
        holder.phoneNum = (TextView) row.findViewById(R.id.contact_phoneNum);
        holder.photo = (ImageView) row.findViewById(R.id.contact_photo);
        holder.delete = (Button) row.findViewById(R.id.delete_contact);

        row.setTag(holder);

        holder._ID.setText(String.valueOf(contactMember.get_ID()));
        holder.name.setText(contactMember.getName());
        holder.phoneNum.setText(contactMember.getPhoneNum());

        if (getByteContactPhoto(contactMember.getPhoto()) == null){
            holder.photo.setImageResource(R.drawable.ic_account_circle);
        }else {
            roundedImage = new RoundImage(getByteContactPhoto(contactMember.getPhoto()));
            holder.photo.setImageDrawable(roundedImage);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonRemoveClicked(getItem(position));
            }
        });

        return row;
    }

    public Bitmap getByteContactPhoto(String photo) {

        Cursor cursor;

        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,Long.parseLong(photo));

        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

        cursor = context.getContentResolver().query(
                photoUri,
                new String[]{ContactsContract.Contacts.Photo.DATA15},
                null,
                null,
                null
        );

        if (cursor == null){
            return null;
        }

        try {
            if (cursor.moveToFirst()){
                byte[] data = cursor.getBlob(0);

                if (data != null){
                    return BitmapFactory.decodeStream(new ByteArrayInputStream(data));
                }
            }

        }finally {
            cursor.close();
        }
        return null;
    }

    private void buttonRemoveClicked(ContactData item) {
        remove(item);
        repo.deleteContact(item.getPhoneNum());
        Toast.makeText(context,"Contact Deleted",Toast.LENGTH_SHORT).show();
    }
}
