package com.example.coffeearrow;

import java.util.HashMap;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;
import com.example.coffeearrow.server.RequestFactory;

public class SignUpActivity extends Activity implements PostToServerCallback {

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
		// Not a all mighty regular expression for email, but not worth it.
		if (!email.matches(".+@.+\\..+")) {
		    String message = "Please enter a valid email address.";
            Toast.makeText(getApplicationContext(), message,
                    Toast.LENGTH_LONG).show();
            return;
		}
		
		EditText passwordText = (EditText) findViewById(R.id.password);
		String password = passwordText.getText().toString();
		if (password.isEmpty()) {
		    String message = "Please enter a password.";
            Toast.makeText(getApplicationContext(), message,
                    Toast.LENGTH_LONG).show();
            return;
		}
		
		EditText confirmPasswordText = (EditText) findViewById(R.id.confirmPassword);
        String confirmPassword = confirmPasswordText.getText().toString();
        if (! password.equals(confirmPassword)) {
            String message = "Password and confirm password do not match.";
            Toast.makeText(getApplicationContext(), message,
                    Toast.LENGTH_LONG).show();
            return;
        }
		
		EditText nameText = (EditText) findViewById(R.id.name);
		String name = nameText.getText().toString();
		if (name.isEmpty()) {
            String message = "Please enter a name.";
            Toast.makeText(getApplicationContext(), message,
                    Toast.LENGTH_LONG).show();
            return;
        }
		
		EditText zipCodeText = (EditText) findViewById(R.id.zipCode);
		String zipCode = zipCodeText.getText().toString();
		
		RadioGroup radioGenderGroup = (RadioGroup) findViewById(R.id.radioGender);
		int selectedGender = radioGenderGroup.getCheckedRadioButtonId();
		RadioButton checkedGender = (RadioButton) findViewById(selectedGender);

		HashMap<String, String> requestParams = new HashMap<String, String>();
	    requestParams.put("firstName", name);
	    requestParams.put("email", email);
	    requestParams.put("password", password);
	    requestParams.put("zipCode", zipCode);
	    requestParams.put("gender", checkedGender.getText().toString());

		HttpPost request = RequestFactory.create(requestParams, "submitUserInfoNative");

		PostToServerAsyncTask task = new PostToServerAsyncTask(this);
		task.execute(request);
	}
	
	public void callback(Object objResult) {
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
			Intent intent = new Intent(this, PendingVerificationActivity.class);
			startActivity(intent);
		}
	}
}
