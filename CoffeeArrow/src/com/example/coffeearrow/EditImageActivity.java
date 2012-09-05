package com.example.coffeearrow;

import java.util.HashMap;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.coffeearrow.helpers.ImageLoader;
import com.example.coffeearrow.server.RequestFactory;
import com.example.coffeearrow.server.ServerInterface;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
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
public class EditImageActivity extends Activity {
	
	private static final String URL = "http://coffeearrow.com/";	
	
	private String s3url;
	private String caption;
	private EditImageActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        
        mainActivity = this;
        
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_image, menu);
        return true;
    }

    public void deleteImage(View view) {
		SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
		String userId = settings.getString("userId", null);
		Log.i("EditImageActivity", "User id deleting the photo is: " + userId);
		
		HashMap<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("userId", userId);
		requestParams.put("imgLink", s3url);
		
		HttpPost request = RequestFactory.create(URL, requestParams, "deleteUserImage");
		
		DeleteImageTask task = new DeleteImageTask();
		task.execute(request);
    }
    

    private class DeleteImageTask extends AsyncTask<HttpPost, Integer, Object> {
    	
    	// This is the first progress dialog we display while fetching the search result.
		private ProgressDialog dialog;
		
		public DeleteImageTask() {
			super();
			dialog = new ProgressDialog(mainActivity);
		}
		
		protected void onPreExecute() {
			// Display the progress dialog.
			this.dialog.setMessage("Deleting image...");
			this.dialog.show();
		}

		@Override
		protected Object doInBackground(HttpPost... params) {

			return ServerInterface.executeHttpRequest(params[0]);
		}

		protected void onPostExecute(Object objResult) {
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
				// Save information for SelfProfileActivity to remove the image
				// we just uploaded.
				Intent result = new Intent();
				result.putExtra("s3url", s3url);
				result.putExtra("type", "delete");
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
