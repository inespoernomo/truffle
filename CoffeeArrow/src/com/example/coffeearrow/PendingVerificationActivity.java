package com.example.coffeearrow;

import java.util.HashMap;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;
import com.example.coffeearrow.server.RequestFactory;

public class PendingVerificationActivity extends Activity implements PostToServerCallback{

	private String newUserEmail = null;	
	private ProgressDialog dialog;
	public final static String EMAIL = "com.coffeearrow.signIn.Email";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Intent intent = getIntent();
    	newUserEmail = intent.getStringExtra(EMAIL);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_verification);
        dialog = new ProgressDialog(this);     
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_pending_verification, menu);
        return true;
    }
    
    public void sendVerificationLinkAgain(View view) {
    	dialog.setMessage("Sending email...");
    	dialog.show();
    	HashMap<String, String> requestParams = new HashMap<String, String>();
	    requestParams.put("email", newUserEmail);		
	    HttpPost request = RequestFactory.create(requestParams, "sendVerificationEmailAgain");
		PostToServerAsyncTask task = new PostToServerAsyncTask(this);
		task.execute(request);
    }
    
    public void signIn(View view) {
    	//Intent intent = new Intent(this, SignIn.class);
		//startActivity(intent);
		finish();
    }
    
	@Override
	public void callback(Object objResult) {
		if (dialog.isShowing())
			dialog.dismiss();
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
		if ("EmailNotFound".equals(status)) {
			String message = "Ah something went wrong, we are looking into it";
			Toast.makeText(getApplicationContext(), message,
					Toast.LENGTH_LONG).show();
		} else {
			String message = "Email sent, please check your inbox again";
			Toast.makeText(getApplicationContext(), message,
					Toast.LENGTH_LONG).show();
			
		} 
	}
}
