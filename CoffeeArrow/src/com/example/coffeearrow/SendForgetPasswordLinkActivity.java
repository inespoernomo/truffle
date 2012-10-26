package com.example.coffeearrow;

import java.util.HashMap;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;
import com.example.coffeearrow.server.RequestFactory;

public class SendForgetPasswordLinkActivity extends PortraitActivity implements PostToServerCallback {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_forget_password_link);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_send_forget_password_link, menu);
        return true;
    }
    
    public void sendPasswordResetLink(View view) {
    	EditText emailText = (EditText) findViewById(R.id.email);
    	String email = emailText.getText().toString();
    	
    	HashMap<String, String> requestParams = new HashMap<String, String>();
	    requestParams.put("email", email);
		
	    HttpPost request = RequestFactory.create(requestParams, "sendResetPasswordLink");
		PostToServerAsyncTask task = new PostToServerAsyncTask(this);
		task.execute(request);
    }

	@Override
	public void callback(JSONObject objResult) {
		String status = null;
		try {
			status = objResult.getString("status");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if ("EmailNotFound".equals(status)) {
			String message = "Email not found, please sign up or try again";
			Toast.makeText(getApplicationContext(), message,
					Toast.LENGTH_LONG).show();
		} else {
			String message = "Please check your email for resetting the password";
			Toast.makeText(getApplicationContext(), message,
					Toast.LENGTH_LONG).show();
			Intent intent = new Intent(this, SignIn.class);
			startActivity(intent);
		} 
	}
}
