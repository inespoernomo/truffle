package com.example.coffeearrow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.methods.HttpPost;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.coffeearrow.domain.SearchProfile;
import com.example.coffeearrow.helpers.ImageLoader;
import com.example.coffeearrow.server.RequestFactory;
import com.example.coffeearrow.server.ServerInterface;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class DisplaySearchResultsActivity extends ListActivity {

	private DisplaySearchResultsActivity mainActivity = null;
	private String userId;

	private class ShowSearchResults extends
			AsyncTask<HttpPost, Integer, Object> {

		// This is the first progress dialog we display while fetching the search result.
		private ProgressDialog dialog;

		public ShowSearchResults(Intent intent,
				DisplaySearchResultsActivity activity) {
			super();
			dialog = new ProgressDialog(activity);
		}

		protected void onPreExecute() {
			// Display the progress dialog.
			this.dialog.setMessage("Populating results...");
			this.dialog.show();
		}

		@Override
		protected Object doInBackground(HttpPost... params) {

			return ServerInterface.executeHttpRequest(params[0]);
		}

		protected void onPostExecute(Object objResult) {
			if (objResult != null) {
				JSONArray resultArray = (JSONArray) objResult;
				ArrayList<SearchProfile> profileList = new ArrayList<SearchProfile>();
				ObjectMapper mapper = new ObjectMapper();
				try {
					for (int i = 0; i < resultArray.length(); i++) {
						JSONObject jsonObj = resultArray.getJSONObject(i);
						String record = jsonObj.toString(1);
						SearchProfile profile = mapper.readValue(record,
								SearchProfile.class);

						profileList.add(profile);
					}
					
					mainActivity.setListAdapter(new DisplayCustomerAdapter(
							DisplaySearchResultsActivity.this, profileList));

				} catch (JSONException e) {
					e.printStackTrace();
				} catch (JsonParseException e) {

					e.printStackTrace();
				} catch (JsonMappingException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}

				// Dismiss the progress dialog.
				if (dialog.isShowing())
					dialog.dismiss();
			}
		}
		
		/**
		 * This is the adapter for the DisplaySearchResultsActivity, which is a ListActiviy.
		 * Given it the profile list, with just image url, not converted to bitmap, it can easy load each
		 * picture and cache them when getView is called.
		 * @author sunshi
		 *
		 */
		public class DisplayCustomerAdapter extends ArrayAdapter<SearchProfile> {

			private ArrayList<SearchProfile> profileList;
			public ImageLoader imageLoader;
			
			public DisplayCustomerAdapter(Context context,
					ArrayList<SearchProfile> profileList) {
				super(context, R.layout.activity_display_search_results,
						profileList);
				this.profileList = profileList;
				imageLoader=new ImageLoader(DisplaySearchResultsActivity.this.getApplicationContext());
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// Find the profile
				SearchProfile profile = profileList.get(position);
				
				// Get the empty row view from the xml.
				LayoutInflater inflater = (LayoutInflater) DisplaySearchResultsActivity.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View rowView = inflater
						.inflate(R.layout.activity_display_search_results,
								parent, false);
				
				// Get the size of the display.
				Display display = getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				
				// We want to display 5 results each page.
				int rowHeight = size.y / 5;
				
				// So we set the height of the row to 1/5 of the display height.
				rowView.setLayoutParams(
						new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, rowHeight));
				
				// This is the profile image and we want it to be square.
				ImageView imageView = (ImageView) rowView
						.findViewById(R.id.icon);
				imageView.setLayoutParams(
						new LinearLayout.LayoutParams(rowHeight, rowHeight));
				
				// Lazy load and cache the image.
				imageLoader.DisplayImage(profile.getProfileImage(), imageView);
				
				// The is the label for name and city.
				TextView textView = (TextView) rowView.findViewById(R.id.label);				
				textView.setText(profile.toString());
				
				return rowView;
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainActivity = this;
		
		SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
		userId = settings.getString("userId", null);
		
		HashMap<String, String> requestParams = new HashMap<String, String>();
	    requestParams.put("userId", userId);
	    requestParams.put("ageRange", "19-30");
		
		HttpPost request = RequestFactory.create(requestParams, "getSearchResultsNative");
		Intent destIntent = new Intent(this,
				DisplaySearchResultsActivity.class);
		ShowSearchResults searchResults = new ShowSearchResults(destIntent,
				this);
		searchResults.execute(request);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		SearchProfile profile = (SearchProfile) getListAdapter().getItem(position);
		Intent intent = new Intent(this, ShowUserProfileActivity.class);
		intent.putExtra("userId", profile.get_id());
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_display_search_results, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		if(item.getItemId() == R.id.notifications) {
			Intent intent = new Intent(this, NotificationsActivity.class);
			startActivity(intent);
		}
		if(item.getItemId() == R.id.userProfile) {
			Intent intent = new Intent(this, SelfProfileActivity.class);
			intent.putExtra("userId", userId);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

}
