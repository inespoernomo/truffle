package com.example.coffeearrow;

import java.util.GregorianCalendar;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;
import com.example.coffeearrow.server.RequestFactory;

public class ChangeDateActivity extends Activity implements
		OnItemSelectedListener, PostToServerCallback {

	protected ChangeDateActivity mainActivity;
	protected String matchId;
	private String userId;
	private String place;
	private String epoch;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_date);
		/*DatePicker picker;
		ViewGroup childpicker;
		childpicker = (ViewGroup) findViewById(Resources.getSystem()
				.getIdentifier("month"  rest is: day, year , "id",
						"android"));
		EditText textview = (EditText)  childpicker.findViewById(Resources.getSystem().getIdentifier("timepicker_input", "id",  "android"));

		textview.setTextColor(Color.GREEN);*/
		mainActivity = this;
		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		spinner.setOnItemSelectedListener(this);
		Intent intent = getIntent();
		matchId = intent.getStringExtra("matchId");
		SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
		userId = settings.getString("userId", null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_change_date, menu);
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		place = parent.getItemAtPosition(pos).toString();
		Log.i("ChangeDateActivity", "onItemSelected called: " + place);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	public void submit(View view) {
		Log.i("ChangeDateActivity", "Submit called.");
		Log.i("ChangeDateActivity", "matchId is: " + matchId);

		DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker1);
		TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker1);

		int year = datePicker.getYear();
		int month = datePicker.getMonth();
		int day = datePicker.getDayOfMonth();
		int hour = timePicker.getCurrentHour();
		int minute = timePicker.getCurrentMinute();

		GregorianCalendar cal = new GregorianCalendar(year, month, day);
		int dayOfWeek = cal.get(GregorianCalendar.DAY_OF_WEEK);
		Log.i("ChangeDateActivity", "Year: " + year + " Month: " + month
				+ " Day of Month: " + day + " Day of Week: " + dayOfWeek
				+ "Hour: " + hour + " Minute: " + minute);

		epoch = String
				.valueOf(datePicker.getCalendarView().getDate() / 1000);
		Log.i("ChangeDateActivity", "Epoch is: " + epoch);

		HashMap<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("matchId", matchId);
		requestParams.put("epoch", epoch);
		requestParams.put("place", place);
		requestParams.put("userId", userId);
		HttpPost request = RequestFactory.create(requestParams, "saveDate");
		PostToServerAsyncTask task = new PostToServerAsyncTask(this);
		task.execute(request);
	}

	@Override
	public void callback(Object result) {
	    Log.i("ChangeDateActivity", "change date called back");
		JSONArray resultArray = (JSONArray) result;
		String status = null;
		try {
			for (int i = 0; i < resultArray.length(); i++) {
				JSONObject record = resultArray.getJSONObject(i);

				status = record.getString("status");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (status.equals("ok")) {
		    Log.i("ChangeDateActivity", "status ok");
		    Intent resultIntent = new Intent();
            resultIntent.putExtra("epoch", epoch);
            resultIntent.putExtra("place", place);
            setResult(RESULT_OK, resultIntent);
            
			finish();
		} else {
			Log.i("ChangeDateActivity", "ChangeDateActivity failed.");
		}
	}
}
