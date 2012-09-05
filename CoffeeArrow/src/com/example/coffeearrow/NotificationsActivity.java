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
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.coffeearrow.adapter.NotificationAdapter;
import com.example.coffeearrow.domain.NotificationItem;
import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.RequestFactory;

public class NotificationsActivity extends ListActivity {
	
	private class NotificationsTask extends PostToServerAsyncTask {
	
		public NotificationsTask() {
			super();
		}
	
		@Override
		protected void onPostExecute(Object objResult) {
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

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        HashMap<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("userId", "3495999573");

		HttpPost request = RequestFactory.create(requestParams, "getAllNotificationsNative");	
		NotificationsTask notifications = new NotificationsTask();
        notifications.execute(request);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_notifications, menu);
        return true;
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	NotificationItem item = (NotificationItem) getListAdapter().getItem(position);
		Toast.makeText(this, item.getName() + " selected", Toast.LENGTH_LONG).show();
		
		Intent destIntent = new Intent(this, RequestHistoryActivity.class);
	
		if (item.getLatestInitiatorId().equals(item.getUserId())) {
			destIntent.putExtra("showSure", "true");
			
		}
		destIntent.putExtra("matchId", item.get_id());
		destIntent.putExtra("lockedDate", item.getLocked());
		destIntent.putExtra("dateName", item.getName());

		startActivity(destIntent);
	}
    
    
    
}
