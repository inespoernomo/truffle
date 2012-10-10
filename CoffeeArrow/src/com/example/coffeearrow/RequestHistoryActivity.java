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
import android.widget.TextView;

import com.example.coffeearrow.domain.InvitationItem;
import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;
import com.example.coffeearrow.server.RequestFactory;

public class RequestHistoryActivity extends Activity {

	private static final String LOCKED_MESSAGE = "Date accepted :)";
	private RequestHistoryActivity mainActivity = null;
	private String userId;
	private InvitationItem invitation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mainActivity = this;
		Intent intent = getIntent();
		invitation = (InvitationItem)intent.getSerializableExtra("invitationItem");
		
		SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
		userId = settings.getString("userId", null);
		setContentView(R.layout.activity_request_history);

		// Logic to figure out latest and previous date and location
        // setter.
        Button agreeButton = (Button) mainActivity
                .findViewById(R.id.surebutton);
        
        String preDateAndLocationSetter;
        if (invitation.getLatestInitiatorId().equals(userId)) {
            preDateAndLocationSetter = invitation.getName();
            //curDateAndLocationSetter = matchName;
            agreeButton.setEnabled(false);
        } else {
            preDateAndLocationSetter = invitation.getName();
            //curDateAndLocationSetter = matchName;
            agreeButton.setEnabled(true);
        }

        // Previous time and location section
        TextView matchName = (TextView) mainActivity
                .findViewById(R.id.matchName);
        TextView preTimeTextView = (TextView) mainActivity
                .findViewById(R.id.preTimeTextView);
        TextView prePlaceTextView = (TextView) mainActivity
                .findViewById(R.id.prePlaceTextView);
        TextView prePlaceMsgView = (TextView) mainActivity
                .findViewById(R.id.prePlaceMsgView);
        matchName.setText(preDateAndLocationSetter);
        if (invitation.getPrevEpoch() == null) {
            prePlaceMsgView.setVisibility(View.GONE);
            //preModifierTextView.setVisibility(View.GONE);
            preTimeTextView.setVisibility(View.GONE);
            prePlaceTextView.setVisibility(View.GONE);
            
        } else {
            String date = new java.text.SimpleDateFormat(
                    "MM/dd/yyyy HH:mm:ss").format(new java.util.Date(
                    Long.parseLong(invitation.getPrevEpoch()) * 1000));
            preTimeTextView.setText(preTimeTextView.getText() + date);
            prePlaceTextView.setText(prePlaceTextView.getText()
                    + invitation.getPrevPlace());        
        }

        // Current time and location section.
        //TextView lastModifierTextView = (TextView) mainActivity
            //  .findViewById(R.id.lastModifierTextView);
        TextView timeTextView = (TextView) mainActivity
                .findViewById(R.id.timeTextView);
        TextView placeTextView = (TextView) mainActivity
                .findViewById(R.id.placeTextView);

        Log.i("RequestHistoryActivity", "epoch is: "+invitation.getCurrEpoch());
        String date = new java.text.SimpleDateFormat(
                "MM/dd/yyyy HH:mm:ss").format(new java.util.Date(Long
                .parseLong(invitation.getCurrEpoch()) * 1000));
        timeTextView.setText(timeTextView.getText() + date);
        placeTextView.setText(placeTextView.getText() + invitation.getCurrPlace());
        //lastModifierTextView.setText("Update time/place");

        // Lock date section.
        TextView text = (TextView) mainActivity
                .findViewById(R.id.lockDate);
        if (!invitation.getLocked().equals("None")) {
            text.setVisibility(View.VISIBLE);
            text.setText(LOCKED_MESSAGE);
            agreeButton.setEnabled(false);
            Button changeDateButton = (Button) mainActivity
                    .findViewById(R.id.changeDatebutton);
            changeDateButton.setEnabled(false);
        } else {
            text.setVisibility(View.GONE);
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_request_history, menu);
		return true;
	}

	/**
	 * This method confirms the date request from another person. "Locking" the
	 * date.
	 * 
	 * @param view
	 *            - the view object
	 * */
	public void lockTheDate(View view) {
		HashMap<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("matchId", invitation.getMatchId());
		HttpPost request = RequestFactory.create(requestParams,
				"lockTheDateNative");

		PostToServerCallback callback = new PostToServerCallback() {
			public void callback(Object objResult) {
				JSONArray resultArray = (JSONArray) objResult;

				String status = null;
				try {
					for (int i = 0; i < resultArray.length(); i++) {

						JSONObject jsonObj = resultArray.getJSONObject(i);
						status = jsonObj.getString("status");

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				if (status.equals("OK")) {
					TextView lock = (TextView) mainActivity
							.findViewById(R.id.lockDate);
					lock.setText(LOCKED_MESSAGE);
					Button agreeButton = (Button) mainActivity
							.findViewById(R.id.surebutton);
					agreeButton.setEnabled(false);
					Button changeDateButton = (Button) mainActivity
							.findViewById(R.id.changeDatebutton);
					changeDateButton.setEnabled(false);
				}
			}
		};
		PostToServerAsyncTask task = new PostToServerAsyncTask(callback);
		task.execute(request);
	}

	public void changeDate(View v) {
		Intent intent = new Intent(this, ChangeDateActivity.class);
		intent.putExtra("matchId", invitation.getMatchId());
		startActivity(intent);
	}

}
