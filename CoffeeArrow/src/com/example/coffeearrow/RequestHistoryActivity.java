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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.coffeearrow.domain.InvitationItem;
import com.example.coffeearrow.helpers.ImageLoader;
import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;
import com.example.coffeearrow.server.RequestFactory;

public class RequestHistoryActivity extends Activity {

	private RequestHistoryActivity mainActivity = null;
	private ImageLoader imageLoader;
	private String userId;
	private InvitationItem invitation;
	private boolean comesFromProfile;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mainActivity = this;
		imageLoader = new ImageLoader(this);
		Intent intent = getIntent();
		invitation = (InvitationItem)intent.getSerializableExtra("invitationItem");
		comesFromProfile = intent.getBooleanExtra("comesFromProfile", false);
		
		SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
		userId = settings.getString("userId", null);
		setContentView(R.layout.activity_request_history);
		
		displayInvitation();
	}

    public void displayInvitation() {
        ImageView profileImage = (ImageView) findViewById(R.id.profileImage);
        imageLoader.DisplayImage(invitation.getProfileImage(), profileImage);
        
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
	            changeDateButton.setVisibility(View.VISIBLE);
	        } else {
	            initiationTextView.setText(invitation.getName() + " is inviting you to a date.");
	            statusTextView.setText("Waiting for you to decide. You can change time and location if you accept.");
	            decisionButtons.setVisibility(View.VISIBLE);
	            changeDateButton.setVisibility(View.GONE);
	        }
	    }
	    else if (invitation.getStatus().equals("accepted")) {
	        if (invitation.getUserId().equals(userId)) {
                initiationTextView.setText("You invited " + invitation.getName() + " to a date.");
                statusTextView.setText(invitation.getName() + " has accepted your invitation.");
            } else {
                initiationTextView.setText(invitation.getName() + " invited you to a date.");
                statusTextView.setText("You have accepted the invitation.");
            }
	        changeDateButton.setVisibility(View.VISIBLE);
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

        if (invitation.getPreEpoch() != null && !invitation.getPreEpoch().isEmpty()) {
            LinearLayout preSection = (LinearLayout) findViewById(R.id.preSection);
            preSection.setVisibility(View.VISIBLE);
            
            TextView preTimeLocationProposer = (TextView) findViewById(R.id.preTimeLocationProposer);
            if (invitation.getLatestInitiatorId().equals(userId)) {
                preTimeLocationProposer.setText(invitation.getName() + " proposed the following time and location:");
            } else {
                preTimeLocationProposer.setText("You proposed the following time and location:");
            }
            String preInvitationTime = new java.text.SimpleDateFormat(
                    "MM/dd/yyyy HH:mm:ss").format(new java.util.Date(Long
                    .parseLong(invitation.getCurEpoch()) * 1000));
            TextView preTimeTextView = (TextView) findViewById(R.id.preInvitationTime);
            preTimeTextView.setText("Time: " + preInvitationTime);
            TextView prePlaceTextView = (TextView) findViewById(R.id.preInvitationLocation);
            prePlaceTextView.setText("Location: " + invitation.getCurPlace());
        }
	    
	    TextView curTimeLocationProposer = (TextView) findViewById(R.id.curTimeLocationProposer);

	    if (invitation.getLatestInitiatorId().equals(userId)) {
	        curTimeLocationProposer.setText("You proposed the following time and location:");
	    } else {
	        curTimeLocationProposer.setText(invitation.getName() + " proposed the following time and location:");
	    }
	    
	    String curInvitationTime = new java.text.SimpleDateFormat(
                "MM/dd/yyyy HH:mm:ss").format(new java.util.Date(Long
                .parseLong(invitation.getCurEpoch()) * 1000));
	    TextView curTimeTextView = (TextView) findViewById(R.id.curInvitationTime);
	    curTimeTextView.setText("Time: " + curInvitationTime);
        TextView curPlaceTextView = (TextView) findViewById(R.id.curInvitationLocation);
		curPlaceTextView.setText("Location: " + invitation.getCurPlace());
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
	
	public void goToProfile(View v) {
	    Log.i("RequestHistoryActivity", "Profile image clicked.");

	    if (comesFromProfile) {
	        finish();
	    }
	}

}
