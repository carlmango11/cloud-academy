package com.carlnolan.cloudacademy.planner;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.courses.CourseListFragment.OnCourseSelectedListener;
import com.carlnolan.cloudacademy.courses.UpcomingClassesFragment.OnClassSelectedListener;
import com.carlnolan.cloudacademy.scheduling.Session;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class DayViewerFragment extends Fragment {
	private TextView title;
	private TextView time;
	private TextView dateInfo;
	private CalendarView calendar;

	private OnScheduleDayChangedListener callback;

    public interface OnScheduleDayChangedListener {
        public void onScheduleDayChanged(Date newDate);
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
   /* void updateList(ArrayList<Session> newSessions) {
    	Session [] sessions = new Session[newSessions.size()];
    	sessions = newSessions.toArray(sessions);
        
    	sessionList.setAdapter(
        		new UpcomingSessionsAdapter(
        				getActivity(),
        				R.layout.upcoming_classes_list_item,
        				sessions
        		)
        );
    	sessionList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				callback.onSessionSelected(upcomingSessions.get(arg2));
			}
    	});
    }*/
    
    private void updateTextFields(Date d) {
		callback.onScheduleDayChanged(d);

		//Set up new field values:
		String newTitle;
		String newDateInfo;
		
		Date newDate = d;
		Calendar selected = Calendar.getInstance();
		selected.setTime(newDate);
		Calendar today = Calendar.getInstance();
		
		boolean todaySelected = today.get(Calendar.YEAR) == selected.get(Calendar.YEAR) &&
				today.get(Calendar.DAY_OF_YEAR) == selected.get(Calendar.DAY_OF_YEAR);
		today.add(Calendar.DATE, 1);
		boolean tomorrowSelected = today.get(Calendar.YEAR) == selected.get(Calendar.YEAR) &&
				today.get(Calendar.DAY_OF_YEAR) == selected.get(Calendar.DAY_OF_YEAR);
		
		String dateFormatString = "d MMMMMMMM yyyy";
		if(todaySelected) {
			newTitle = "TODAY";
			dateFormatString = "EEEEEEEEE, " + dateFormatString;
		} else if(tomorrowSelected) {
			newTitle = "TOMORROW";
			dateFormatString = "EEEEEEEEE, " + dateFormatString;
		} else {
			newTitle = selected.getDisplayName(
					Calendar.DAY_OF_WEEK,
					Calendar.LONG,
					Locale.UK);
			newTitle = newTitle.toUpperCase();
		}

		Format sdf = new SimpleDateFormat(dateFormatString);
		newDateInfo = sdf.format(selected.getTime());
		
		title.setText(newTitle);
		dateInfo.setText(newDateInfo);
    }

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			callback = (OnScheduleDayChangedListener) activity;
		} catch(ClassCastException e) {
			Log.d("carl", "Could not cast class");
			throw new ClassCastException(activity.toString()
					+ " upcoming! must implement DayViewerFragment.OnScheduleDayChangedListener");
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d("cloudacademy", "Started Day Viewer Fragment");
		
		//Get links to views
		calendar = (CalendarView) getActivity().findViewById(R.id.calendar_view);
		title = (TextView) getActivity().findViewById(R.id.title);
		time = (TextView) getActivity().findViewById(R.id.time);
		dateInfo = (TextView) getActivity().findViewById(R.id.date_info);
		
		calendar.setOnDateChangeListener(new OnDateChangeListener() {
			public void onSelectedDayChange(CalendarView view, int year,
					int month, int dayOfMonth) {
				updateTextFields(new Date(view.getDate()));
			}
		});

        updateTextFields(new Date());
	}

	@Override
	public void onResume() {
		super.onResume();
		
		updateTextFields(new Date(calendar.getDate()));
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
		View defaultView = inflater.inflate(R.layout.day_viewer, container, false);

		LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
		p1.weight = 0.5f;
		defaultView.setLayoutParams(p1);
		
		return defaultView;
    }
    
	private class UpdateFragment extends AsyncTask<Date, Void, ArrayList<Session>> {
		@Override
		protected ArrayList<Session> doInBackground(Date... date) {
			ArrayList<Session> ls = WebServiceInterface.getInstance().getSessionsForDate(date[0]);
			return ls;
		}

		@Override
		protected void onPostExecute(ArrayList<Session> result) {
			super.onPostExecute(result);
			//updateList(result);
		}
	}
}
