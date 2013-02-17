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

public class LessonListFragment extends ListFragment {

	private OnLessonSelectedListener callback;
	private ArrayList<Lesson> allLessons;
	private Section thisSection;

    public interface OnLessonSelectedListener {
        public void onLessonSelected(Lesson lesson);
    }
    
    void updateList(ArrayList<Lesson> newLessons) {
    	String [] lessons = new String[newLessons.size()];
    	
    	for(int i=0; i<newLessons.size(); i++) {
    		lessons[i] = newLessons.get(i).toString();
    	}
        
        setListAdapter(
        		new ArrayAdapter<String>(
        				getActivity(),
        				android.R.layout.simple_list_item_activated_1,
        				lessons
        		)
        );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reset();
    }

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		callback.onLessonSelected(allLessons.get(position));
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
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			callback = (OnLessonSelectedListener) activity;
		} catch(ClassCastException e) {
			Log.d("carl", "Could not cast class");
			throw new ClassCastException(activity.toString()
					+ " upcoming! must implement SetionListFragment.OnSectionSelectedListener");
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		
	}

	public void loadLessons(Section section) {
		thisSection = section;
        new DownloadLessons().execute(section.getId());
	}

	public void reset() {
		  String [] lsa = new String[] {"Select Section"};
	      setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, lsa));
	}
    
	private class DownloadLessons extends AsyncTask<Integer, Void, ArrayList<Lesson>> {

		@Override
		protected ArrayList<Lesson> doInBackground(Integer... params) {
			ArrayList<Lesson> ls = WebServiceInterface.getInstance().getLessons(params[0]);
			return ls;
		}

		@Override
		protected void onPostExecute(ArrayList<Lesson> result) {
			super.onPostExecute(result);
			allLessons = result;
			updateList(result);
		}
	}
}
