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
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import com.example.coffeearrow.adapter.NotificationAdapter;
import com.example.coffeearrow.domain.NotificationItem;
import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;
import com.example.coffeearrow.server.RequestFactory;

public class NotificationsActivity extends ListActivity implements PostToServerCallback {

	public String userId;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
		userId = settings.getString("userId", null);
		
        HashMap<String, String> requestParams = new HashMap<String, String>();
		//requestParams.put("userId", userId);
        requestParams.put("userId", "3495999573");
        
		HttpPost request = RequestFactory.create(requestParams, "getAllNotificationsNative");	
		
		PostToServerAsyncTask task = new PostToServerAsyncTask(this);
		task.execute(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_notifications, menu);
        return true;
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	NotificationItem item = (NotificationItem) getListAdapter().getItem(position);
		
		Intent destIntent = new Intent(this, RequestHistoryActivity.class);

		if (item.getLatestInitiatorId().equals(item.getUserId())) {
			destIntent.putExtra("showSure", "true");
			
		}
		destIntent.putExtra("matchId", item.get_id());
		destIntent.putExtra("matchName", item.getName());
		destIntent.putExtra("matchProfileImage", item.getProfileImage());
		destIntent.putExtra("lockedDate", item.getLocked());
		destIntent.putExtra("dateName", item.getName());

		startActivity(destIntent);
	}
    
    public void callback(Object objResult) {
		JSONArray resultArray = (JSONArray)objResult;
		ArrayList<NotificationItem> responseList = new ArrayList<NotificationItem>();
		ObjectMapper mapper = new ObjectMapper(); 
		try {
			for(int i = 0; i<resultArray.length(); i++) {
				JSONObject jsonObj = resultArray.getJSONObject(i);
				String record = jsonObj.toString(1);
				NotificationItem notificationItem = mapper.readValue(record, NotificationItem.class);
				responseList.add(notificationItem);
				
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
		
		NotificationAdapter notificationAdapter = 
					new NotificationAdapter(NotificationsActivity.this, 
							responseList);
        NotificationsActivity.this.setListAdapter(notificationAdapter);
	}

}
