package com.carlnolan.cloudacademy.listadapters;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.scheduling.Session;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CourseListAdapter extends ArrayAdapter<Session>{

    Context context; 
    int layoutResourceId;    
    Session data[] = null;
    
    public CourseListAdapter(Context context, int layoutResourceId, Session[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        UpcomingClassHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new UpcomingClassHolder();
            holder.courseName = (TextView)row.findViewById(R.id.session_list_item_course);
            holder.room = (TextView)row.findViewById(R.id.session_list_item_room);
            holder.time = (TextView)row.findViewById(R.id.session_list_item_time);
            
            row.setTag(holder);
        } else {
            holder = (UpcomingClassHolder)row.getTag();
        }
        
        Session session = data[position];
        holder.courseName.setText(session.getCourseName());
        holder.room.setText(session.getRoom());
        holder.time.setText(session.getStartsNice());
        
        return row;
    }
    
    private static class UpcomingClassHolder
    {
    	TextView courseName;
    	TextView room;
    	TextView time;
    }
}