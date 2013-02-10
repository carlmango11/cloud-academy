package com.carlnolan.cloudacademy.coursebrowser;

import java.util.ArrayList;
import java.util.List;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.courses.Course;
import com.carlnolan.cloudacademy.scheduling.Session;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CourseBrowserCourseAdapter extends ArrayAdapter<Course> {
	private List<Course> courses;
	private int layoutResourceId;
	private Context context;
	
	public CourseBrowserCourseAdapter(Context context, int resource,
			List<Course> theseCourses) {
		super(context, resource, theseCourses);
		this.context = context;
		layoutResourceId = resource;
		courses = theseCourses;
	}

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CourseHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new CourseHolder();
            holder.name = (TextView)row.findViewById(R.id.course_browser_course_item_name);
            holder.teacher = (TextView)row.findViewById(R.id.course_browser_course_item_teacher);
            
            row.setTag(holder);
        } else {
            holder = (CourseHolder)row.getTag();
        }
        
        Course course = courses.get(position);
        holder.name.setText(course.toString());
        holder.teacher.setText(course.getOwner());
        
        return row;
    }
	
	private class CourseHolder {
		TextView name;
		TextView teacher;
	}
}
