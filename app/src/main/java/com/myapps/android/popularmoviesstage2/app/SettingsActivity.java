/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.popularmovies.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {


    //A handle to the Preference that resets all values to their defaults.
    private Preference resetDialogPreference;
    //An intent object, that holds the intent that started this Activity.
    private Intent startIntent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_data_source_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_order_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_vote_count_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_period_key)));
        bindMultSelectListPreference(findPreference(getString(R.string.pref_genre_ids_key)));
        bindPreferenceResetToValue(findPreference("resetDialog"));

    }



    private void bindMultSelectListPreference(Preference preference){
        MultiSelectListPreference mslPref = (MultiSelectListPreference) preference;

        if (mslPref != null) {
            mslPref.setSummary(getSortedGenreList(mslPref));
            mslPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    MultiSelectListPreference mpreference = (MultiSelectListPreference) preference;
                    mpreference.setValues((Set<String>) newValue);
                    mpreference.setSummary(getSortedGenreList(mpreference));
                    return true;
                }
            });
        }
    }

    @NonNull
    private String getSortedGenreList(MultiSelectListPreference mslPref) {
        List<String> genres = new ArrayList<String>();
        for(String value : (Set<String>) mslPref.getValues() )
            genres.add(mslPref.getEntries()[mslPref.findIndexOfValue(value)].toString());
        Collections.sort(genres);
        return genres.toString();
    }


    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);

        onPreferenceChange(preference, PreferenceManager
                                            .getDefaultSharedPreferences(preference.getContext())
                                            .getString(preference.getKey(), ""));

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            preference.setSummary(stringValue);
        }
        return true;
    }


    private void bindPreferenceResetToValue(Preference preference) {
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        this.resetDialogPreference = getPreferenceScreen().findPreference("resetDialog");
        this.startIntent = getIntent();
        this.resetDialogPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                overridePendingTransition(0, 0);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                startActivity(startIntent);
                return false;
            }
        });
    }

}