package com.example.deepn.flicker;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by deepn on 9/7/2017.
 */

public class SettingFragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.detail_preference);
    }
}
