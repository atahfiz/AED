package com.example.tahfiz.aed.Settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.AppBarLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import com.example.tahfiz.aed.Drawer.BaseActivity;
import com.example.tahfiz.aed.R;

public class SettingsActivity extends BaseActivity {

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    public static void startThisActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public static void startThisActivityForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);

        AppBarLayout bar;

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.app_bar,root,false);
        root.addView(bar,0);

        Toolbar toolbar = (Toolbar) bar.getChildAt(0);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDrawerToggle();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items); // load
        // titles
        // from
        // strings.xml

        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);// load icons from
        // strings.xml

        set(navMenuTitles, navMenuIcons);

        //addPreferencesFromResource(R.xml.preferences);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return false;
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        private AppSettings settings;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            Preference usernamePrefs = findPreference("username");
            usernamePrefs.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(String.valueOf(newValue));
                    return true;
                }
            });

            Preference categoryAgePrefs = findPreference("category_age");
            categoryAgePrefs.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(String.valueOf(newValue));
                    return true;
                }
            });

            Preference categorySexPrefs = findPreference("category_sex");
            categorySexPrefs.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(String.valueOf(newValue));
                    return true;
                }
            });
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            settings = AppSettings.getSettings(getActivity());

            Preference usernamePrefs = findPreference("username");
            usernamePrefs.setSummary(settings.getUsername());

            Preference categoryAgePrefs = findPreference("category_age");
            categoryAgePrefs.setSummary(settings.getCategoryAge());

            Preference categorySexPrefs = findPreference("category_sex");
            categorySexPrefs.setSummary(settings.getCategorySex());
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(sharedPrefsChangeListener);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(sharedPrefsChangeListener);
        }

        private final SharedPreferences.OnSharedPreferenceChangeListener sharedPrefsChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                settings.load();
            }
        };
    }
}

