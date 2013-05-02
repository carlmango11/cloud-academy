package com.carlnolan.cloudacademy.progress;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.asynctasks.DownloadCourses;
import com.carlnolan.cloudacademy.asynctasks.DownloadCourses.OnCoursesDownloadedListener;
import com.carlnolan.cloudacademy.courses.Course;
import com.carlnolan.cloudacademy.inclass.Exam;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ListView;
import android.widget.ProgressBar;

public class SelectExamDialog extends DialogFragment implements
		Exam.DownloadExamsForRangeListener {
	private ExamToGradeSelectedListener callback;

	private ListView list;
	private ProgressBar progress;
	private DatePicker date;
	private List<Exam> allExams;
	private AsyncTask currentTask;
	private int courseId;

	public interface ExamToGradeSelectedListener {
		public void examToGradeSelected(int id);
	}

	public static SelectExamDialog newInstance(int course) {
		SelectExamDialog instance = new SelectExamDialog();
		
		Bundle args = new Bundle();
        args.putInt("course", course);
        instance.setArguments(args);

		return instance;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		courseId = getArguments().getInt("course");
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_select_exam_to_grade, null);

		list = (ListView) dialogView
				.findViewById(R.id.dialog_select_exam_list);
		progress = (ProgressBar) dialogView
				.findViewById(R.id.dialog_select_exam_progress);
		date = (DatePicker) dialogView.findViewById(R.id.dialog_select_exam_date);
		
		date.init(date.getYear(), date.getMonth(), date.getDayOfMonth(), new OnDateChangedListener() {
			public void onDateChanged(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				updateDate();
			}
		});
		
		alterDatePicker();
		updateDate();

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.dialog_select_exam_title)
				.setView(dialogView)
				.setNegativeButton(R.string.dialog_select_exam_cancel, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int id) {
	    				dismiss();
	    			}
	    		});
		return builder.create();
	}
	
	/**
	 * Recalculates calendar range and updates list
	 */
	private void updateDate() {
		//if theres an outstanding request cancel it
		if(currentTask != null) {
			currentTask.cancel(true);
			currentTask = null;
		}
		
		//sort of ranges for this month
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.clear(Calendar.MINUTE);
		start.clear(Calendar.SECOND);
		start.clear(Calendar.MILLISECOND);
		start.set(Calendar.DAY_OF_MONTH, 1);
		
		int month = date.getMonth();
		int year = date.getYear();
		
		start.set(Calendar.MONTH, month);
		start.set(Calendar.YEAR, year);
		
		//end of month:
		end = (Calendar) start.clone();
		end.add(Calendar.MONTH, 1);
		end.add(Calendar.DAY_OF_YEAR, -1);
		
		list.setVisibility(View.INVISIBLE);
		progress.setVisibility(View.VISIBLE);
		currentTask = Exam.downloadExamsForRange(this, start, end, courseId);
	}

	/**
	 * Removes the day field from the date picker
	 * From ze interwebs
	 */
	private void alterDatePicker() {
		try {
		    Field f[] = date.getClass().getDeclaredFields();
		    for (Field field : f) {
		        if (field.getName().equals("mDaySpinner")) {
		            field.setAccessible(true);
		            Object dayPicker = new Object();
		            dayPicker = field.get(date);
		            ((View) dayPicker).setVisibility(View.GONE);
		        }
		    }
		} catch (SecurityException e) {
		    Log.d("ERROR", e.getMessage());
		} 
		catch (IllegalArgumentException e) {
		    Log.d("ERROR", e.getMessage());
		} catch (IllegalAccessException e) {
		    Log.d("ERROR", e.getMessage());
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			callback = (ExamToGradeSelectedListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement ExamToGradeSelectedListener");
		}
	}

	public void onExamsForRangeDownloaded(List<Exam> exams) {
		currentTask = null;
		allExams = exams;
		
		//create string array
		String [] strings = new String[exams.size()];
		for(int i=0;i<exams.size();i++) {
			strings[i] = exams.get(i).toString();
		}
		
		list.setAdapter(new ArrayAdapter<String>(
				getActivity(),
				android.R.layout.simple_list_item_activated_1,
				strings
		));
		
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				selectExam(pos);
			}
		});
		
		progress.setVisibility(View.GONE);
		list.setVisibility(View.VISIBLE);
	}

	private void selectExam(int pos) {
		dismiss();
		callback.examToGradeSelected(allExams.get(pos).getId());
	}
}
