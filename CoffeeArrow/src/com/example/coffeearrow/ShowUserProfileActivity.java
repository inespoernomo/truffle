package com.example.coffeearrow;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.methods.HttpPost;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.coffeearrow.domain.SearchProfile;
import com.example.coffeearrow.domain.UserProfile;
import com.example.coffeearrow.helpers.ConvertImagetoBitmap;
import com.example.coffeearrow.server.RequestFactory;
import com.example.coffeearrow.server.ServerInterface;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowUserProfileActivity extends Activity {
	
	private static final String URL = "http://coffeearrow.com/";
	
	private ShowUserProfileActivity mainActivity = null;

	private class CovertImageToBitMap extends
			AsyncTask<UserProfile, Integer, UserProfile> {

		private Context context;

		public CovertImageToBitMap(ShowUserProfileActivity activity) {
			super();
			this.context = activity;
		}

		@Override
		protected UserProfile doInBackground(
				UserProfile... params) {
			UserProfile userProfile = params[0];
			userProfile.setProfileImageBitMap(ConvertImagetoBitmap
					.getImageBitmap(userProfile.getProfileImage()));
			ArrayList<UserProfile.Image> bitMapImages = new ArrayList<UserProfile.Image>();
			for (UserProfile.Image image : userProfile.getImages()) {
				image.setBitMapImgLink(ConvertImagetoBitmap.getImageBitmap(image.getImgLink()));	
			}
			
			return userProfile;
		}

		protected void onPostExecute(UserProfile userProfile) {
			System.out.println("UserProfile in post execute " + userProfile.toString());
			mainActivity.setContentView(R.layout.activity_show_user_profile);
			TextView textView = (TextView) findViewById(R.id.label);
			ImageView imageView = (ImageView)findViewById(R.id.icon);
			textView.setText(userProfile.getFirstName());
			imageView.setImageBitmap(userProfile.getProfileImageBitMap());
			LinearLayout layout = (LinearLayout) findViewById(R.id.container);
			for(UserProfile.Image image : userProfile.getImages()) {
				ImageView imageView1 = new ImageView(context);
				imageView1.setImageBitmap(image.getBitMapImgLink());
				layout.addView(imageView1);
			}
			
		}

	}

	private class GetUserProfile extends
			AsyncTask<HttpPost, Integer, Object> {

		private ProgressDialog dialog;

		public GetUserProfile(ShowUserProfileActivity activity) {
			super();
			dialog = new ProgressDialog(activity);
		}

		protected void onPreExecute() {
			this.dialog.setMessage("Building profile...");
			this.dialog.show();
		}

		@Override
		protected Object doInBackground(HttpPost... params) {

			return ServerInterface.executeHttpRequest(params[0]);
		}

		protected void onPostExecute(Object objResult) {
			if (dialog.isShowing())
				dialog.dismiss();

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

				CovertImageToBitMap converter = new CovertImageToBitMap(
						ShowUserProfileActivity.this);
				converter.execute(userProfile);
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mainActivity = this;
		super.onCreate(savedInstanceState);
		Intent sourceIntent = getIntent();
		
		String userId = sourceIntent.getStringExtra("userId");

		JSONObject jsonRequestParams = new JSONObject();
		try {
			jsonRequestParams.put("userId", userId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		HttpPost request = RequestFactory.create(URL, jsonRequestParams,
				"getUserProfile");
		GetUserProfile getUserProfile = new GetUserProfile(this);
		getUserProfile.execute(request);
		
		//setContentView(R.layout.activity_show_user_profile);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_show_user_profile, menu);
		return true;
	}
}
