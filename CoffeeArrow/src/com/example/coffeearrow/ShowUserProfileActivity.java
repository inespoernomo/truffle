package com.example.coffeearrow;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.methods.HttpPost;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.coffeearrow.domain.UserProfile;
import com.example.coffeearrow.helpers.ImageLoader;
import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;
import com.example.coffeearrow.server.RequestFactory;
import com.example.coffeearrow.helpers.SquareFrameLayout;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowUserProfileActivity extends Activity implements PostToServerCallback {

	private ImageLoader imageLoader;
	private int displayWidth;
	private LinearLayout userImages;
	private ProgressDialog dialog;
	
	protected String userId;
	protected ShowUserProfileActivity mainActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mainActivity = this;
		imageLoader=new ImageLoader(this);
		dialog = new ProgressDialog(mainActivity);
		
		// Get the size of the display.
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		displayWidth = size.x;
		
		Intent sourceIntent = getIntent();
		userId = sourceIntent.getStringExtra("userId");
		System.out.println("Got user id from source intent:"+userId);
		HashMap<String, String> requestParams = new HashMap<String, String>();
	    requestParams.put("userId", userId);
		HttpPost request = RequestFactory.create(requestParams, "getUserProfile");
		
		// Display the progress dialog.
		this.dialog.setMessage("Building profile...");
		this.dialog.show();
		PostToServerAsyncTask task = new PostToServerAsyncTask(this);
		task.execute(request);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_show_user_profile, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {		
		if(item.getItemId() == R.id.sendInvitation) {
			//TODO: Hook up send invitation.
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Add one image to the end of all the images we ar displaying
	 * @param s3url The url to the image. Currently it's from s3
	 * @param caption The caption string for the image.
	 */
	protected void addImageWithCaption(String s3url, String caption) {
		// A vertical linear layout with one picture (square) and caption for the picture.
		// Width set to the width of the display
		LinearLayout onePicWithCaption = new LinearLayout(this);
		onePicWithCaption.setOrientation(LinearLayout.VERTICAL);
		onePicWithCaption.setLayoutParams(
				new LinearLayout.LayoutParams(displayWidth, LinearLayout.LayoutParams.MATCH_PARENT));
		userImages.addView(onePicWithCaption);
		
		// This the frame that make sure the picture is in a square frame.
		SquareFrameLayout picFrame = new SquareFrameLayout(this, null);
		picFrame.setLayoutParams(
				new ViewGroup.LayoutParams(displayWidth, ViewGroup.LayoutParams.MATCH_PARENT));
		onePicWithCaption.addView(picFrame);
		
		// This is the image itself.
		ImageView imageView = new ImageView(this);
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);				
		picFrame.addView(imageView);
		
		// Lazy load and cache the image.
		imageLoader.DisplayImage(s3url, imageView);
		
		// Add click handler for the image
		addImageClickListener(imageView, s3url, caption);
	
		// This is the caption for the image.
		TextView captionTextView = new TextView(this);
		captionTextView.setText(caption);
		onePicWithCaption.addView(captionTextView);
	}
	
	protected void addImageClickListener(ImageView view, String s3url, String caption) {
		// Do nothing now. For SelfProfileActivity to override.
	}
	
	protected void editImage(String s3url, String caption) {
		// Do nothing now. For SelfProfileActivity to override.		
	}
	
	public void callback(Object objResult) {
		// Dismiss the progress dialog
		if (dialog.isShowing())
			dialog.dismiss();
		
		if (objResult != null) {
			// Parse the JSON
			JSONArray resultArray = (JSONArray) objResult;
			UserProfile userProfile = null;
			ObjectMapper mapper = new ObjectMapper();
			try {
				for (int i = 0; i < resultArray.length(); i++) {
					JSONObject jsonObj = resultArray.getJSONObject(i);
					String record = jsonObj.toString(1);
					userProfile = mapper.readValue(record,
							UserProfile.class);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (JsonParseException e) {

				e.printStackTrace();
			} catch (JsonMappingException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
			System.out.println("Got back the user profile:");
			System.out.println(userProfile);
			
			// We setup the content view here instead of in the onCreate method of the main activity because
			// If we put it in onCreate, we do not have enough information at that time and will display 
			// empty info like (null) etc, and it's bad for user experience.
			mainActivity.setContentView(R.layout.activity_show_user_profile);
			
			// This is the LinearLayout containing all the pictures of this user with caption.
			userImages = (LinearLayout) findViewById(R.id.container);
			
			// This is the name and profile picture.
			TextView textView = (TextView) findViewById(R.id.label);
			ImageView imageView = (ImageView)findViewById(R.id.icon);
			textView.setText(userProfile.getFirstName());

			// Lazy load and cache the image.
			imageLoader.DisplayImage(userProfile.getProfileImage(), imageView);
			imageView.setAdjustViewBounds(true);
			
			for(final UserProfile.Image image : userProfile.getImages()) {
				addImageWithCaption(image.getImgLink(), image.getImgCaption());
			}
		}
	}
}
