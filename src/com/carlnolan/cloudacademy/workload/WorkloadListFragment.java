package com.carlnolan.cloudacademy.workload;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.inclass.Exam;
import com.carlnolan.cloudacademy.inclass.Homework;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class WorkloadListFragment extends Fragment {
	private WorkloadItemSelectedListener callback;
	
	private ExpandableListView list;
	private TextView dueDateView;
	
	private String fullWorkloadString;
	private String specificDatePrefixString;
	
	public interface WorkloadItemSelectedListener {
		public void onExamSelected(Exam exam);
		public void onHomeworkSelected(Homework homework);
	}

	public static WorkloadListFragment newInstance() {
		WorkloadListFragment instance = new WorkloadListFragment();
		
		return instance;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			callback = (WorkloadItemSelectedListener) activity;
		} catch(ClassCastException e) {
			Log.d("carl", "Could not cast class");
			throw new ClassCastException(activity.toString()
					+ " upcoming! must implement " +
					"WorkloadItemSelectedListener");
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		
		dueDateView = (TextView) getActivity().findViewById(R.id.workload_list_viewer_due_date);
		
		//get strings for header:
		fullWorkloadString = getActivity().getString(R.string.workload_list_full_workload);
		specificDatePrefixString = getActivity().getString(R.string.workload_list_specific_date_prefix);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        return inflater.inflate(R.layout.workload_list_viewer, null);
	}

	/**
	 * Called by outside to update the date we should be showing workload for
	 * @param date The date, if null we want all dates
	 */
	public void setDate(Calendar date) {
		if(date == null) {
			dueDateView.setText(fullWorkloadString);
		} else {
			SimpleDateFormat formatter = new SimpleDateFormat("d MMMMMMMM yyyy");
			String dueDateString = formatter.format(date.getTime());
			
			dueDateView.setText(specificDatePrefixString + dueDateString);
		}
		
		//test:
		callback.onHomeworkSelected(null);
	}
}
