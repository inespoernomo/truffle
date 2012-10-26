package com.example.coffeearrow;

import java.util.HashMap;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;
import com.example.coffeearrow.server.RequestFactory;

public class EditProfile extends PortraitActivity implements PostToServerCallback {
    private ProgressDialog dialog;
    private String userId;
    private TextView nameText;
    private TextView zipcodeText;
    private String newName;
    private String newZipcode;
    private String newGender;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        
        Intent sourceIntent = getIntent();
        Log.i("EditProfile", String.format("Got the following from source Intent: %s %s %s %s",
                sourceIntent.getStringExtra("userId"),
                sourceIntent.getStringExtra("name"),
                sourceIntent.getStringExtra("gender"),
                sourceIntent.getStringExtra("zip")
                ));
        
        userId = sourceIntent.getStringExtra("userId");
        
        nameText = (TextView)findViewById(R.id.editProfileName);
        zipcodeText = (TextView)findViewById(R.id.editProfileZipcode);
        
        nameText.setText(sourceIntent.getStringExtra("name"));
        zipcodeText.setText(sourceIntent.getStringExtra("zip"));
        
        if (sourceIntent.getStringExtra("gender").equals("Female")) {
            RadioButton female = (RadioButton)findViewById(R.id.editProfileGenderRadio2);
            female.setChecked(true);
        }
        
        dialog = new ProgressDialog(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_profile, menu);
        return true;
    }
    
    public void updateProfile(View view) {
        dialog.setMessage("Updating...");
        dialog.show();

        newName = nameText.getText().toString();
        if (newName.isEmpty()) {
            String message = "Please enter a name.";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
                    .show();
            return;
        } else {
            newName = newName.trim();
        }

        newZipcode = zipcodeText.getText().toString();

        RadioGroup radioGenderGroup = (RadioGroup) findViewById(R.id.editProfileRadioGender);
        int selectedGender = radioGenderGroup.getCheckedRadioButtonId();
        RadioButton checkedGender = (RadioButton) findViewById(selectedGender);
        newGender = checkedGender.getText().toString();

        HashMap<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("userId", userId);
        requestParams.put("name", newName);
        requestParams.put("zipcode", newZipcode);
        requestParams.put("gender", newGender);

        HttpPost request = RequestFactory.create(requestParams,
                "updateUserInfo");

        PostToServerAsyncTask task = new PostToServerAsyncTask(this);
        task.execute(request);
    }
    
    @Override
    public void callback(JSONObject objResult) {
        // Dismiss the progress dialog.
        if (dialog.isShowing())
            dialog.dismiss();
        
        Log.i("EditProfile", "Got back to onPostExecute.");
        Log.i("EditProfile", "The result is: " + objResult);
        
        String status = null;
        try {
            status = objResult.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if ("Failed".equals(status)) {
            String message = "Something went wrong";
            Toast.makeText(getApplicationContext(), message,
                    Toast.LENGTH_SHORT).show();
        } else {
            Intent result = new Intent();
            result.putExtra("newName", newName);
            result.putExtra("newGender", newGender);
            result.putExtra("newZipcode", newZipcode);
            setResult(RESULT_OK, result);
            
            // Go back to the profile activity
            finish();
        }
    }
}
