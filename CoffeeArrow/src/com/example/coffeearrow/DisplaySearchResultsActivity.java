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

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.coffeearrow.adapter.UserRowsAdapter;
import com.example.coffeearrow.domain.SearchProfile;
import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;
import com.example.coffeearrow.server.RequestFactory;

public class DisplaySearchResultsActivity extends ListActivity implements PostToServerCallback {

	private String userId;
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        
		SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
		userId = settings.getString("userId", null);
		
		HashMap<String, String> requestParams = new HashMap<String, String>();
	    requestParams.put("userId", userId);
	    requestParams.put("ageRange", "19-30");
		HttpPost request = RequestFactory.create(requestParams, "getSearchResultsNative");
		
		// Display the progress dialog.		
		dialog = new ProgressDialog(this);
		this.dialog.setMessage("Populating results...");
		this.dialog.show();
		PostToServerAsyncTask task = new PostToServerAsyncTask(this);
		task.execute(request);
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
	    Log.i("DisplaySearchResultsActivity", "Got option item selected and menu item is: " + item.toString());
		switch (item.getItemId()) {
		case R.id.invitations:
			Intent intent1 = new Intent(this, InvitationsActivity.class);
			startActivity(intent1);
			break;
		case R.id.userProfile:
			Intent intent2 = new Intent(this, SelfProfileActivity.class);
			intent2.putExtra("userId", userId);
			startActivity(intent2);
			break;
		default:
		    Log.i("DisplaySearchResultsActivity", "Unknow menu item clicked.");
		    break;
		}
		return true;
	}
	
	public void callback(Object result) {
		// Dismiss the progress dialog.
		if (dialog.isShowing())
			dialog.dismiss();
		
		if (result != null) {
			JSONArray resultArray = (JSONArray) result;
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
				
				this.setListAdapter(new UserRowsAdapter(this, profileList));

			} catch (JSONException e) {
				e.printStackTrace();
			} catch (JsonParseException e) {

				e.printStackTrace();
			} catch (JsonMappingException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

}
