package com.example.coffeearrow;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class NotificationsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_notifications, menu);
        return true;
    }
}
