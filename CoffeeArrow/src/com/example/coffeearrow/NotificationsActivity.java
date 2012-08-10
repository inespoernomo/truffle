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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.coffeearrow.adapter.NotificationAdapter;
import com.example.coffeearrow.domain.NotificationItem;
import com.example.coffeearrow.server.IntentFactory;
import com.example.coffeearrow.server.RequestFactory;
import com.example.coffeearrow.server.ServerInterface;

public class NotificationsActivity extends ListActivity {
	
	private static final String URL = "http://coffeearrow.com/";
	
	private class NotificationsTask extends AsyncTask<HttpPost, Integer, Object> {
		
		private Intent intent;
	
		public NotificationsTask(Intent intent) {
			super();
			this.intent = intent;
		}
		
		@Override
		protected Object doInBackground(HttpPost... params) {
			
			return ServerInterface.executeHttpRequest(params[0]);
		}
		
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
        
        Intent sourceIntent = getIntent();
        String userId = sourceIntent.getStringExtra("userId");
        
        HashMap<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("userId", "3495999573");

		HttpPost request = RequestFactory.create(URL, requestParams, "getAllNotificationsNative");	
		Intent destIntent = IntentFactory.create(this, NotificationsActivity.class, requestParams);
		NotificationsTask notifications = new NotificationsTask(destIntent);
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
		HashMap<String, String> extendedData = new HashMap<String, String>();
	
		if (item.getLatestInitiatorId().equals(item.getUserId())) {
			extendedData.put("showSure", "true");
			
		}
		extendedData.put("matchId", item.get_id());
		extendedData.put("lockedDate", item.getLocked());
		extendedData.put("dateName", item.getName());

		Intent destIntent = IntentFactory.create(this, RequestHistoryActivity.class, extendedData);
		startActivity(destIntent);
	}
    
    
    
}
