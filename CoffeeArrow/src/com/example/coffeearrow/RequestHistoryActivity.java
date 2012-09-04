package com.example.coffeearrow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.http.client.methods.HttpPost;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.coffeearrow.adapter.DatesAdapter;
import com.example.coffeearrow.domain.DateItem;
import com.example.coffeearrow.server.RequestFactory;
import com.example.coffeearrow.server.ServerInterface;

public class RequestHistoryActivity extends ListActivity {

	private static final String URL = "http://coffeearrow.com/";
	
	private class RequestHistory extends AsyncTask<HttpPost, Integer, Object> {
	
		public RequestHistory() {
			super();
			
		}
		
		@Override
		protected Object doInBackground(HttpPost... params) {
			
			return ServerInterface.executeHttpRequest(params[0]);
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
			
			DatesAdapter adapter = 	
					new DatesAdapter(RequestHistoryActivity.this, responseList);
            RequestHistoryActivity.this.setListAdapter(adapter);
            
            
            
		}
		
		
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();        
        
        HashMap<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("matchId", intent.getStringExtra("matchId"));

		HttpPost request = RequestFactory.create(URL, requestParams, "getNotificationsForMatchNative");	
		Intent destIntent = new Intent(this, NotificationsActivity.class);
		destIntent.putExtra("lockedDate", intent.getStringExtra("lockedDate"));
		destIntent.putExtra("dateName", intent.getStringExtra("dateName"));
		destIntent.putExtra("showSure", intent.getStringExtra("showSure"));
		
		
		RequestHistory history = new RequestHistory();
        history.execute(request);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_request_history, menu);
        return true;
    }
    
    public void newTime(View view) {
    	DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }
    
    public static class TimePickerFragment extends DialogFragment
    implements TimePickerDialog.OnTimeSetListener {

    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		// Use the current time as the default values for the picker
    		final Calendar c = Calendar.getInstance();
    		int hour = c.get(Calendar.HOUR_OF_DAY);
    		int minute = c.get(Calendar.MINUTE);

    		// Create a new instance of TimePickerDialog and return it
    		return new TimePickerDialog(getActivity(), this, hour, minute,
    				DateFormat.is24HourFormat(getActivity()));
    	}


		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Toast.makeText(getActivity(),
					"Hour: " + String.valueOf(hourOfDay) + "Minute: " + String.valueOf(minute) , 
					Toast.LENGTH_LONG).show();
			
		}
    }
    
}
