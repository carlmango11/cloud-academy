package com.carlnolan.cloudacademy.planner;

import java.util.List;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.scheduling.Session;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SessionListAdapter extends ArrayAdapter<Session> {
    Context context; 
    int layoutResourceId;    
    Session data[] = null;
    boolean isTeacher;
    
    public SessionListAdapter(
    		Context context,
    		int layoutResourceId,
    		List<Session> data,
    		boolean isTeacher) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.isTeacher = isTeacher;
        
        Session[] sessionArray = new Session[data.size()];
        sessionArray = data.toArray(sessionArray);
        this.data = sessionArray;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SessionHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new SessionHolder();
            holder.courseName = (TextView)row.findViewById(R.id.session_list_item_course);
            holder.room = (TextView)row.findViewById(R.id.session_list_item_room);
            holder.time = (TextView)row.findViewById(R.id.session_list_item_time);
            holder.teacherOrClass = (TextView)row.findViewById(R.id.session_list_item_teacher_class);
            
            row.setTag(holder);
        } else {
            holder = (SessionHolder)row.getTag();
        }
        
        Session session = data[position];
        holder.courseName.setText(session.getCourseName());
        holder.room.setText(session.getRoom());
        holder.time.setText(session.getStartFinishTimes());
        
        if(isTeacher) {
        	holder.teacherOrClass.setText(session.getClassName());
        } else {
        	holder.teacherOrClass.setText(session.getLeadName());
        }
        
        return row;
    }
    
    private static class SessionHolder {
    	TextView courseName;
    	TextView time;
    	TextView room;
    	TextView teacherOrClass;
    }
}