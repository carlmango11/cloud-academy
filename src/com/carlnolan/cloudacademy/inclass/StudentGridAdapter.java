package com.carlnolan.cloudacademy.inclass;

import java.util.ArrayList;
import java.util.List;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.scheduling.Session;
import com.carlnolan.cloudacademy.usermanagement.Student;

import android.app.Activity;
import android.content.Context;
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

public class StudentGridAdapter extends ArrayAdapter<Student> {
    Context context;
    int layoutResourceId;
    Student data[] = null;
    boolean isSelected[];
    
    private static final int DIMMED_ALPHA_LEVEL = 100;
    private static final int NORMAL_ALPHA_LEVEL = 255;
    
    public StudentGridAdapter(
    		Context context,
    		int layoutResourceId,
    		List<Student> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        
        Student[] studentArray = new Student[data.size()];
        studentArray = data.toArray(studentArray);
        this.data = studentArray;
        
        isSelected = new boolean[this.data.length];
        for(int i=0;i<isSelected.length;i++) {
        	isSelected[i] = false;
        }
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
            holder.name = (TextView)row.findViewById(R.id.student_grid_item_name);
            
            row.setTag(holder);
        } else {
            holder = (StudentHolder)row.getTag();
        }
        
        Student student = data[position];
        holder.name.setText(student.getSurname() + ", " + student.getFirstname());
        holder.name.setId(position);
        
        Drawable pic;
        if(student.getPhoto() != null) {
        	//Users pic:
	        pic = new BitmapDrawable(
	        		context.getResources(),
	        		student.getPhoto());
	    } else {
	    	pic = context.getResources().getDrawable(R.drawable.no_user_pic_available);
	    }
        
        setAlpha(pic, position);
        
        holder.name.setCompoundDrawablesWithIntrinsicBounds(null, pic, null, null);
        holder.name.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
    			int position = v.getId();
    			isSelected[position] = !isSelected[position];
    			
    			//Update GUI
    			TextView tv = (TextView) v;
    			Drawable pic = tv.getCompoundDrawables()[1];
    			setAlpha(pic, position);
    		}
        });
        
        return row;
    }
    
    public void setAllSelected(boolean selectAll) {
    	//If selectAll is true, select everything, otherwise
    	//deselect everything
    	for(int i=0;i<isSelected.length;i++) {
    		isSelected[i] = selectAll;
    	}
    	notifyDataSetChanged();
    }
    
    public List<Student> getAttendanceList() {
    	List<Student> ls = new ArrayList<Student>();
    	for(int i=0;i<data.length;i++) {
    		if(isSelected[i]) {
    			ls.add(data[i]);
    		}
    	}
    	return ls;
    }
    
    private void setAlpha(Drawable pic, int position) {
    	if(isSelected[position]) {
			pic.setAlpha(NORMAL_ALPHA_LEVEL);
		} else {
			pic.setAlpha(DIMMED_ALPHA_LEVEL);
		}
    }

	private static class StudentHolder {
    	TextView name;
    }

	public void setStudentsSelected(List<Integer> studentIds) {
		int searchIndex;
		for(Integer i:studentIds) {
			searchIndex = findStudentWithId(data, i);
			if(searchIndex != -1) {
				isSelected[searchIndex] = true;
			}
		}
    	notifyDataSetChanged();
	}

	private static int findStudentWithId(Student[] list, Integer toFind) {
		for(int i=0;i<list.length;i++) {
			if(list[i].getId() == toFind) {
				return i;
			}
		}
		return -1;
	}
}