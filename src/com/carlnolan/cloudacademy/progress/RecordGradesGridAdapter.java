package com.carlnolan.cloudacademy.progress;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.configuration.AcademyProperties;
import com.carlnolan.cloudacademy.scheduling.Session;
import com.carlnolan.cloudacademy.usermanagement.Student;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecordGradesGridAdapter extends ArrayAdapter<Student> {
    Context context;
    int layoutResourceId;
    Student data[] = null;
    boolean isSelected[];
    Map<Integer, Integer> grades;
    private int passMin;
    
    public RecordGradesGridAdapter(
    		Context context,
    		int layoutResourceId,
    		List<Student> data,
    		Map<Integer, Integer> grades) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.grades = grades;
        System.out.println("null??? " + (grades==null));
        
        passMin = AcademyProperties.getInstance().getPassMinimum();
        
        Student[] studentArray = new Student[data.size()];
        studentArray = data.toArray(studentArray);
        this.data = studentArray;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        StudentHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new StudentHolder();
            holder.name = (TextView)row.findViewById(R.id.grades_grid_name);
            holder.grade = (TextView)row.findViewById(R.id.grades_grid_score);
            
            row.setTag(holder);
        } else {
            holder = (StudentHolder)row.getTag();
        }
        
        Student student = data[position];
        holder.name.setText(student.getSurname() + ", " + student.getFirstname());
        holder.name.setId(position);
        
        //Set grade view if an existing grade is available
        if(grades.containsKey(student.getId())) {
        	int grade = grades.get(student.getId());
            holder.grade.setText(grade + "%");
            int gradeColour = Color.GREEN;
            if(grade < passMin) {
            	gradeColour = Color.RED;
            }
    		holder.grade.setTextColor(gradeColour);
        }
        
		//get pic
        Drawable pic;
        if(student.getPhoto() != null) {
        	//Users pic:
	        pic = new BitmapDrawable(
	        		context.getResources(),
	        		student.getPhoto());
	    } else {
	    	pic = context.getResources().getDrawable(R.drawable.no_user_pic_available);
	    }
        
        holder.name.setCompoundDrawablesWithIntrinsicBounds(null, pic, null, null);
        
        return row;
    }

	private static class StudentHolder {
    	TextView name;
    	TextView grade;
    }
}