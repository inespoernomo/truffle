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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.coffeearrow.domain.InvitationItem;
import com.example.coffeearrow.domain.UserProfile;
import com.example.coffeearrow.helpers.ImageLoader;
import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;
import com.example.coffeearrow.server.RequestFactory;

public class ShowUserProfileActivity extends Activity implements
		PostToServerCallback {
	private static final int INVITE_REQUEST_CODE = 4567;

	protected ImageLoader imageLoader;
	private int displayWidth;
	private LinearLayout userImages;
	private ProgressDialog dialog;
	private boolean comesFromInvitation;
	protected String userId;
	protected ShowUserProfileActivity mainActivity;
	protected UserProfile userProfile;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mainActivity = this;
		imageLoader = new ImageLoader(this);
		dialog = new ProgressDialog(mainActivity);

		// Get the size of the display.
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		displayWidth = size.x;

		Intent sourceIntent = getIntent();
		userId = sourceIntent.getStringExtra("userId");
		System.out.println("Got user id from source intent:" + userId);
		comesFromInvitation = sourceIntent.getBooleanExtra(
				"comesFromInvitation", false);
		HashMap<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("userId", userId);
		HttpPost request = RequestFactory.create(requestParams,
				"getUserProfile");

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
		SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
		SharedPreferences.Editor editor = settings.edit();
		Log.i("ShowUserProfileActivity", "Invite menu item clicked." + item.toString());
		switch (item.getItemId()) {
		case R.id.invitations:
			Intent intent1 = new Intent(this, InvitationsActivity.class);
			startActivity(intent1);
			break;
		case R.id.logout:

			editor.remove("userId");
			editor.commit();

			Intent intent = new Intent(this, SignIn.class);
			// These flags clear the whole thing, so back button will not come
			// back.
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			break;
		case R.id.sendInvitation:
			// TODO: Below need to be replaced with an different server method.
			// TODO: When the other user has invited you, we should also not
			// charge the user.
			String loggedInUserId = settings.getString("userId", null);

			HashMap<String, String> requestParams = new HashMap<String, String>();
			requestParams.put("userId", loggedInUserId);

			HttpPost request = RequestFactory.create(requestParams,
					"getAllInvitationsNative");

			PostToServerAsyncTask task = new PostToServerAsyncTask(
					new PostToServerCallback() {
						public void callback(Object objResult) {
							Log.i("ShowUserProfileActivity",
									"getAllInvitationsNative called back with: "
											+ objResult);
							JSONArray resultArray = (JSONArray) objResult;
							ObjectMapper mapper = new ObjectMapper();
							InvitationItem invitationItem = null;
							boolean invited = false;
							try {
								for (int i = 0; i < resultArray.length(); i++) {

									JSONObject jsonObj = resultArray
											.getJSONObject(i);
									String record = jsonObj.toString(1);
									invitationItem = mapper.readValue(record,
											InvitationItem.class);

									// Check both if user is inviting the user
									// or being invited.
									if (invitationItem.getUserId().equals(
											userId)
											|| invitationItem.getDateId()
													.equals(userId)) {
										invited = true;
										break;
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							} catch (JsonParseException e) {
								// TODO Auto-generated catch block
								Log.i("ShowUserProfileActivity", "exception 1");
								e.printStackTrace();
							} catch (JsonMappingException e) {
								// TODO Auto-generated catch block
								Log.i("ShowUserProfileActivity", "exception 2");
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								Log.i("ShowUserProfileActivity", "exception 3");
								e.printStackTrace();
							}

							if (invited) {
								if (comesFromInvitation) {
									finish();
								} else {
									Intent destIntent = new Intent(
											mainActivity,
											RequestHistoryActivity.class);
									destIntent.putExtra("invitationItem",
											invitationItem);
									destIntent.putExtra("comesFromProfile",
											true);
									startActivity(destIntent);
								}

							} else {
								Intent intent = new Intent(mainActivity,
										NewDateActivity.class);
								intent.putExtra("dateId", mainActivity.userId);
								mainActivity.startActivityForResult(intent,
										INVITE_REQUEST_CODE);
							}

						}
					});
			task.execute(request);
		default:
			Log.i("SelfProfile", "Unknown menu item.");
			break;
		}
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent returnedIntent) {
		super.onActivityResult(requestCode, resultCode, returnedIntent);

		// Get back from the payment activity.
		Log.i("ShowUserProfileActivity",
				"Got back from payment with requestCode: " + requestCode
						+ " and resultCode: " + resultCode);

		switch (requestCode) {
		case INVITE_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				Log.i("ShowUserProfileActivity", "still ok");
				onOptionsItemSelected(null);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Add one image to the end of all the images we ar displaying
	 * 
	 * @param s3url
	 *            The url to the image. Currently it's from s3
	 * @param caption
	 *            The caption string for the image.
	 */
	protected void addImageWithCaption(String s3url, String caption) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(
				R.layout.activity_display_search_results, userImages, false);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				displayWidth - 30, RelativeLayout.LayoutParams.MATCH_PARENT);
		params.setMargins(5, 5, 5, 5);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		RelativeLayout imageFrame = (RelativeLayout) rowView
				.findViewById(R.id.imageFrame);
		imageFrame.setLayoutParams(params);

		ImageView image = (ImageView) rowView.findViewById(R.id.icon);
		imageLoader.DisplayImage(s3url, image);

		// Add click handler for the image
		addImageClickListener(image, s3url, caption);

		TextView text = (TextView) rowView.findViewById(R.id.nameOnImage);
		text.setText(caption);
		userImages.addView(rowView);
	}

	protected void updateProfileImage(String url) {
		ImageView imageView = (ImageView) findViewById(R.id.icon);
		imageLoader.DisplayImage(url, imageView);
		imageView.setAdjustViewBounds(true);
	}

	protected void addImageClickListener(ImageView view, String s3url,
			String caption) {
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
			ObjectMapper mapper = new ObjectMapper();
			try {
				for (int i = 0; i < resultArray.length(); i++) {
					JSONObject jsonObj = resultArray.getJSONObject(i);
					String record = jsonObj.toString(1);
					userProfile = mapper.readValue(record, UserProfile.class);
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
			Log.i("ShowUserProfileActivity", "Got back the user profile: "
					+ userProfile);

			// We setup the content view here instead of in the onCreate method
			// of the main activity because
			// If we put it in onCreate, we do not have enough information at
			// that time and will display
			// empty info like (null) etc, and it's bad for user experience.
			mainActivity.setContentView(R.layout.activity_show_user_profile);

			// This is the LinearLayout containing all the pictures of this user
			// with caption.
			userImages = (LinearLayout) findViewById(R.id.container);

			// This is the name and profile picture.
			TextView textView = (TextView) findViewById(R.id.label);
			textView.setText(userProfile.getFirstName());

			updateProfileImage(userProfile.getProfileImage());

			for (final UserProfile.Image image : userProfile.getImages()) {
				addImageWithCaption(image.getImgLink(), image.getImgCaption());
			}
		}
	}
}
