package com.yangli907.newandroid;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessageArrayAdapter extends ArrayAdapter<Response> {
	private TextView messageField;
	private List<Response> messages = new ArrayList<Response>();
	private LinearLayout wrapper;
	
	public MessageArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	@Override
	public void add(Response object) {
		messages.add(object);
		StringBuilder messageArray = new StringBuilder();
		for(Response r:messages){
			messageArray.append(r.getMessage());
			messageArray.append(",");
		}
		Log.i("messages:",messageArray.toString());
		super.add(object);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.messages.size();
	}

	@Override
	public Response getItem(int position) {
		// TODO Auto-generated method stub
		return this.messages.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if(row==null){
			LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.listitem, parent,false);
		}
		wrapper = (LinearLayout) row.findViewById(R.id.wrapper);
		//Response response = getItem(getCount()==0?position:(getCount()-1));
		Response response = getItem(position);
		wrapper.setGravity(response.isLeft()?Gravity.LEFT:Gravity.RIGHT);
		//Log.i("Response",response.getMessage());
		messageField = (TextView)row.findViewById(R.id.messageBody);
		messageField.setBackgroundResource(response.isLeft()?R.drawable.bubble_green:R.drawable.bubble_yellow);
		messageField.setText(response.getMessage());
		
		return row;
	}

}