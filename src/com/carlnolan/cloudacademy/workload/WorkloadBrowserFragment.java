package com.carlnolan.cloudacademy.workload;

import java.util.Calendar;
import com.carlnolan.cloudacademy.R;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.CalendarView.OnDateChangeListener;

public class WorkloadBrowserFragment extends Fragment {	
	private WorkloadDateSelectedListener callback;
	
	private CalendarView calendar;
	private RadioButton all;
	private RadioButton day;
	
	public interface WorkloadDateSelectedListener {
		public void onWorkloadDateSelected(Calendar date);
	}

	public static WorkloadBrowserFragment newInstance() {
		WorkloadBrowserFragment instance = new WorkloadBrowserFragment();
		
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
			callback = (WorkloadDateSelectedListener) activity;
		} catch(ClassCastException e) {
			Log.d("carl", "Could not cast class");
			throw new ClassCastException(activity.toString()
					+ " upcoming! must implement " +
					"WorkloadDateSelectedListener");
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		
		calendar = (CalendarView) getActivity().findViewById(R.id.workload_browser_calendar);
		all = (RadioButton) getActivity().findViewById(R.id.workload_browser_all);
		day = (RadioButton) getActivity().findViewById(R.id.workload_browser_day);
		
		all.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {				
				//Send null to the callback, this will be interpreted as "all dates"
				callback.onWorkloadDateSelected(null);
				
				setCalendarEnabled(false);
			}
    	});
		day.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				updateDate();
				setCalendarEnabled(true);
			}
    	});
		
		calendar.setOnDateChangeListener(new OnDateChangeListener() {
			public void onSelectedDayChange(CalendarView view, int year,
					int month, int dayOfMonth) {
				updateDate();
			}
		});
	}

	private void setCalendarEnabled(boolean b) {
		//disable the calendar
		int newColour = b ? Color.WHITE : Color.GRAY;
		calendar.setBackgroundColor(newColour);
		calendar.setEnabled(b);
	}
	
	/**
	 * Builds a Calendar from the selected date and sends it to the callback
	 */
	private void updateDate() {
		//Call back to update the workload list fragment with the selected date
		long selectedTimeMillis = calendar.getDate();
		Calendar selectedTime = Calendar.getInstance();
		selectedTime.setTimeInMillis(selectedTimeMillis);
		callback.onWorkloadDateSelected(selectedTime);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        return inflater.inflate(R.layout.workload_browser, null);
	}
}
