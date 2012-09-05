package com.example.coffeearrow;

import java.util.HashMap;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.coffeearrow.R;
import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;
import com.example.coffeearrow.server.RequestFactory;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//import android.support.v4.app.NavUtils;
import android.graphics.Color;
import android.graphics.PorterDuff;

public class SignIn extends Activity implements PostToServerCallback {
	
	private ProgressDialog dialog;

	public final static String EMAIL = "com.coffeearrow.signIn.Email";
	public final static String PASSWORD = "com.coffeearrow.signIn.Password";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		Button signInButton = (Button) findViewById(R.id.signInButton);
		signInButton.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
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

	public void callback(Object result){
		// Dismiss the progress dialog.
		if (dialog.isShowing())
			dialog.dismiss();
		
		JSONArray resultArray = (JSONArray)result;
		String userId = null;
		try {
			for(int i = 0; i<resultArray.length(); i++) {
				JSONObject record = resultArray.getJSONObject(i);

				userId = record.getString("userId");
			} 
		}catch (JSONException e) {
			e.printStackTrace();
		}
		if ("Failed".equals(userId)) {
			String message = "Invalid Username/Password";
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
		} else if("NotVerified".equals(userId))  {
			String message = "Account not verfied, check your work email for verification link";
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
		} else {
			SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
			SharedPreferences.Editor editor = settings.edit();			
			editor.putString("userId", userId);
			editor.commit();
			
			Intent intent = new Intent(this, DisplaySearchResultsActivity.class);
			startActivity(intent);
		}
	}
}
