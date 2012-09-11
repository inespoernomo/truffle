package com.example.coffeearrow;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class ChangeDateActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    	setContentView(R.layout.activity_change_date);
    	setTitle("Change date and time");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_change_date, menu);
        return true;
    }
    
    public void closeDialog(View view) {
    	 
    	Log.i("ChangeDateActivity", "Dismiss");
    	
    }
}
