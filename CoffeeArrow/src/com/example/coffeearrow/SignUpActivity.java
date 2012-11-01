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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.coffeearrow.domain.Area;
import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;
import com.example.coffeearrow.server.RequestFactory;

public class SignUpActivity extends PortraitActivity implements PostToServerCallback, OnItemSelectedListener {
	
	private ProgressDialog dialog;
	public final static String EMAIL = "com.coffeearrow.signIn.Email";
	public String newUserEmail = null;
	private Spinner areaSpinner;
	private SignUpActivity mainActivity;
	private ArrayList<Area> areasList;
	private Area selectedArea;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mainActivity = this;
        areaSpinner = (Spinner)findViewById(R.id.areaSpinner);
        
        dialog = new ProgressDialog(this);
        dialog.setMessage("Getting areas...");
        dialog.show();
        
        HashMap<String, String> requestParams = new HashMap<String, String>();

        HttpPost request = RequestFactory.create(requestParams,
                "getAreas");

        PostToServerCallback callback = new PostToServerCallback() {
            @Override
            public void callback(JSONObject objResult) {
                if (dialog.isShowing())
                    dialog.dismiss();
                Log.i("SignUpActivity", "Got the following areas:");
                areasList = new ArrayList<Area>();
                ArrayList<String> areasNameList = new ArrayList<String>(); 
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JSONArray resultArray = (JSONArray) objResult.getJSONArray("areas");
                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject jsonObj = resultArray.getJSONObject(i);
                        String record = jsonObj.toString(1);
                        Area area = mapper.readValue(record,
                                Area.class);

                        areasList.add(area);
                        areasNameList.add(area.getDisplay());
                        Log.i("SignUpActivity", area.getId()+":"+area.getDisplay());
                        
                    }
                    
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mainActivity,
                            android.R.layout.simple_spinner_item, areasNameList);
                    areaSpinner.setAdapter(dataAdapter);
                    
                    areaSpinner.setOnItemSelectedListener(mainActivity);
                    selectedArea = areasList.get(0);
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
            }
        };
        PostToServerAsyncTask task = new PostToServerAsyncTask(callback);
        task.execute(request);
    }
	
	@Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos,
            long id) {
        selectedArea = areasList.get(pos);
        Log.i("SignUpActivity", "Selected area: " + selectedArea.getId() + " " + selectedArea.getDisplay());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_sign_up, menu);
        return true;
    }

    public void submit(View view) {
    	dialog.setMessage("Submitting...");
    	dialog.show();
        EditText emailText = (EditText) findViewById(R.id.email);
        String email = emailText.getText().toString();
        newUserEmail = email;
        // Not a all mighty regular expression for email, but not worth it.
        if (!email.matches(".+@.+\\..+")) {
            String message = "Please enter a valid email address.";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
                    .show();
            return;
        }

        EditText passwordText = (EditText) findViewById(R.id.password);
        String password = passwordText.getText().toString();
        if (password.isEmpty()) {
            String message = "Please enter a password.";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
                    .show();
            return;
        }

        EditText confirmPasswordText = (EditText) findViewById(R.id.confirmPassword);
        String confirmPassword = confirmPasswordText.getText().toString();
        if (!password.equals(confirmPassword)) {
            String message = "Password and confirm password do not match.";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
                    .show();
            return;
        }

        EditText nameText = (EditText) findViewById(R.id.name);
        String name = nameText.getText().toString();
        if (name.isEmpty()) {
            String message = "Please enter a name.";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
                    .show();
            return;
        }

        RadioGroup radioGenderGroup = (RadioGroup) findViewById(R.id.radioGender);
        int selectedGender = radioGenderGroup.getCheckedRadioButtonId();
        RadioButton checkedGender = (RadioButton) findViewById(selectedGender);

        HashMap<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("firstName", name.trim());
        requestParams.put("email", email.trim());
        requestParams.put("password", password.trim());
        requestParams.put("areaId", selectedArea.getId());
        requestParams.put("gender", checkedGender.getText().toString());

        HttpPost request = RequestFactory.create(requestParams,
                "submitUserInfoNative");

        PostToServerAsyncTask task = new PostToServerAsyncTask(this);
        task.execute(request);
    }

    @Override
    public void callback(JSONObject objResult) {
    	if (dialog.isShowing())
			dialog.dismiss();
        String status = null;
        try {
            status = objResult.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if ("Failed".equals(status)) {
            String message = "Something went wrong";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                    .show();
        } else if ("AlreadyPresent".equals(status)) {
            String message = "Email already present, please go to sign in page";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                    .show();
        } else {
            Intent intent = new Intent(this, PendingVerificationActivity.class);
            intent.putExtra(EMAIL,newUserEmail);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
        
    }
}
