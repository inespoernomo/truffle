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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.example.coffeearrow.adapter.InvitationsAdapter;
import com.example.coffeearrow.domain.InvitationItem;
import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;
import com.example.coffeearrow.server.RequestFactory;

public class InvitationsActivity extends ListActivity implements PostToServerCallback {

	public String userId;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_invitations);
        
		SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
		userId = settings.getString("userId", null);
		
        HashMap<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("userId", userId);
        
		HttpPost request = RequestFactory.create(requestParams, "getAllInvitationsNative");
		
		PostToServerAsyncTask task = new PostToServerAsyncTask(this);
		task.execute(request);
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    Log.i("InvitationActivity", "Got option item selected and menu item is: " + item.toString());
		switch (item.getItemId()) {
		case R.id.searchResults:
			Intent intent1 = new Intent(this, DisplaySearchResultsActivity.class);
			startActivity(intent1);
			break;
		case R.id.logout:
		    SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
		    SharedPreferences.Editor editor = settings.edit();
	        editor.remove("userId");
	        editor.commit();
	        
	        Intent intent = new Intent(this, SignIn.class);
	        // These flags clear the whole thing, so back button will not come back.
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
	        startActivity(intent);
		    break;
		default:
		    Log.i("DisplaySearchResultsActivity", "Unknow menu item clicked.");
		    break;
		}
		return true;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_invitations, menu);
        return true;
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	InvitationItem item = (InvitationItem) getListAdapter().getItem(position);
		Intent destIntent = new Intent(this, RequestHistoryActivity.class);
		destIntent.putExtra("invitationItem", item);
		startActivity(destIntent);
	}
    
    public void callback(Object objResult) {
		JSONArray resultArray = (JSONArray)objResult;
		ArrayList<InvitationItem> responseList = new ArrayList<InvitationItem>();
		ObjectMapper mapper = new ObjectMapper(); 
		try {
			for(int i = 0; i<resultArray.length(); i++) {
				JSONObject jsonObj = resultArray.getJSONObject(i);
				String record = jsonObj.toString(1);
				InvitationItem invitationItem = mapper.readValue(record, InvitationItem.class);
				responseList.add(invitationItem);
				
			} 
		}catch (JSONException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		InvitationsAdapter invitationsAdapter = 
					new InvitationsAdapter(InvitationsActivity.this, 
							responseList);
        InvitationsActivity.this.setListAdapter(invitationsAdapter);
	}

}
