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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.coffeearrow.domain.InvitationItem;
import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;
import com.example.coffeearrow.server.RequestFactory;

public class RequestHistoryActivity extends Activity {

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
		
		displayInvitation();
	}

    public void displayInvitation() {
        TextView initiationTextView = (TextView) findViewById(R.id.invitationInitiation);
	    TextView statusTextView = (TextView) findViewById(R.id.invitationStatus);
	    Button changeDateButton = (Button) findViewById(R.id.changeDateButton);
	    LinearLayout decisionButtons = (LinearLayout) findViewById(R.id.decisionButtons);
	    decisionButtons.setVisibility(View.GONE);
	    Log.i("RequestHistory", "Invitation status: " + invitation.getStatus());
	    
	    if (invitation.getStatus().equals("pending")){
	        changeDateButton.setVisibility(View.VISIBLE);
	        if (invitation.getUserId().equals(userId)) {
	            initiationTextView.setText("You are inviting " + invitation.getName() + " to a date.");
	            statusTextView.setText("Waiting for " + invitation.getName() + " to decide.");
	            changeDateButton.setEnabled(true);
	        } else {
	            initiationTextView.setText(invitation.getName() + " is inviting you to a date.");
	            statusTextView.setText("Waiting for you to decide. You can change time and location if you accept.");
	            decisionButtons.setVisibility(View.VISIBLE);
	            changeDateButton.setEnabled(false);
	        }
	    }
	    else if (invitation.getStatus().equals("accepted")) {
	        if (invitation.getUserId().equals(userId)) {
                initiationTextView.setText("You invited " + invitation.getName() + " to a date.");
                statusTextView.setText(invitation.getName() + " has accepted.");
            } else {
                initiationTextView.setText(invitation.getName() + " invited you to a date.");
                statusTextView.setText("You have accepted.");
            }
	        changeDateButton.setVisibility(View.VISIBLE);
	        changeDateButton.setEnabled(true);
	    }
	    else if (invitation.getStatus().equals("declined")) {
	        if (invitation.getUserId().equals(userId)) {
                initiationTextView.setText("You invited " + invitation.getName() + " to a date.");
                statusTextView.setText(invitation.getName() + " has declined.");
            } else {
                initiationTextView.setText(invitation.getName() + " invited you to a date.");
                statusTextView.setText("You have declined.");
            }
	        changeDateButton.setVisibility(View.GONE);
	    }
	    
	    TextView timeLocationProposer = (TextView) findViewById(R.id.timeLocationProposer);
	    if (invitation.getLatestInitiatorId().equals(userId)) {
	        timeLocationProposer.setText("You proposed the following time and location:");
	    } else {
	        timeLocationProposer.setText(invitation.getName() + " proposed the following time and location:");
	    }
	    
	    String invitationTime = new java.text.SimpleDateFormat(
                "MM/dd/yyyy HH:mm:ss").format(new java.util.Date(Long
                .parseLong(invitation.getCurrEpoch()) * 1000));
	    TextView timeTextView = (TextView) findViewById(R.id.invitationTime);
	    timeTextView.setText("Time: " + invitationTime);
        TextView placeTextView = (TextView) findViewById(R.id.invitationLocation);
		placeTextView.setText("Location: " + invitation.getCurrPlace());
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
	public void acceptInvitation(View view) {
		HashMap<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("matchId", invitation.getMatchId());
		HttpPost request = RequestFactory.create(requestParams,
				"acceptInvitation");

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
					mainActivity.invitation.setStatus("accepted");
					mainActivity.displayInvitation();
				}
			}
		};
		PostToServerAsyncTask task = new PostToServerAsyncTask(callback);
		task.execute(request);
	}
	
    public void declineInvitation(View view) {
        HashMap<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("matchId", invitation.getMatchId());
        HttpPost request = RequestFactory.create(requestParams,
                "declineInvitation");

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
                    mainActivity.invitation.setStatus("declined");
                    mainActivity.displayInvitation();
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
