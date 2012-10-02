package com.example.coffeearrow.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
	public ImageLoader imageLoader;

	public UserRowsAdapter(Activity activity,
			ArrayList<SearchProfile> profileList) {
		super(activity, R.layout.activity_display_search_results, profileList);
		this.profileList = profileList;
		this.activity = activity;
		imageLoader = new ImageLoader(activity);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Find the profile
		SearchProfile profile = profileList.get(position);

		// Get the empty row view from the xml.
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(
				R.layout.activity_display_search_results, parent, false);

		// Get the size of the display.
		Display display = activity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		// size 480*300 is pretty good for all the android phones
		int rowHeight = 480;
		int rowWidth = 300;
		// So we set the height of the row to 1/5 of the display height.
		rowView.setLayoutParams(new AbsListView.LayoutParams(rowWidth, rowHeight-150));

		// This is the profile image and we want it to be square.
		// RelativeLayout rLayout = new RelativeLayout(activity);
		// LayoutParams rlParams = new LayoutParams(LayoutParams.FILL_PARENT
		// ,LayoutParams.FILL_PARENT);
		// rLayout.setLayoutParams(rlParams);

		ImageView image = new ImageView(activity);
		image.setScaleType(ImageView.ScaleType.CENTER);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				rowWidth - 5, LinearLayout.LayoutParams.MATCH_PARENT);

		image.setLayoutParams(layoutParams);
		LinearLayout linearLayout = (LinearLayout) rowView
				.findViewById(R.id.container);
		
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(rowWidth,
				rowHeight-150));
		// LinearLayout imageLayout = new LinearLayout(activity);
		// Lazy load and cache the image.
		imageLoader.DisplayImage(profile.getProfileImage(), image);
		linearLayout.setBackgroundDrawable(image.getDrawable());
		//linearLayout.addView(image);
		/*
		 * RelativeLayout.LayoutParams tParams = new RelativeLayout.LayoutParams
		 * (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		 * tParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
		 * RelativeLayout.TRUE);
		 * tParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
		 * RelativeLayout.TRUE);
		 */
		LinearLayout.LayoutParams introText = new LinearLayout.LayoutParams(
				AbsListView.LayoutParams.MATCH_PARENT, 70);
		LinearLayout textLayout = (LinearLayout) linearLayout
				.findViewById(R.id.textContainer);
		textLayout.setLayoutParams(introText);
		TextView text = new TextView(activity);
		//text.setLayoutParams(introText);
		//text.setTextAppearance(activity, R.style.transparency);
		text.setText(profile.toString());
		text.setTextColor(Color.WHITE);
		text.setTypeface(Typeface.SANS_SERIF);
		// text.setLayoutParams(tParams);
		textLayout.addView(text);
		//linearLayout.addView(textLayout);

		// The is the label for name and city.
		// TextView textView = (TextView) rowView.findViewById(R.id.label);
		// textView.setText(profile.toString());

		return rowView;
	}
}
