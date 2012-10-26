package com.example.coffeearrow;

import java.util.HashMap;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;
import com.example.coffeearrow.server.RequestFactory;
//import android.support.v4.app.NavUtils;

public class SignIn extends PortraitActivity implements PostToServerCallback {

	private ProgressDialog dialog;

	public final static String EMAIL = "com.coffeearrow.signIn.Email";
	public final static String PASSWORD = "com.coffeearrow.signIn.Password";
	public String kEmail = null;
	private SharedPreferences settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = getSharedPreferences("MyPrefsFile", 0);
		String userId = settings.getString("userId", null);
		// User already logged in.
		
		if (userId != null){
			goToDisplayActivity(userId);
		    return;
		} 
		
		setContentView(R.layout.activity_sign_in);
		dialog = new ProgressDialog(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_sign_in, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/** Called when the user presses signIn button */
	public void signIn(View view) {

		EditText emailText = (EditText) findViewById(R.id.email);
		String email = emailText.getText().toString();
		kEmail = email;
		EditText passwordText = (EditText) findViewById(R.id.password);
		String password = passwordText.getText().toString();

		HashMap<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("email", email);
		requestParams.put("password", password);
		HttpPost request = RequestFactory.create(requestParams, "signInNative");

		// Show progress dialog.
		dialog.setMessage("Signing in...");
		dialog.show();
		PostToServerAsyncTask task = new PostToServerAsyncTask(this);
		task.execute(request);
	}

	/** Called when the user presses signUp button */
	public void signUp(View view) {
		Intent intent = new Intent(this, SignUpActivity.class);
		startActivity(intent);
	}
	
	public void sendForgetPasswordLink(View view) {
		Intent intent = new Intent(this, SendForgetPasswordLinkActivity.class);
		startActivity(intent);
	}

	@Override
	public void callback(JSONObject result) {
		// Dismiss the progress dialog.
		if (dialog.isShowing())
			dialog.dismiss();

		String userId = null;
		try {
			userId = result.getString("userId");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if ("Failed".equals(userId)) {
			String message = "Invalid Username/Password";
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
					.show();
		} else if ("NotVerified".equals(userId)) {
			String message = "Account not verfied, check your work email for verification link";
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
					.show();
			Intent intent = new Intent(this, PendingVerificationActivity.class);
			intent.putExtra(EMAIL, kEmail);
			startActivity(intent);
		} else {
			goToDisplayActivity(userId);
		}
	}

	/**
	 * Save userId in the shared preference and then go to the next activity.
	 * @param userId The user's id.
	 */
    private void goToDisplayActivity(String userId) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userId", userId);
        editor.commit();

        Intent intent = new Intent(this, DisplaySearchResultsActivity.class);
        startActivity(intent);
        
        // Do not come back to the login page when user press back button
        finish();
    }
}
