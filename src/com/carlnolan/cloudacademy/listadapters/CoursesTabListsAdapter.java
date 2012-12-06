package com.carlnolan.cloudacademy.listadapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Checkable;
import android.widget.TextView;

public class CoursesTabListsAdapter extends ArrayAdapter<String>
		implements Checkable {
	private boolean checked;
	
	public CoursesTabListsAdapter(Context context, int textViewResourceId,
			String[] objects) {
		super(context, textViewResourceId, objects);
		checked = false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView defaultView = (TextView) super.getView(position, convertView, parent);
		
		defaultView.setTextSize(13);
		
		return defaultView;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;		
	}

	public void toggle() {
		checked = !checked;		
	}

}