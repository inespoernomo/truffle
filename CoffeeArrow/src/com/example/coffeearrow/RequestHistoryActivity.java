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

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.coffeearrow.adapter.DatesAdapter;
import com.example.coffeearrow.domain.DateItem;
import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;
import com.example.coffeearrow.server.RequestFactory;

public class RequestHistoryActivity extends Activity {


	private static final String LOCKED_MESSAGE = "You are going on a date at: ";
	private RequestHistoryActivity mainActivity = null;
	private String matchId;
	
		
/**	private class RequestHistory extends PostToServerAsyncTask {
	
		private String lockDate;
		
		public RequestHistory(String lockDate) {
			super();
			this.lockDate = lockDate;
			
		}
		
		
		protected void onPostExecute(Object objResult) {
			JSONArray resultArray = (JSONArray)objResult;
			ArrayList<DateItem> responseList = new ArrayList<DateItem>();
			ObjectMapper mapper = new ObjectMapper(); 
			try {
				for(int i = 0; i<resultArray.length(); i++) {
					JSONObject jsonObj = resultArray.getJSONObject(i);
					String record = jsonObj.toString(1);
					DateItem dateItem = mapper.readValue(record, DateItem.class);
					responseList.add(dateItem);
					
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
			
			setContentView(R.layout.activity_request_history);
			ListView listView = (ListView) findViewById(R.id.list);
			DatesAdapter adapter = 	
					new DatesAdapter(RequestHistoryActivity.this, responseList);
            listView.setAdapter(adapter);
                        
            if (!lockDate.equals("None")) {
            	TextView text = (TextView) findViewById(R.id.lockDate);
            	text.setText(LOCKED_MESSAGE + lockDate);
            }
            
            Button newDate = (Button) findViewById (R.id.changeDatebutton);
            newDate.setClickable(true);
            newDate.setFocusable(true);
            newDate.setFocusableInTouchMode(true);
            
            newDate.setOnClickListener(new View.OnClickListener() {
          
            	// Requesting new date
    			@Override
    			public void onClick(View v) {
    				
    				Log.i("requestHistory", "Requesting new date");
    				// set up dialog
    				final Dialog dialog = new Dialog(mainActivity);
    				dialog.setContentView(R.layout.activity_change_date);
    				dialog.setTitle("Propose New Time");
    				dialog.setCancelable(true);
    				
    				
    				Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
    				cancelButton.setOnClickListener(new View.OnClickListener() {

    					@Override
    					public void onClick(View v) {
    						dialog.dismiss();
    						
    					}
    					
    				});
    				
    				Button button = (Button) dialog.findViewById(R.id.okButton);
    				button.setOnClickListener(new View.OnClickListener() {

    					@Override
    					public void onClick(View v) {
    						dialog.dismiss();
    						
    					}
    					
    				});
    				
    				dialog.show();
    				
    			}
            	
            	
            });
            
		}
		
	}
*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_history);
    	mainActivity = this;
        Intent intent = getIntent();        
        matchId = intent.getStringExtra("matchId");
        
        HashMap<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("matchId", matchId);
		
		HttpPost request = RequestFactory.create(requestParams, "getNotificationsForMatchNative");	
		
		
        
		PostToServerCallback callback = new PostToServerCallback(){
			public void callback(Object objResult) {
				Log.i("requesthistory", "The objResult is: "+objResult);
				JSONArray resultArray = (JSONArray)objResult;
				ArrayList<DateItem> responseList = new ArrayList<DateItem>();
				ObjectMapper mapper = new ObjectMapper(); 
				try {
					for(int i = 0; i<resultArray.length(); i++) {
						JSONObject jsonObj = resultArray.getJSONObject(i);
						String record = jsonObj.toString(1);
						DateItem dateItem = mapper.readValue(record, DateItem.class);
						responseList.add(dateItem);
						
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
				
				setContentView(R.layout.activity_request_history);
				ListView listView = (ListView) mainActivity.findViewById(R.id.list);
				DatesAdapter adapter = 	
						new DatesAdapter(mainActivity, responseList);
		        listView.setAdapter(adapter);
		                    
		        Intent intent = getIntent();        
		        matchId = intent.getStringExtra("matchId");
				String lockDate = intent.getStringExtra("lockedDate");
				

		        if (!lockDate.equals("None")) {
		        	TextView text = (TextView) mainActivity.findViewById(R.id.lockDate);
		        	text.setText(LOCKED_MESSAGE + lockDate);
		        } 
		        

	            Button newDate = (Button) findViewById (R.id.changeDatebutton);
	            newDate.setClickable(true);
	            newDate.setFocusable(true);
	            newDate.setFocusableInTouchMode(true);
	            
	            newDate.setOnClickListener(new View.OnClickListener() {
	          
	            	// Requesting new date
	    			@Override
	    			public void onClick(View v) {
	    				
	    				Log.i("requestHistory", "Requesting new date");
	    				// set up dialog
	    				final Dialog dialog = new Dialog(mainActivity);
	    				dialog.setContentView(R.layout.activity_change_date);
	    				dialog.setTitle("Propose New Time");
	    				dialog.setCancelable(true);
	    				
	    				
	    				Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
	    				cancelButton.setOnClickListener(new View.OnClickListener() {

	    					@Override
	    					public void onClick(View v) {
	    						dialog.dismiss();
	    						
	    					}
	    					
	    				});
	    				
	    				Button button = (Button) dialog.findViewById(R.id.okButton);
	    				button.setOnClickListener(new View.OnClickListener() {

	    					@Override
	    					public void onClick(View v) {
	    						dialog.dismiss();
	    						
	    					}
	    					
	    				});
	    				
	    				dialog.show();
	    				
				}
            		
            });
	           
			}
		};
		
		PostToServerAsyncTask task = new PostToServerAsyncTask(callback);
		task.execute(request);


    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_request_history, menu);
        return true;
    }

    
    
    /**
     * This method confirms the date request from another person. "Locking" the date.
     * @param view - the view object
     * */
    public void lockTheDate(View view) {
    	
    	Intent intent = getIntent();
    	
    	HashMap<String, String> requestParams = new HashMap<String, String>();
    	requestParams.put("matchId", intent.getStringExtra("matchId"));
    	HttpPost request = RequestFactory.create(requestParams, "lockTheDateNative");
    	
    	PostToServerCallback callback = new PostToServerCallback(){
    		public void callback(Object objResult) {
    			JSONArray resultArray = (JSONArray)objResult;
    			ArrayList<DateItem> responseList = new ArrayList<DateItem>();
    			ObjectMapper mapper = new ObjectMapper(); 
    			try {
    				for(int i = 0; i<resultArray.length(); i++) {
    					
    					JSONObject jsonObj = resultArray.getJSONObject(i);
    					System.out.println(jsonObj);
    					String record = jsonObj.toString(1);
    					DateItem dateItem = mapper.readValue(record, DateItem.class);
    					responseList.add(dateItem);
    					
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

    			TextView lock = (TextView)mainActivity.findViewById(R.id.lockDate);
    			lock.setText(LOCKED_MESSAGE + responseList.get(0).getTime());
    			
    		}
    	};
    	PostToServerAsyncTask task = new PostToServerAsyncTask(callback);
		task.execute(request);
    }
    
}
