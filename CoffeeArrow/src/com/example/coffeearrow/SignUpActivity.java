package com.example.coffeearrow;

import java.util.HashMap;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.coffeearrow.server.IntentFactory;
import com.example.coffeearrow.server.RequestFactory;
import com.example.coffeearrow.server.ServerInterface;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SignUpActivity extends Activity {

	private static final String URL = "http://coffeearrow.com/";

	public class SubmitUserInfo extends AsyncTask<HttpPost, Integer, Object> {

		private Intent intent;

		public SubmitUserInfo(Intent intent) {
			super();
			this.intent = intent;
		}

		@Override
		protected Object doInBackground(HttpPost... params) {

			return ServerInterface.executeHttpRequest(params[0]);
		}

		protected void onPostExecute(Object objResult) {
			JSONArray resultArray = (JSONArray) objResult;
			String status = null;
			try {
				for (int i = 0; i < resultArray.length(); i++) {
					JSONObject record = resultArray.getJSONObject(i);

					status = record.getString("status");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if ("Failed".equals(status)) {
				String message = "Something went wrong";
				Toast.makeText(getApplicationContext(), message,
						Toast.LENGTH_SHORT).show();
			} else if ("AlreadyPresent".equals(status)) {
				String message = "Email already present, please go to sign in page";
				Toast.makeText(getApplicationContext(), message,
						Toast.LENGTH_SHORT).show();
			} else {
				startActivity(intent);
			}
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_sign_up, menu);
		return true;
	}

	public void submit(View view) {
		EditText emailText = (EditText) findViewById(R.id.email);
		String email = emailText.getText().toString();
		EditText passwordText = (EditText) findViewById(R.id.password);
		String password = passwordText.getText().toString();
		EditText nameText = (EditText) findViewById(R.id.name);
		String name = nameText.getText().toString();
		EditText zipCodeText = (EditText) findViewById(R.id.zipCode);
		String zipCode = zipCodeText.getText().toString();
		RadioGroup radioGenderGroup = (RadioGroup) findViewById(R.id.radioGender);
		RadioGroup radioLookingForGroup = (RadioGroup) findViewById(R.id.radioLookingFor);
		
		int selectedGender = radioGenderGroup.getCheckedRadioButtonId();
		RadioButton checkedGender = (RadioButton) findViewById(selectedGender);

		int selectedLookingFor = radioLookingForGroup.getCheckedRadioButtonId();
		RadioButton checkedLookingFor = (RadioButton) findViewById(selectedLookingFor);
		
		HashMap<String, String> requestParams = new HashMap<String, String>();
	    requestParams.put("firstName", name);
	    requestParams.put("email", email);
	    requestParams.put("password", password);
	    requestParams.put("zipCode", zipCode);
	    requestParams.put("gender", checkedGender.getText().toString());
	    requestParams.put("looking", checkedLookingFor.getText().toString());

		HttpPost request = RequestFactory
				.create(URL, requestParams, "submitUserInfoNative");
		Intent intent = IntentFactory.create(this,
				PendingVerificationActivity.class, requestParams);

		SubmitUserInfo authenticateUser = new SubmitUserInfo(intent);
		authenticateUser.execute(request);

	}
}
