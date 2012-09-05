package com.example.coffeearrow;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.coffeearrow.server.ServerInterface;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * This is the activity to upload a image from the phone.
 * TODO: This and UploadImageActivity are almost identical, need to refactor.
 * @author sunshi
 *
 */
public class UploadImageActivity extends Activity {
	private static final String URL = "http://coffeearrow.com/";
	
	private String filePath;
	private String caption;
	private UploadImageActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        
        Intent sourceIntent = getIntent();
		filePath = sourceIntent.getStringExtra("filePath");
        Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
        ImageView imageToUpload = (ImageView)findViewById(R.id.imageToUpload);
        imageToUpload.setImageBitmap(yourSelectedImage);
        
        mainActivity = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_upload_image, menu);
        return true;
    }
    
	public void submit(View view) {
		Log.i("UploadImageActivity", "Submit button clicked.");
		Log.i("UploadImageActivity", "File path is: " + filePath);
		EditText captionEdit = (EditText)findViewById(R.id.caption);
		caption = captionEdit.getText().toString();
		Log.i("UploadImageActivity", "Caption is: " + caption);
		
		SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
		String userId = settings.getString("userId", null);
		Log.i("UploadImageActivity", "User id uploading to is: " + userId);
		
	    try {
	    	String filePostUrl = URL+"uploadUserImage";
	    	Log.i("UploadImageActivity", "Posting to: " + filePostUrl);
	        HttpPost request = new HttpPost(filePostUrl);
			
	        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
	        
            entity.addPart("userId", new StringBody(userId));
            entity.addPart("imgCaption", new StringBody(caption));
            entity.addPart("imageData", new FileBody(new File (filePath)));
            request.setEntity(entity);

	        Log.i("UploadImageActivity", "http post generated successfully.");

		    UploadUserImageTask uploadTask = new UploadUserImageTask();
		    uploadTask.execute(request);
		    
	    } catch (IOException e) {
	    	Log.i("UploadImageActivity", "Failed with exception:"+e);
	        e.printStackTrace();
	    }
	}
	
	public class UploadUserImageTask extends AsyncTask<HttpPost, Integer, Object> {

		// This is the first progress dialog we display while fetching the search result.
		private ProgressDialog dialog;
		
		public UploadUserImageTask() {
			super();
			dialog = new ProgressDialog(mainActivity);
		}

		@Override
		protected Object doInBackground(HttpPost... params) {
			return ServerInterface.executeHttpRequest(params[0]);
		}
		
		protected void onPreExecute() {
			// Display the progress dialog.
			this.dialog.setMessage("Uploading image...");
			this.dialog.show();
		}

		protected void onPostExecute(Object objResult) {
			Log.i("UploadUserImage", "Got back to onPostExecute.");
			Log.i("UploadUserImage", "The result is: " + objResult);
			
			JSONArray resultArray = (JSONArray) objResult;
			String status = null;
			String s3url = null;
			try {
				for (int i = 0; i < resultArray.length(); i++) {
					JSONObject record = resultArray.getJSONObject(i);

					status = record.getString("status");
					
					// This is the s3 url of the file we just uploaded.
					s3url = record.getString("imgLink");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if ("Failed".equals(status)) {
				String message = "Something went wrong";
				Toast.makeText(getApplicationContext(), message,
						Toast.LENGTH_SHORT).show();
			} else {
				// Save information for SelfProfileActivity to display the new image
				// we just uploaded.
				Intent result = new Intent();
				result.putExtra("s3url", s3url);
				result.putExtra("caption", caption);
				setResult(RESULT_OK, result);
				
				// Dismiss the progress dialog.
				if (dialog.isShowing())
					dialog.dismiss();
				
				// Go back to the profile activity
				finish();
			}
			
		}
	}
}
