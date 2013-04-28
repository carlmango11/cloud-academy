package com.carlnolan.cloudacademy.progress;

import java.util.ArrayList;
import java.util.List;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.asynctasks.DownloadCourses;
import com.carlnolan.cloudacademy.asynctasks.DownloadCourses.OnCoursesDownloadedListener;
import com.carlnolan.cloudacademy.courses.Course;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

public class SelectCourseDialog extends DialogFragment implements
		OnCoursesDownloadedListener {
	private CourseSelectedCallback callback;

	private ListView list;
	private ProgressBar progress;
	private List<Course> allCourses;

	public interface CourseSelectedCallback {
		public void courseSelected(int id);
	}

	public static SelectCourseDialog newInstance() {
		SelectCourseDialog instance = new SelectCourseDialog();

		return instance;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// start download
		new DownloadCourses(this).execute();

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_select_course, null);

		list = (ListView) dialogView
				.findViewById(R.id.dialog_select_courses_list);
		progress = (ProgressBar) dialogView
				.findViewById(R.id.dialog_select_courses_progress);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.dialog_select_course_title)
				.setView(dialogView)
				.setNegativeButton(R.string.dialog_select_course_cancel, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int id) {
	    				dismiss();
	    			}
	    		});
		return builder.create();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			callback = (CourseSelectedCallback) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement CourseSelectedCallback");
		}
	}

	public void onCoursesDownloaded(ArrayList<Course> result) {
		allCourses = result;
		
		//create string array
		String [] courses = new String[result.size()];
		for(int i=0;i<result.size();i++) {
			courses[i] = result.get(i).toString();
		}
		
		list.setAdapter(new ArrayAdapter<String>(
				getActivity(),
				android.R.layout.simple_list_item_activated_1,
				courses
		));
		
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				selectCourse(pos);
			}
		});
		
		progress.setVisibility(View.GONE);
	}
	
	private void selectCourse(int pos) {
		dismiss();
		callback.courseSelected(allCourses.get(pos).getId());
	}
}
