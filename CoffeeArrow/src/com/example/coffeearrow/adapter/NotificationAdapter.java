package com.example.coffeearrow.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.coffeearrow.R;
import com.example.coffeearrow.domain.NotificationItem;

public class NotificationAdapter extends ArrayAdapter<NotificationItem> {
	
	private Context context; 
	private List<NotificationItem> items;
	public NotificationAdapter(Context context, List<NotificationItem> items) {
		super(context, R.layout.activity_notifications, items);
		this.items = items;
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.activity_notifications, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		textView.setText(items.get(position).getName());
		return rowView;
	} 
}



