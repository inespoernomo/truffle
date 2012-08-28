package com.example.coffeearrow;

import android.view.Menu;

public class SelfProfileActivity extends ShowUserProfileActivity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_self_profile, menu);
		return true;
	}
}
