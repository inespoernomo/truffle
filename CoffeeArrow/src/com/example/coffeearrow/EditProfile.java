package com.example.coffeearrow;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class EditProfile extends PortraitActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_profile, menu);
        return true;
    }
}
