package com.example.coffeearrow;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.view.Menu;
import android.view.View;

public class ChangeDateActivity extends Activity {

	Dialog changeDateDialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeDateDialog = new Dialog(this);
    	changeDateDialog.setContentView(R.layout.date_time_picker);
    	changeDateDialog.setTitle("Change date and time");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_change_date, menu);
        return true;
    }
    
    public void closeDialog(View view) {
    	 
    	changeDateDialog.dismiss();
    	
    }
}
