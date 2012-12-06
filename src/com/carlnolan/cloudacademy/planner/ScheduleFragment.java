package com.carlnolan.cloudacademy.planner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.configuration.AcademyProperties;
import com.carlnolan.cloudacademy.courses.CourseListFragment.OnCourseSelectedListener;
import com.carlnolan.cloudacademy.courses.UpcomingClassesFragment.OnClassSelectedListener;
import com.carlnolan.cloudacademy.scheduling.Session;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ScheduleFragment extends Fragment {
	private ListView sessionList;

	private OnSessionSelectedListener callback;
	private ArrayList<Session> sessions;

    public interface OnSessionSelectedListener {
        public void onSessionSelected(Session session);
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			callback = (OnSessionSelectedListener) activity;
		} catch(ClassCastException e) {
			Log.d("carl", "Could not cast class");
			throw new ClassCastException(activity.toString()
					+ " upcoming! must implement ScheduleFragment.OnSessionSelectedListener");
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d("cloudacademy", "Started Day Viewer Fragment");
		
		//Get links to views
		sessionList = (ListView) getActivity().findViewById(R.id.day_schedule_list);
		
		sessionList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				callback.onSessionSelected(sessions.get(arg2));
			}
		});
		
		new DownloadSessions().execute(new Date());
	}
	
	public void setDate(Date d) {
		new DownloadSessions().execute(d);
	}

	private void updateList(ArrayList<Session> result) {
		boolean isTeacher =
				AcademyProperties.getInstance().getUser().isTeacher();
		
		//Update list of sessions:
		sessionList.setAdapter(
				new SessionListAdapter(
						getActivity(),
						R.layout.session_list_item,
						result,
						isTeacher));
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
		View defaultView = inflater.inflate(R.layout.schedule_viewer, container, false);
    	
		LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
		p1.weight = 0.5f;
		defaultView.setLayoutParams(p1);
		
		return defaultView;
    }
    
	private class DownloadSessions extends AsyncTask<Date, Void, ArrayList<Session>> {
		@Override
		protected ArrayList<Session> doInBackground(Date... date) {
			ArrayList<Session> ls = WebServiceInterface.getInstance().getSessionsForDate(date[0]);
			return ls;
		}

		@Override
		protected void onPostExecute(ArrayList<Session> result) {
			super.onPostExecute(result);
			sessions = result;
			updateList(result);
		}
	}
}
