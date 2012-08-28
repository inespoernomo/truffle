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
import com.example.coffeearrow.server.RequestFactory;
import com.example.coffeearrow.server.ServerInterface;
import com.example.coffeearrow.helpers.SquareFrameLayout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowUserProfileActivity extends Activity {
	
	private static final String URL = "http://coffeearrow.com/";
	
	private ShowUserProfileActivity mainActivity = null;

	private class GetUserProfile extends
			AsyncTask<HttpPost, Integer, Object> {

		// This is the first progress dialog we display while fetching the user info.
		// TOOD: There is a gap in between the 2 progress dialogs. See if they can be combined to one.
		private ProgressDialog dialog;
		
		private ShowUserProfileActivity context;
		public ImageLoader imageLoader;

		public GetUserProfile(ShowUserProfileActivity activity) {
			super();
			dialog = new ProgressDialog(activity);
			this.context = activity;
			imageLoader=new ImageLoader(this.context);			
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
				
				// This is the name and profile picture.
				TextView textView = (TextView) findViewById(R.id.label);
				ImageView imageView = (ImageView)findViewById(R.id.icon);
				textView.setText(userProfile.getFirstName());
				//imageView.setImageBitmap(userProfile.getProfileImageBitMap());
				// Lazy load and cache the image.
				imageLoader.DisplayImage(userProfile.getProfileImage(), imageView);
				imageView.setAdjustViewBounds(true);				
				
				// Here we get all the pictures of this user with caption.
				LinearLayout layout = (LinearLayout) findViewById(R.id.container);
				
				for(final UserProfile.Image image : userProfile.getImages()) {
					// Get the size of the display.
					Display display = getWindowManager().getDefaultDisplay();
					Point size = new Point();
					display.getSize(size);
					int displayWidth = size.x;
					
					// A vertical linear layout with one picture (square) and caption for the picture.
					// Width set to the width of the display
					LinearLayout onePicWithCaption = new LinearLayout(context);
					onePicWithCaption.setOrientation(LinearLayout.VERTICAL);
					onePicWithCaption.setLayoutParams(
							new LinearLayout.LayoutParams(displayWidth, LinearLayout.LayoutParams.MATCH_PARENT));
					layout.addView(onePicWithCaption);
					
					// This the frame that make sure the picture is in a square frame.
					SquareFrameLayout picFrame = new SquareFrameLayout(context, null);
					picFrame.setLayoutParams(
							new ViewGroup.LayoutParams(displayWidth, ViewGroup.LayoutParams.MATCH_PARENT));
					onePicWithCaption.addView(picFrame);
					
					// This is the image itself.
					ImageView imageView1 = new ImageView(context);
					imageView1.setScaleType(ImageView.ScaleType.FIT_CENTER);				
					picFrame.addView(imageView1);
					
					// Lazy load and cache the image.
					imageLoader.DisplayImage(image.getImgLink(), imageView1);

					// This is the caption for the image.
					TextView textView1 = new TextView(context);
					textView1.setText(image.getImgCaption());
					onePicWithCaption.addView(textView1);				
				}
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
