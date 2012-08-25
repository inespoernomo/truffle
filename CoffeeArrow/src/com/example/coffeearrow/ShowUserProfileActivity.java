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
import com.example.coffeearrow.helpers.ConvertImagetoBitmap;
import com.example.coffeearrow.server.RequestFactory;
import com.example.coffeearrow.server.ServerInterface;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowUserProfileActivity extends Activity {
	
	private static final String URL = "http://coffeearrow.com/";
	
	private ShowUserProfileActivity mainActivity = null;

	private class CovertImageToBitMap extends
			AsyncTask<UserProfile, Integer, UserProfile> {

		private Context context;
		
		// This is the second progress dialog we display while doing the convert image to big map async task.
		// TOOD: There is a gap in between the 2 progress dialogs. See if they can be combined to one.
		private ProgressDialog dialog;

		public CovertImageToBitMap(ShowUserProfileActivity activity) {
			super();
			this.context = activity;
			dialog = new ProgressDialog(activity);
		}

		@Override
		protected UserProfile doInBackground(
				UserProfile... params) {
			UserProfile userProfile = params[0];
			userProfile.setProfileImageBitMap(ConvertImagetoBitmap
					.getImageBitmap(userProfile.getProfileImage()));
			
			for (UserProfile.Image image : userProfile.getImages()) {
				image.setBitMapImgLink(ConvertImagetoBitmap.getImageBitmap(image.getImgLink()));	
			}
			
			return userProfile;
		}
		
		protected void onPreExecute() {
			// Display the progress dialog.
			this.dialog.setMessage("Fetching images...");
			this.dialog.show();
		}

		// After convert all the images to bitmap, show them.
		protected void onPostExecute(UserProfile userProfile) {
			// We setup the content view here instead of in the onCreate method of the main activity because
			// If we put it in onCreate, we do not have enough information at that time and will display 
			// empty info like (null) etc, and it's bad for user experience.
			mainActivity.setContentView(R.layout.activity_show_user_profile);
			
			// This is the name and profile picture.
			TextView textView = (TextView) findViewById(R.id.label);
			ImageView imageView = (ImageView)findViewById(R.id.icon);
			textView.setText(userProfile.getFirstName());
			imageView.setImageBitmap(userProfile.getProfileImageBitMap());
			LinearLayout layout = (LinearLayout) findViewById(R.id.container);
			
			// Here we get all the pictures of this user with caption.
			// TODO: Cache locally.
			for(final UserProfile.Image image : userProfile.getImages()) {
				TextView textView1 = new TextView(context);
				textView1.setText(image.getImgCaption());
				ImageView imageView1 = new ImageView(context);
				imageView1.setImageBitmap(image.getBitMapImgLink());
				layout.addView(imageView1);
				layout.addView(textView1);
			}
			
			// Dismiss the progress dialog
			if (dialog.isShowing())
				dialog.dismiss();
		}

	}

	private class GetUserProfile extends
			AsyncTask<HttpPost, Integer, Object> {

		// This is the first progress dialog we display while fetching the user info.
		// TOOD: There is a gap in between the 2 progress dialogs. See if they can be combined to one.
		private ProgressDialog dialog;

		public GetUserProfile(ShowUserProfileActivity activity) {
			super();
			dialog = new ProgressDialog(activity);
		}

		protected void onPreExecute() {
			// Display the progress dialog.
			this.dialog.setMessage("Building profile...");
			this.dialog.show();
		}

		@Override
		protected Object doInBackground(HttpPost... params) {

			return ServerInterface.executeHttpRequest(params[0]);
		}

		protected void onPostExecute(Object objResult) {
			if (objResult != null) {
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

				CovertImageToBitMap converter = new CovertImageToBitMap(
						ShowUserProfileActivity.this);
				converter.execute(userProfile);
			}
			
			// Dismiss the progress dialog
			if (dialog.isShowing())
				dialog.dismiss();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mainActivity = this;
		super.onCreate(savedInstanceState);
		Intent sourceIntent = getIntent();
		
		String userId = sourceIntent.getStringExtra("userId");
		System.out.println("Got user id from source intent:");
		System.out.println(userId);
		HashMap<String, String> requestParams = new HashMap<String, String>();
	    requestParams.put("userId", userId);
		
		
		HttpPost request = RequestFactory.create(URL, requestParams,
				"getUserProfile");
		System.out.println("Created request:");
		System.out.println(request);
		GetUserProfile getUserProfile = new GetUserProfile(this);
		getUserProfile.execute(request);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_show_user_profile, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
		String userId = settings.getString("userId", null);
		
		if(item.getItemId() == R.id.sendInvitation) {
			//TODO: Hook up send invitation.
		}
		return super.onOptionsItemSelected(item);
	}
}
