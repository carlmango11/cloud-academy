package com.carlnolan.cloudacademy.workload;

import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.inclass.ConfirmCompletionDialog;
import com.carlnolan.cloudacademy.inclass.Exam;
import com.carlnolan.cloudacademy.inclass.Homework;

public class WorkloadListAdapter extends ArrayAdapter<WorkloadListAdapterEntry>
	implements ConfirmCompletionDialog.HomeworkCompleteDialogCallback,
	Homework.UpdateHomeworkCompletionCallback {
	private List<WorkloadListAdapterEntry> entries;
	private Context context;
	private WorkloadListFragment.WorkloadItemSelectedListener listener;
    private int resourceId;
    private FragmentManager fragMan;
    
    private static final int INCOMPLETE_MARKER = R.drawable.homework_incomplete_marker;
    private static final int COMPLETE_MARKER = R.drawable.homework_complete_marker;
    
	public WorkloadListAdapter(Context context,
			WorkloadListFragment.WorkloadItemSelectedListener l,
			int resourceId,
			List<WorkloadListAdapterEntry> entries,
			FragmentManager fm) {
		
		super(context, resourceId, entries);
		
		this.entries = entries;
		this.context = context;
		this.listener = l;
		this.resourceId = resourceId;
		fragMan = fm;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		
	    final WorkloadListItemHolder holder;
	    if(convertView == null) {
	        // setup holder
	        holder = new WorkloadListItemHolder();
	        
	        //get the views:
	        convertView = inflater.inflate(R.layout.workload_list_row, null);
	        holder.dropdown = (LinearLayout) convertView.findViewById(R.id.workload_list_row_dropdown);
	        holder.dateTextView = (TextView) convertView.findViewById(R.id.workload_list_date);
	        holder.header = (LinearLayout) convertView.findViewById(R.id.workload_list_date_header);
	        
	        convertView.setTag(holder);
	    } else {
	        // get existing row view
	        holder = (WorkloadListItemHolder) convertView.getTag();
	    }
	    
	    //clear the list:
	    holder.dropdown.removeAllViews();
	    
	    //set the values
	    WorkloadListAdapterEntry thisEntry = entries.get(position);
	    List<Homework> homework = thisEntry.getHomework();
	    List<Exam> exams = thisEntry.getExams();
	    
	    //set the date:
	    //format first:
	    SimpleDateFormat formatter = new SimpleDateFormat("d MMMMMMMM yyyy");
		String dateString = formatter.format(thisEntry.getDate().getTime());
	    holder.dateTextView.setText(dateString);
	    
	    //add the homework
	    for(Homework h:homework) {
	    	//inflate the views, find views
	    	RelativeLayout homeworkLayout = (RelativeLayout) inflater.inflate(R.layout.workload_list_homework_item, null);
	    	TextView homeworkName = (TextView) homeworkLayout.findViewById(R.id.workload_list_homework_name);
	    	TextView homeworkSubject = (TextView) homeworkLayout.findViewById(R.id.workload_list_subject_name);
	    	ImageView homeworkMarker = (ImageView) homeworkLayout.findViewById(R.id.workload_list_completed_marker);
	    	
	    	//set values:
	    	homeworkName.setText(h.toString());
	    	homeworkSubject.setText(h.getCourseName());
	    	
	    	//set marker to red if incomplete:
	    	if(!h.isComplete()) {
	    		homeworkMarker.setImageDrawable(
	    				context.getResources().getDrawable(
	    						INCOMPLETE_MARKER));
	    	}
	    	
	    	//set onclick
	    	final Homework selectedHomework = h;
	    	homeworkLayout.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					listener.onHomeworkSelected(selectedHomework);
				}
	    	});
	    	
	    	//set onLongClick
	    	final ConfirmCompletionDialog.HomeworkCompleteDialogCallback callback =
					this;
	    	homeworkLayout.setOnLongClickListener(new OnLongClickListener() {
				public boolean onLongClick(View v) {
					//Show dialog asking if homework is finished:
					ConfirmCompletionDialog confirmDialog
						= ConfirmCompletionDialog.newInstance(selectedHomework, callback);
					confirmDialog.show(fragMan, "CONFIRM_COMPLETION_DIALOG");
					
					return true;
				}
	    	});
	    	
	    	//add to the linearlayout
	    	holder.dropdown.addView(homeworkLayout);
	    }
	    
	    for(Exam e:exams) {
	    	//inflate the views, find views
	    	RelativeLayout examLayout = (RelativeLayout) inflater.inflate(R.layout.workload_list_exam_item, null);
	    	TextView examName = (TextView) examLayout.findViewById(R.id.workload_list_exam_name);
	    	TextView examSubject = (TextView) examLayout.findViewById(R.id.workload_list_exam_subject_name);
	    	
	    	//set values:
	    	examName.setText(e.toString());
	    	examSubject.setText(e.getCourseName());
	    	
	    	//set onclick
	    	final Exam selectedExam = e;
	    	examLayout.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					listener.onExamSelected(selectedExam);
				}
	    	});
	    	
	    	//add to the linearlayout
	    	holder.dropdown.addView(examLayout);
	    }
	    
	    //set the visiblity
	    int visibility = thisEntry.isExpanded() ? View.VISIBLE : View.GONE;
	    holder.dropdown.setVisibility(visibility);
	    
	    return convertView;
	}

	public List<WorkloadListAdapterEntry> getEntries() {
		return entries;
	}
	
	public static class WorkloadListItemHolder {
		LinearLayout header;
		TextView dateTextView;
		LinearLayout dropdown;
	}

	/**
	 * Called when the ConfirmCompletion dialog returns
	 */
	public void onHomeworkCompleteDialogResponse(Homework updatedHomework, boolean complete) {
		updatedHomework.setIsCompleteAndUpdate(complete, this);
	}

	/**
	 * Called when the homework completion has been updated in the db
	 */
	public void homeworkCompletionUpdated() {
		notifyDataSetChanged();
	}
}
