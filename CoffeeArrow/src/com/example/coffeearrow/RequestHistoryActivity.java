package com.example.coffeearrow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.coffeearrow.adapter.DatesAdapter;
import com.example.coffeearrow.domain.DateItem;
import com.example.coffeearrow.helpers.ImageLoader;
import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;
import com.example.coffeearrow.server.RequestFactory;

public class RequestHistoryActivity extends Activity {


	private static final String LOCKED_MESSAGE = "You are going on a date at: ";
	private RequestHistoryActivity mainActivity = null;
	private String matchId;
	private ImageLoader imageLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request_history);
		mainActivity = this;
		imageLoader = new ImageLoader(this);
		Intent intent = getIntent();
		matchId = intent.getStringExtra("matchId");
		String matchName = intent.getStringExtra("matchName");
		String matchProfileImage = intent.getStringExtra("matchProfileImage");

		HashMap<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("matchId", matchId);
		HttpPost request = RequestFactory.create(requestParams,
				"getNotificationsForMatchNative");

		setContentView(R.layout.activity_request_history);
		ImageView profileImage = (ImageView) findViewById(R.id.icon);
		imageLoader.DisplayImage(matchProfileImage, profileImage);

		TextView textView = (TextView) findViewById(R.id.label);
		textView.setText(matchName);

		PostToServerCallback callback = new PostToServerCallback() {
			public void callback(Object objResult) {
				Log.i("requesthistory", "The objResult is: " + objResult);
				JSONArray resultArray = (JSONArray) objResult;
				ArrayList<DateItem> responseList = new ArrayList<DateItem>();
				ObjectMapper mapper = new ObjectMapper();
				try {
					for (int i = 0; i < resultArray.length(); i++) {
						JSONObject jsonObj = resultArray.getJSONObject(i);
						String record = jsonObj.toString(1);
						DateItem dateItem = mapper.readValue(record,
								DateItem.class);
						responseList.add(dateItem);

					}
				} catch (JSONException e) {
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

				ListView listView = (ListView) mainActivity
						.findViewById(R.id.list);
				DatesAdapter adapter = new DatesAdapter(mainActivity,
						responseList);
				listView.setAdapter(adapter);

				final Intent intent = getIntent();
				String lockDate = intent.getStringExtra("lockedDate");

				if (!lockDate.equals("None")) {
					TextView text = (TextView) mainActivity
							.findViewById(R.id.lockDate);
					text.setText(LOCKED_MESSAGE + lockDate);
				}

				Button newDate = (Button) findViewById(R.id.changeDatebutton);
				newDate.setClickable(true);
				newDate.setFocusable(true);
				newDate.setFocusableInTouchMode(true);

				newDate.setOnClickListener(new View.OnClickListener() {
					// Requesting new date
					public void onClick(View v) {

						Log.i("requestHistory", "Requesting new date");
						// set up dialog
						final Dialog dialog = new Dialog(mainActivity);
						dialog.setContentView(R.layout.activity_change_date);
						dialog.setTitle("Propose New Time");
						dialog.setCancelable(true);

						Button cancelButton = (Button) dialog
								.findViewById(R.id.cancelButton);
						cancelButton
								.setOnClickListener(new View.OnClickListener() {

									public void onClick(View v) {
										dialog.dismiss();

									}

								});

						Button button = (Button) dialog
								.findViewById(R.id.okButton);
						
						
						button.setOnClickListener(new View.OnClickListener() {

							public void onClick(View v) {
								
								DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker1);
								int day = datePicker.getDayOfMonth();
								int month = datePicker.getMonth() + 1;
								int year = datePicker.getYear();
								 
								TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePicker1);
								int hour = timePicker.getCurrentHour();
								int minute = timePicker.getCurrentMinute();
								
								Calendar calendar = Calendar.getInstance();
								calendar.set(year, month, day, hour, minute, 0);
								
						    	HashMap<String, String> requestParams = new HashMap<String, String>();
						    	requestParams.put("userId", intent.getStringExtra("userId"));
						    	requestParams.put("dateId", intent.getStringExtra("dateId"));
						    	requestParams.put("time", calendar.toString());
						    	Log.i("RequestHistoryActivity", "Sending the new time to server: " + calendar.toString());
						    	HttpPost request = RequestFactory.create(requestParams, "saveProposedMatchNative");


								dialog.dismiss();

						    	PostToServerAsyncTask task = new PostToServerAsyncTask(null);
								task.execute(request);
								
								
								
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

    public class NewDateCallback implements PostToServerCallback {

    	Activity _mainActivity; 
    	String _lockedDate;
    	public NewDateCallback(Activity mainActivity, String lockedDate) {
    		_mainActivity = mainActivity;
    		_lockedDate = lockedDate;
    		
    	}
		public void callback(Object result) {

			TextView text = (TextView) _mainActivity
					.findViewById(R.id.lockDate);
			text.setText(LOCKED_MESSAGE + _lockedDate);
		}
    	
    	
    }
}

