package com.example.coffeearrow;

import java.util.HashMap;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.coffeearrow.R;
import com.example.coffeearrow.server.IntentFactory;
import com.example.coffeearrow.server.RequestFactory;
import com.example.coffeearrow.server.ServerInterface;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.graphics.Color;
import android.graphics.PorterDuff;

public class SignIn extends Activity {

	public final static String EMAIL = "com.coffeearrow.signIn.Email";
	public final static String PASSWORD = "com.coffeearrow.signIn.Password";
	private static final String URL = "http://coffeearrow.com/";
	private SignIn mainActivity = null;
	
	
	private class AuthenticateUser extends AsyncTask<HttpPost, Integer, Object> {
		
		private Intent intent;
		public AuthenticateUser(Intent intent) {
			super();
			this.intent = intent;
		}

		@Override
		protected Object doInBackground(HttpPost... params) {

			return ServerInterface.executeHttpRequest(params[0]);
		}

		protected void onPostExecute(Object objResult) {
			JSONArray resultArray = (JSONArray)objResult;
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
			} else {
				intent.putExtra("userId", userId);
				startActivity(intent);
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mainActivity = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		Button signInButton = (Button) findViewById(R.id.signInButton);
		//Button signUpButton = (Button) findViewById(R.id.signUpButton);
		signInButton.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
		//getActionBar().setDisplayHomeAsUpEnabled(true);
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
			NavUtils.navigateUpFromSameTask(this);
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
		
		HttpPost request = RequestFactory.create(URL, requestParams, "signInNative");	
		Intent intent = IntentFactory.create(this, DisplaySearchResultsActivity.class, requestParams);
		
		AuthenticateUser authenticateUser = new AuthenticateUser(intent);
		authenticateUser.execute(request);
	}

	/** Called when the user presses signUp button */
	public void signUp(View view) {
		Intent intent = new Intent(mainActivity, SignUpActivity.class);
		startActivity(intent);
	}

}
