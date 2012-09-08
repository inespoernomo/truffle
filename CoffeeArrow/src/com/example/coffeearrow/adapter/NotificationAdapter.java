package com.example.coffeearrow.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.coffeearrow.R;
import com.example.coffeearrow.domain.NotificationItem;
import com.example.coffeearrow.helpers.ImageLoader;

public class NotificationAdapter extends ArrayAdapter<NotificationItem> {
	
	private Activity activity; 
	private List<NotificationItem> profileList;
	public ImageLoader imageLoader;
	
	public NotificationAdapter(Activity activity, List<NotificationItem> profileList) {
		super(activity, R.layout.activity_notifications, profileList);
		this.profileList = profileList;
		this.activity = activity;
		imageLoader=new ImageLoader(activity);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// Find the profile
		NotificationItem profile = profileList.get(position);
		
		// Get the empty row view from the xml.
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater
				.inflate(R.layout.activity_notifications,
						parent, false);
		
		// Get the size of the display.
		Display display = activity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		
		// We want to display 5 results each page.
		int rowHeight = size.y / 5;
		
		// So we set the height of the row to 1/5 of the display height.
		rowView.setLayoutParams(
				new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, rowHeight));
		
		// This is the profile image and we want it to be square.
		ImageView imageView = (ImageView) rowView
				.findViewById(R.id.icon);
		imageView.setLayoutParams(
				new LinearLayout.LayoutParams(rowHeight, rowHeight));
		
		// Lazy load and cache the image.
		imageLoader.DisplayImage(profile.getProfileImage(), imageView);
		
		// The is the label for name and city.
		TextView textView = (TextView) rowView.findViewById(R.id.label);			
		textView.setText(profile.getName());
		
		return rowView;
	} 
}



