package com.example.coffeearrow.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.coffeearrow.R;
import com.example.coffeearrow.domain.SearchProfile;
import com.example.coffeearrow.helpers.ImageLoader;

/**
 * This is the adapter for the DisplaySearchResultsActivity, which is a
 * ListActiviy. Given it the profile list, with just image url, not converted to
 * bitmap, it can easy load each picture and cache them when getView is called.
 * 
 * @author sunshi
 * 
 */
public class UserRowsAdapter extends ArrayAdapter<SearchProfile> {

	private ArrayList<SearchProfile> profileList;
	private Activity activity;
	private ImageLoader imageLoader;
	private int imageSize;

	public UserRowsAdapter(Activity activity,
			ArrayList<SearchProfile> profileList) {
		super(activity, R.layout.activity_display_search_results, profileList);
		this.profileList = profileList;
		this.activity = activity;
		imageLoader = new ImageLoader(activity);
		
		// Get the size of the display.
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        imageSize = size.x;
        Log.i("UserRowsAdapter", "Screen size is: " + imageSize);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    Log.i("UserRowsAdapter", "getView image size is: " + imageSize);
		// Find the profile
		SearchProfile profile = profileList.get(position);

		// Get the row view from the xml.
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(
				R.layout.activity_display_search_results, parent, false);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imageSize, imageSize);
		params.setMargins(2, 2, 2, 2);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		RelativeLayout imageFrame = (RelativeLayout) rowView.findViewById(R.id.imageFrame);
		imageFrame.setLayoutParams(params);
		
		ImageView image = (ImageView) rowView.findViewById(R.id.icon);
		imageLoader.DisplayImage(profile.getProfileImage(), image);
		
		TextView text = (TextView) rowView.findViewById(R.id.nameOnImage);
		text.setText(profile.toString());

		return rowView;
	}
}
