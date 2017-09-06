package com.example.deepn.flicker;

import android.app.Activity;
import android.os.Bundle;

public class DetailSettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingFragment settingFragment = new SettingFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, settingFragment).commit();
    }


}
