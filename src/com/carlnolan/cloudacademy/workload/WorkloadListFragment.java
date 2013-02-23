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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class WorkloadListFragment extends Fragment
	implements DownloadHomeworkDueForRange.DownloadHomeworkDueForRangeListener,
	Exam.DownloadExamsForRangeListener {
	private boolean isClone;
	private WorkloadItemSelectedListener callback;
	
	private ListView list;
	private TextView dueDateView;
	private List<Homework> homework;
	private List<Exam> exams;
	
	private int listPos;
	private int listTop;	
	private String fullWorkloadString;
	
	/**
	 * Homework and Exams are d/l'ed simultaneously so I need to wait until both
	 * are d/l'd. When one downloads it will set this to true. Cos they're both
	 * running simultaneously
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

	public static WorkloadListFragment newInstance(int savedPos, int savedTop) {
		WorkloadListFragment instance = new WorkloadListFragment();
		
		Bundle args = new Bundle();
		args.putInt("LIST_POS", savedPos);
		args.putInt("LIST_TOP", savedTop);
		instance.setArguments(args);
		
		return instance;
	}

	/**
	 * Used to build an identical instance of an existing workloadlistfragment
	 * Used to get around a bug in android where you cant move a fragment to 
	 * a different container, you have to create a new one
	 * @param workloadList
	 * @return
	 */
	public WorkloadListFragment cloneInstance() {		
		int savedPosition = list.getFirstVisiblePosition();
	    View firstVisibleView = list.getChildAt(0);
	    int savedListTop = (firstVisibleView == null) ? 0 : firstVisibleView.getTop();
	    
		WorkloadListFragment instance = WorkloadListFragment.newInstance(savedPosition, savedListTop);
		
		instance.callback = callback;
		instance.list = list;
		instance.dueDateView = dueDateView;
		instance.fullWorkloadString = fullWorkloadString;
		instance.homework = homework;
		instance.exams = exams;
		instance.otherDownloaded = otherDownloaded;
		instance.isClone = true;
		
		return instance;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if(args != null) {
	        listPos = args.getInt("LIST_POS");
	        listTop = args.getInt("LIST_TOP");
        }
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
		
		//get strings for header:
		fullWorkloadString = getActivity().getString(R.string.workload_list_full_workload);
		
		dueDateView = (TextView) getActivity().findViewById(R.id.workload_list_viewer_due_date);
		dueDateView.setText(fullWorkloadString);

		//If this fragment is a clone of a previous one we need to save the listadapter then restore it
		ListAdapter savedAdapter = null;
		if(isClone) {
			savedAdapter = list.getAdapter();
		}
		list = (ListView) getActivity().findViewById(R.id.workload_list_viewer_list);
		list.setOnItemClickListener(new WorkloadClickListener(getActivity()));
		
		if(isClone) {
			list.setAdapter(savedAdapter);
			list.invalidate();
			
			//Set the saved position
			if(listPos >= 0) {
	        	System.out.println("listTop");
	        	list.setSelectionFromTop(listPos, listTop);
	        }
		}
        
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
		
		if(date == null) {
			//get h/w for next 2 weeks
			endDate.add(Calendar.DAY_OF_YEAR, 14);
		} else {
			//get homework just for the selected day
			startDate = date;
			endDate = date;
		}
		System.out.println(startDate.getTime());
		System.out.println(endDate.getTime());
		
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
				callback, R.layout.workload_list_row, entries);
		list.setAdapter(adapter);
		list.invalidate();
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
