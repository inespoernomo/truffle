package com.example.coffeearrow;

import android.app.ListActivity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public abstract class PortraitListActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}