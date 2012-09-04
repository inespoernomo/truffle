package com.example.coffeearrow.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.coffeearrow.R;
import com.example.coffeearrow.domain.DateItem;

public class DatesAdapter extends ArrayAdapter<DateItem> {
	
	private Context context; 
	private List<DateItem> items;
	public DatesAdapter(Context context, List<DateItem> items) {
		super(context, R.layout.activity_request_history, items);
		this.items = items;
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.activity_request_history, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		textView.setText(items.get(position).getTime());
		return rowView;
	} 
}



