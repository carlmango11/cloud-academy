package com.carlnolan.cloudacademy.courses;

import java.util.ArrayList;
import java.util.Calendar;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.listadapters.CourseListAdapter;
import com.carlnolan.cloudacademy.listadapters.CoursesTabListsAdapter;
import com.carlnolan.cloudacademy.scheduling.Session;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;
import com.carlnolan.cloudacademy.R;

import android.app.Activity;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CourseListFragment extends ListFragment {

	private OnCourseSelectedListener callback;
	private ArrayList<Course> allCourses;

    public interface OnCourseSelectedListener {
        public void onCourseSelected(Course course);
    }
    
    void updateList(ArrayList<Course> newCourses) {
    	String [] courses = new String[newCourses.size()];
    	
    	for(int i=0; i<newCourses.size(); i++) {
    		courses[i] = newCourses.get(i).toString();
    	}
        
        setListAdapter(
        		new ArrayAdapter<String>(
        				getActivity(),
        				android.R.layout.simple_list_item_activated_1,
        				courses
        	));
    }
    
    public void deselectAll() {
    	ListView listView = getListView();
    	
    	for(int i=0; i < getListAdapter().getCount(); i++) {
    		listView.setItemChecked(i, false);
    	}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        String [] lsa = new String[] {"Downloading..."};
        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, lsa));

        new DownloadCourses().execute();
    }

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		getListView().setItemChecked(position, true);
		callback.onCourseSelected(allCourses.get(position));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View defaultView = super.onCreateView(inflater, container, savedInstanceState);
		LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
		p1.weight = 0.33f;
		defaultView.setLayoutParams(p1);
		return defaultView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			callback = (OnCourseSelectedListener) activity;
		} catch(ClassCastException e) {
			Log.d("carl", "Could not cast class");
			throw new ClassCastException(activity.toString()
					+ " upcoming! must implement CourseListFragment.OnCourseSelectedListener");
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		getListView().setItemChecked(0, true);
		/*if(getFragmentManager().findFragmentById(R.id.section_list_fragment) != null) {
			Log.d("carl", "setting choice mode");
			
		}*/
	}
    
	private class DownloadCourses extends AsyncTask<Void, Void, ArrayList<Course>> {

		@Override
		protected ArrayList<Course> doInBackground(Void... params) {
			ArrayList<Course> ls = WebServiceInterface.getInstance().getCourses(true);
			return ls;
		}

		@Override
		protected void onPostExecute(ArrayList<Course> result) {
			super.onPostExecute(result);
			allCourses = result;
			updateList(result);
		}
	}
}
