package com.example.coffeearrow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class EditProfile extends PortraitActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        
        Intent sourceIntent = getIntent();
        Log.i("EditProfile", String.format("Got the following from source Intent: %s %s %s %s",
                sourceIntent.getStringExtra("userId"),
                sourceIntent.getStringExtra("name"),
                sourceIntent.getStringExtra("gender"),
                sourceIntent.getStringExtra("zip")
                ));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_profile, menu);
        return true;
    }
}
