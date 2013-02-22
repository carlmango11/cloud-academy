package com.carlnolan.cloudacademy.workload;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.asynctasks.DownloadHomeworkDueForRange;
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
import android.widget.ListView;
import android.widget.TextView;

public class WorkloadListFragment extends Fragment
	implements DownloadHomeworkDueForRange.DownloadHomeworkDueForRangeListener,
	Exam.DownloadExamsForRangeListener {
	private WorkloadItemSelectedListener callback;
	
	private ListView list;
	private TextView dueDateView;
	
	private String fullWorkloadString;
	
	private List<Homework> homework;
	private List<Exam> exams;
	
	/**
	 * Homework and Exams are d/l'ed simultaneously so I need to wait until both
	 * are d/l'd. When one downloads it will set this to true. Cos theyre both
	 * running simulaneously
	 */
	private boolean otherDownloaded;
	
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
		
		//list
		list = (ListView) getActivity().findViewById(R.id.workload_list_viewer_list);
		list.setOnItemClickListener(new WorkloadClickListener(getActivity()));
        
        otherDownloaded = false;
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
		//get dates for range of homework we want
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();

		dueDateView.setText(fullWorkloadString);
		
		if(date == null) {
			//get h/w for next 2 weeks
			endDate.add(Calendar.DAY_OF_YEAR, 14);
		} else {			
			//get homework just for the selected day
			startDate = date;
			endDate = date;
		}
		
		//fire off asyncs for exam and h/w downloads
		new DownloadHomeworkDueForRange(this, startDate, endDate).execute();
		/*the exam async is a static method contained within Exam. I think this is a cleaner way
		 * of doing it rather than having a load of random asynctasks hanging around. Will do in future
		 */
		Exam.downloadExamsForRange(this, startDate, endDate);
	}

	/**
	 * Called when homework and exams are finished d/l'ing and we can build the workload list
	 */
	private void buildList() {
		//this is going to seem weird but it should work:
		Map<Calendar, WorkloadListAdapterEntry> items =
				new HashMap<Calendar, WorkloadListAdapterEntry>();
		
		for(Homework h:homework) {
			if(!items.containsKey(h.getDueDate())) {
				//if we dont have an entry for this date create one
				items.put(h.getDueDate(), new WorkloadListAdapterEntry(h.getDueDate()));
			}
			
			//yes the calendar object seems duplicated. pls ignore
			items.get(h.getDueDate()).addHomework(h);
		}
		
		for(Exam e:exams) {
			if(!items.containsKey(e.getDate())) {
				//yes the calendar object seems duplicated. pls ignore
				items.put(e.getDate(), new WorkloadListAdapterEntry(e.getDate()));
			}
			
			items.get(e.getDate()).addExam(e);
		}
		
		//so now we have a lovely map with all our adapterentries, build a list
		List<WorkloadListAdapterEntry> entries =
				new ArrayList<WorkloadListAdapterEntry>(items.values());
		Collections.sort(entries);
		
		//build the list adapter
		WorkloadListAdapter adapter = new WorkloadListAdapter(getActivity(),
				R.layout.workload_list_row, entries);
		list.setAdapter(adapter);
		list.invalidate();
		System.out.println("lists built");
	}

	public void onHomeworkRangeDownloaded(List<Homework> result) {
		homework = result;
		
		if(otherDownloaded) {
			buildList();
		} else {
			otherDownloaded = true;
		}
	}

	public void onExamsForRangeDownloaded(List<Exam> result) {
		exams = result;
		System.out.println("exams:"+result);
		
		if(otherDownloaded) {
			buildList();
		} else {
			otherDownloaded = true;
		}
	}
}
