package com.carlnolan.cloudacademy.courses;

import java.util.ArrayList;
import java.util.Calendar;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.listadapters.CourseListAdapter;
import com.carlnolan.cloudacademy.listadapters.CoursesTabListsAdapter;
import com.carlnolan.cloudacademy.scheduling.Session;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;
import com.carlnolan.cloudacademy.asynctasks.DownloadSections;
import com.carlnolan.cloudacademy.R;

import android.app.Activity;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class SectionListFragment extends ListFragment
		implements DownloadSections.DownloadSectionsResponder {

	private OnSectionSelectedListener callback;
	private ArrayList<Section> allSections;
	private Course thisCourse;

    public interface OnSectionSelectedListener {
        public void onSectionSelected(Section section);
    }
    
    void updateList(ArrayList<Section> newSections) {
    	String [] sections = new String[newSections.size()];
    	
    	for(int i=0; i<newSections.size(); i++) {
    		sections[i] = newSections.get(i).toString();
    	}
        
        setListAdapter(
        		new ArrayAdapter<String>(
        				getActivity(),
        				android.R.layout.simple_list_item_activated_1,
        				sections
        		)
        );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        String [] lsa = new String[] {"Select Course"};
        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, lsa));
    }

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		callback.onSectionSelected(allSections.get(position));
		getListView().setItemChecked(position, true);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			callback = (OnSectionSelectedListener) activity;
		} catch(ClassCastException e) {
			Log.d("carl", "Could not cast class");
			throw new ClassCastException(activity.toString()
					+ " upcoming! must implement SetionListFragment.OnSectionSelectedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View defaultView = super.onCreateView(inflater, container, savedInstanceState);
		LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT);
		p1.weight = 0.33f;
		defaultView.setLayoutParams(p1);
		return defaultView;
	}

	@Override
	public void onStart() {
		super.onStart();
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	public void loadSessions(Course course) {
		thisCourse = course;
        new DownloadSections(this, course.getId()).execute();
	}

	public void onDownloadSectionsComplete(ArrayList<Section> sections, int courseId) {
		allSections = sections;
		updateList(sections);
	}
}
