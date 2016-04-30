package com.example.tahfiz.aed.Contacts;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.tahfiz.aed.R;

/**
 * Created by tahfiz on 4/4/2016.
 */
public class ContactPickFragment extends Fragment {

    private static RelativeLayout addFunction;
    private static final int RESULT_PICK_CONTACT = 85500;

    ContactPickListener actvContact;

    public interface ContactPickListener{
        public void sendContactData(Intent data);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            actvContact = (ContactPickListener) activity;

        }catch (ClassCastException e){
            throw  new ClassCastException(activity.toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_pick_fragment,container,false);

        addFunction = (RelativeLayout) view.findViewById(R.id.addFunction);

        addFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked(v);
            }
        });
        return view;
    }

    public void buttonClicked(View v){
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK);
        contactPickerIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        startActivityForResult(contactPickerIntent,RESULT_PICK_CONTACT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case RESULT_PICK_CONTACT:
                    actvContact.sendContactData(data);
                    break;
            }
        }else {
            Log.e("Contact Pick Fragement", "Failed to pick contact");
        }
    }
}
