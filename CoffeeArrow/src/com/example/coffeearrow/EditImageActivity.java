package com.example.coffeearrow;

import java.util.HashMap;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.coffeearrow.helpers.ImageLoader;
import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;
import com.example.coffeearrow.server.RequestFactory;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * This is the activity to edit the image caption or delete the image.
 * TODO: This and UploadImageActivity are almost identical, need to refactor.
 * @author sunshi
 *
 */
public class EditImageActivity extends Activity implements PostToServerCallback {

	private String s3url;
	private String caption;
	private String action;
	private String userId;
	private ProgressDialog dialog;
	private EditImageActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        mainActivity = this;
        
        dialog = new ProgressDialog(this);
        
        // Get the info from the source intent.
        Intent sourceIntent = getIntent();
        s3url = sourceIntent.getStringExtra("s3url");
        caption = sourceIntent.getStringExtra("caption");
        Log.i("EditImageActivity", "Got s3url: "+s3url+" . Caption: "+caption);
        
        // Setup the image
        ImageLoader imageLoader = new ImageLoader(this);
        ImageView imageToEdit = (ImageView)findViewById(R.id.imageToEdit);
        imageLoader.DisplayImage(s3url, imageToEdit);
        
        // Setup the caption
        EditText captionEdit = (EditText)findViewById(R.id.caption);
        captionEdit.setText(caption);
        
        // Save the userId
		SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
		userId = settings.getString("userId", null);
		Log.i("EditImageActivity", "User id deleting the photo is: " + userId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_image, menu);
        return true;
    }

    public void deleteImage(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to delete this picture?")
        .setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                action = "delete";

                HashMap<String, String> requestParams = new HashMap<String, String>();
                requestParams.put("userId", userId);
                requestParams.put("imgLink", s3url);
                HttpPost request = RequestFactory.create(requestParams, "deleteUserImage");
                
                // Display the progress dialog.
                mainActivity.dialog.setMessage("Deleting image...");
                mainActivity.dialog.show();
                PostToServerAsyncTask task = new PostToServerAsyncTask(mainActivity);
                task.execute(request);
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                 //
            }
        });
        
        AlertDialog alert = builder.create();
        alert.show();
    }
    
    public void updateCaption(View view) {
    	action = "edit";
		
		EditText captionEdit = (EditText)findViewById(R.id.caption);
		caption = captionEdit.getText().toString();
		Log.i("UploadImageActivity", "Caption is: " + caption);
		
		HashMap<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("userId", userId);
		requestParams.put("imgLink", s3url);
		requestParams.put("imgCaption", caption);
		HttpPost request = RequestFactory.create(requestParams, "editImageCaption");
		
		// Display the progress dialog.
		this.dialog.setMessage("Updating caption...");
		this.dialog.show();
		PostToServerAsyncTask task = new PostToServerAsyncTask(this);
		task.execute(request);
    }
    
    public void setProfileImage(View view){
        action = "setProfile";
        HashMap<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("userId", userId);
        requestParams.put("profileImage", s3url);
        HttpPost request = RequestFactory.create(requestParams, "saveProfileImage");
        
        // Display the progress dialog.
        this.dialog.setMessage("Updating profile image...");
        this.dialog.show();
        PostToServerAsyncTask task = new PostToServerAsyncTask(this);
        task.execute(request);
        
    }
    
	public void callback(Object objResult) {
		Log.i("UploadUserImage", "Got back to onPostExecute.");
		Log.i("UploadUserImage", "The result is: " + objResult);
		
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
		} else {
			// Save information for SelfProfileActivity to remove/update the image
			Intent result = new Intent();
			result.putExtra("s3url", s3url);
			result.putExtra("caption", caption);
			result.putExtra("type", action);
			setResult(RESULT_OK, result);
			
			// Dismiss the progress dialog.
			if (dialog.isShowing())
				dialog.dismiss();
			
			// Go back to the profile activity
			finish();
		}
	}
  
}
