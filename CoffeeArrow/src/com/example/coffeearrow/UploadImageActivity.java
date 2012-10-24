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

import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;
import com.example.coffeearrow.server.RequestFactory;

import android.os.Bundle;
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
public class UploadImageActivity extends PortraitActivity implements PostToServerCallback {

	private String filePath;
	private String caption;
	private ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        
        Intent sourceIntent = getIntent();
		filePath = sourceIntent.getStringExtra("filePath");
		
		// Load smaller image to prevent run out of memory.
		//decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);
        
        //Find the correct scale value. It should be the power of 2.
        final int REQUIRED_SIZE=200;
        int width_tmp=o.outWidth, height_tmp=o.outHeight;
        int scale=1;
        while(true){
            if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                break;
            width_tmp/=2;
            height_tmp/=2;
            scale*=2;
        }
        
        //decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize=scale;
        Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath, o2);
		
        ImageView imageToUpload = (ImageView)findViewById(R.id.imageToUpload);
        imageToUpload.setImageBitmap(yourSelectedImage);
        
        dialog = new ProgressDialog(this);
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
	    	String filePostUrl = RequestFactory.URL+"uploadUserImage";
	    	Log.i("UploadImageActivity", "Posting to: " + filePostUrl);
	        HttpPost request = new HttpPost(filePostUrl);
			
	        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
	        
            entity.addPart("userId", new StringBody(userId));
            entity.addPart("imgCaption", new StringBody(caption));
            entity.addPart("imageData", new FileBody(new File (filePath)));
            request.setEntity(entity);

	        Log.i("UploadImageActivity", "http post generated successfully.");

			// Display the progress dialog.
			dialog.setMessage("Uploading image...");
			dialog.show();
			PostToServerAsyncTask task = new PostToServerAsyncTask(this);
			task.execute(request);
	    } catch (IOException e) {
	    	Log.i("UploadImageActivity", "Failed with exception:"+e);
	        e.printStackTrace();
	    }
	}
	
	public void callback(Object objResult) {
		// Dismiss the progress dialog.
		if (dialog.isShowing())
			dialog.dismiss();
		
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
			
			// Go back to the profile activity
			finish();
		}
	}
}
