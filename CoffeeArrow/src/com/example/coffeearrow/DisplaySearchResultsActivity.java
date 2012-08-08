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
import com.example.coffeearrow.helpers.ConvertImagetoBitmap;
import com.example.coffeearrow.server.IntentFactory;
import com.example.coffeearrow.server.RequestFactory;
import com.example.coffeearrow.server.ServerInterface;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class DisplaySearchResultsActivity extends ListActivity {

	private static final String URL = "http://coffeearrow.com/";

	private DisplaySearchResultsActivity mainActivity = null;

	private class CovertImageToBitMap extends
			AsyncTask<ArrayList<SearchProfile>, Integer, ArrayList<SearchProfile>> {

		private Context context;

		public CovertImageToBitMap(DisplaySearchResultsActivity activity) {
			super();
			this.context = activity;
		}

		@Override
		protected ArrayList<SearchProfile> doInBackground(
				ArrayList<SearchProfile>... params) {
			for (SearchProfile profile : params[0]) {
				profile.setProfileBitMap(ConvertImagetoBitmap
						.getImageBitmap(profile.getProfileImage()));
			}
			return params[0];
		}

		protected void onPostExecute(ArrayList<SearchProfile> profileList) {
			mainActivity.setListAdapter(new DisplayCustomerAdapter(
					DisplaySearchResultsActivity.this, profileList));
		}

		public class DisplayCustomerAdapter extends ArrayAdapter<SearchProfile> {

			private ArrayList<SearchProfile> profileList;

			public DisplayCustomerAdapter(Context context,
					ArrayList<SearchProfile> profileList) {
				super(context, R.layout.activity_display_search_results,
						profileList);
				this.profileList = profileList;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View rowView = inflater
						.inflate(R.layout.activity_display_search_results,
								parent, false);
				TextView textView = (TextView) rowView.findViewById(R.id.label);
				ImageView imageView = (ImageView) rowView
						.findViewById(R.id.icon);
				SearchProfile profile = profileList.get(position);
				textView.setText(profile.toString());
				imageView.setImageBitmap(profile.getProfileBitMap());
				return rowView;
			}
		}
	}

	private class ShowSearchResults extends
			AsyncTask<HttpPost, Integer, Object> {

		private ProgressDialog dialog;

		public ShowSearchResults(Intent intent,
				DisplaySearchResultsActivity activity) {
			super();
			dialog = new ProgressDialog(activity);
		}

		protected void onPreExecute() {
			this.dialog.setMessage("Populating results...");
			this.dialog.show();
		}

		@Override
		protected Object doInBackground(HttpPost... params) {

			return ServerInterface.executeHttpRequest(params[0]);
		}

		@SuppressWarnings("unchecked")
		protected void onPostExecute(Object objResult) {
			if (dialog.isShowing())
				dialog.dismiss();

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
						DisplaySearchResultsActivity.this);
				converter.execute(profileList);
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainActivity = this;

		Intent sourceIntent = getIntent();
		String userId = sourceIntent.getStringExtra("userId");
		HashMap<String, String> requestParams = new HashMap<String, String>();
	    requestParams.put("userId", userId);
	    requestParams.put("ageRange", "19-30");
		
		HttpPost request = RequestFactory.create(URL, requestParams,
				"getSearchResultsNative");
		Intent destIntent = IntentFactory.create(this,
				DisplaySearchResultsActivity.class, requestParams);
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
		return super.onOptionsItemSelected(item);
	}

}
