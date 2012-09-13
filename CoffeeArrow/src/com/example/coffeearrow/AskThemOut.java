package com.example.coffeearrow;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AskThemOut extends Activity implements OnItemSelectedListener {

	private Spinner spinner;
	private Button btnSubmit;
	private static final int MY_DATE_DIALOG_ID = 3;
	private static final int MY_TIME_DIALOG_ID = 4;

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		Toast.makeText(
				parent.getContext(),
				"OnItemSelectedListener : "
						+ parent.getItemAtPosition(pos).toString(),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ask_them_out);
		 addListenerOnSpinnerItemSelection();
		 TextView chooseDateView = (TextView) findViewById(R.id.chooseDate);
		 
		 Calendar cal = Calendar.getInstance(TimeZone.getDefault());
	     CharSequence strDate = DateFormat.format("MMMM dd, yyyy", cal.getTime());
	     chooseDateView.append("   " + strDate);
		 chooseDateView.setOnClickListener(new View.OnClickListener() {

		        @Override
		        public void onClick(View v) {
		        	showDialog(MY_DATE_DIALOG_ID);

		        }
		    });
			
			TextView chooseTimeView = (TextView) findViewById(R.id.chooseTime);
			CharSequence strTime = DateFormat.format("HH:mm:ss", cal.getTime());
			chooseTimeView.append("   " + strTime);
			chooseTimeView.setOnClickListener(new View.OnClickListener() {

		        @Override
		        public void onClick(View v) {
		        	showDialog(MY_TIME_DIALOG_ID);

		        }
		    });
		  
	}
	
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case MY_DATE_DIALOG_ID:
		    DatePickerDialog dateDlg = new DatePickerDialog(this, 
		         new DatePickerDialog.OnDateSetListener() {
		          
		    	@Override
		         public void onDateSet(DatePicker view, int year,
		                                             int monthOfYear, int dayOfMonth) 
		         {
		                    Time chosenDate = new Time();        
		                    chosenDate.set(dayOfMonth, monthOfYear, year);
		                    long dtDob = chosenDate.toMillis(true);
		                    CharSequence strDate = DateFormat.format("MMMM dd, yyyy", dtDob);
		                    TextView chooseDateView = (TextView) findViewById(R.id.chooseDate);
		                    chooseDateView.append(strDate);
		                    //Toast.makeText(getApplicationContext(), 
		                      //   "Date picked: " + strDate, Toast.LENGTH_SHORT).show();
		        }}, 2011,0, 1);
		           
		    dateDlg.setMessage("Pick a date");
		      return dateDlg;
		   
		case MY_TIME_DIALOG_ID:
		    TimePickerDialog timeDlg = new TimePickerDialog(this, 
		  
		         new TimePickerDialog.OnTimeSetListener() {
					
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						 Time chosenTime = new Time();
						 final Calendar cal = Calendar.getInstance();
					        int month = cal.get(Calendar.MONTH);
					        int year = cal.get(Calendar.YEAR);
					        int monthDay = cal.get(Calendar.DAY_OF_MONTH);
						    chosenTime.set(1, minute, hourOfDay, monthDay, month, year);
						    
		                    long dtDob = chosenTime.toMillis(true);
		                    CharSequence strDate = DateFormat.format("MMMM dd, yyyy", dtDob);
		                    Toast.makeText(getApplicationContext(), 
		                         "Time picked: " + chosenTime, Toast.LENGTH_SHORT).show();
						
					}},6, 6, true); 
		           
		    timeDlg.setMessage("Pick a time");
		      return timeDlg;
		    
		}
		
		
		return null;
	}
	
	public void onDateDialogButtonClick(View v) {
	     showDialog(MY_DATE_DIALOG_ID);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
	    switch (id) {
	    case MY_DATE_DIALOG_ID:
	        DatePickerDialog dateDlg = (DatePickerDialog) dialog;
	        int iDay,iMonth,iYear;
	    
	        Calendar cal = Calendar.getInstance();
	        iDay = cal.get(Calendar.DAY_OF_MONTH);
	        iMonth = cal.get(Calendar.MONTH);
	        iYear = cal.get(Calendar.YEAR);
	        dateDlg.updateDate(iYear, iMonth, iDay);
	         
	        break;
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_ask_them_out, menu);
		return true;
	}

	public void addListenerOnSpinnerItemSelection() {
		spinner = (Spinner) findViewById(R.id.spinner);
		spinner.setOnItemSelectedListener(this);
	}

	public void addListenerOnButton() {
		spinner = (Spinner) findViewById(R.id.spinner);
	}
	
	
	
	public void chooseDate(View view) {
		showDialog(MY_DATE_DIALOG_ID);
	}
	
	public void chooseTime(View view) {
		showDialog(MY_TIME_DIALOG_ID);
	}
}
