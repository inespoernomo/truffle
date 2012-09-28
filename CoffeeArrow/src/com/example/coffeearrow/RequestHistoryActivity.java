package com.example.coffeearrow;

import java.util.HashMap;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.coffeearrow.helpers.ImageLoader;
import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;
import com.example.coffeearrow.server.RequestFactory;

public class RequestHistoryActivity extends Activity {

	private static final String LOCKED_MESSAGE = "The date and location is locked.";
	private RequestHistoryActivity mainActivity = null;
	private String matchId;
	private String userId;
	private ImageLoader imageLoader;
	String matchName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request_history);
		mainActivity = this;
		imageLoader = new ImageLoader(this);
		Intent intent = getIntent();
		matchId = intent.getStringExtra("matchId");
		matchName = intent.getStringExtra("matchName");
		String matchProfileImage = intent.getStringExtra("matchProfileImage");
		SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
        userId = settings.getString("userId", null);

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
				
				String initiater = null;
				String lastestInitiatorId = null;
				String epoch = null;
				String place = null;
				String preEpoch = null;
				String prePlace = null;
                String lockDate = null;
				try {
					for (int i = 0; i < resultArray.length(); i++) {
						JSONObject record = resultArray.getJSONObject(i);
						Log.i("RequestHistoryActivity", "record is: " + record);
						initiater = record.getString("userId");
						epoch = record.getString("currEpoch");
						place = record.getString("currPlace");
						lockDate = record.getString("locked");
                        lastestInitiatorId = record.getString("latestInitiatorId");
                        if (record.has("prevEpoch")) {
                            preEpoch = record.getString("prevEpoch");
                            prePlace = record.getString("prevPlace");
                        }
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				// Initiator section
				TextView initiaterTextView = (TextView) mainActivity.findViewById(R.id.initiaterTextView);
				if(initiater.equals(userId)) {
				    initiaterTextView.setText("You invited " + matchName + " to meet.");
				} else {
				    initiaterTextView.setText(matchName + " invited you to meet.");
				}
				
				// Logic to figure out latest and previous date and location setter.
				Button agreeButton = (Button) mainActivity.findViewById(R.id.surebutton);
				String preDateAndLocationSetter;
				String curDateAndLocationSetter;
				if(lastestInitiatorId.equals(userId)){
				    preDateAndLocationSetter = matchName;
				    curDateAndLocationSetter = "You";
				    agreeButton.setEnabled(false);
                } else {
                    preDateAndLocationSetter = "You";
                    curDateAndLocationSetter = matchName;
                    agreeButton.setEnabled(true);
                }
				
				// Previous time and location section
				TextView preModifierTextView = (TextView) mainActivity.findViewById(R.id.preModifierTextView);
                TextView preTimeTextView = (TextView) mainActivity.findViewById(R.id.preTimeTextView);
                TextView prePlaceTextView = (TextView) mainActivity.findViewById(R.id.prePlaceTextView);
                if(preEpoch == null) {
                    preModifierTextView.setVisibility(View.GONE);
                    preTimeTextView.setVisibility(View.GONE);
                    prePlaceTextView.setVisibility(View.GONE);
                } else {
                    String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date (Long.parseLong(preEpoch)*1000));
                    preTimeTextView.setText(preTimeTextView.getText() + date);
                    prePlaceTextView.setText(prePlaceTextView.getText() + prePlace);
                    preModifierTextView.setText(preDateAndLocationSetter + " proposed the following time and location.");
                }
				
                // Current time and location section.
				TextView lastModifierTextView = (TextView) mainActivity.findViewById(R.id.lastModifierTextView);
				TextView timeTextView = (TextView) mainActivity.findViewById(R.id.timeTextView);
				TextView placeTextView = (TextView) mainActivity.findViewById(R.id.placeTextView);
				
				String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date (Long.parseLong(epoch)*1000));
				timeTextView.setText(timeTextView.getText() + date);
				placeTextView.setText(placeTextView.getText() + place);
				lastModifierTextView.setText(curDateAndLocationSetter + " proposed the following time and location.");

				// Lock date section.
				if (!lockDate.equals("None")) {
					TextView text = (TextView) mainActivity
							.findViewById(R.id.lockDate);
					text.setText(LOCKED_MESSAGE);
					agreeButton.setEnabled(false);
					Button changeDateButton = (Button) mainActivity.findViewById(R.id.changeDatebutton);
					changeDateButton.setEnabled(false);
				}
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
    			
    			String status = null;
    			try {
    				for(int i = 0; i<resultArray.length(); i++) {
    					
    					JSONObject jsonObj = resultArray.getJSONObject(i);
    					status = jsonObj.getString("status");
    					
    				} 
    			}catch (JSONException e) {
    				e.printStackTrace();
    			}

    			if (status.equals("OK")) {
    			    TextView lock = (TextView)mainActivity.findViewById(R.id.lockDate);
    			    lock.setText(LOCKED_MESSAGE);
    			    Button agreeButton = (Button) mainActivity.findViewById(R.id.surebutton);
    			    agreeButton.setEnabled(false);
                    Button changeDateButton = (Button) mainActivity.findViewById(R.id.changeDatebutton);
                    changeDateButton.setEnabled(false);
    			}
    		}
    	};
    	PostToServerAsyncTask task = new PostToServerAsyncTask(callback);
		task.execute(request);
    }
    
    public void changeDate(View v) {
        Intent intent = new Intent(this, ChangeDateActivity.class);
        intent.putExtra("matchId", matchId);
        startActivity(intent);
    }
}
