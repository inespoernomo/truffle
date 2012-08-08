package com.example.coffeearrow;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class PendingVerificationActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_verification);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_pending_verification, menu);
        return true;
    }
}
