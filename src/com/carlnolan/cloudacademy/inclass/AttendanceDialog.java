package com.carlnolan.cloudacademy.inclass;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.asynctasks.DownloadClassList;
import com.carlnolan.cloudacademy.asynctasks.DownloadLessons;
import com.carlnolan.cloudacademy.asynctasks.DownloadSections;
import com.carlnolan.cloudacademy.asynctasks.GetAttendanceList;
import com.carlnolan.cloudacademy.asynctasks.RecordAttendance;
import com.carlnolan.cloudacademy.courses.Lesson;
import com.carlnolan.cloudacademy.courses.Section;
import com.carlnolan.cloudacademy.usermanagement.Student;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class AttendanceDialog extends DialogFragment
	implements DownloadClassList.DownloadClassListResponder,
	RecordAttendance.OnAttendanceRecordedListener,
	GetAttendanceList.GetAttendanceListListener {
	
	private AttendanceTakenListener callback;
	private int classId;
	private int sessionId;
	private GridView studentList;
	private ProgressBar progress;
	private Button selectAll;
	private Button deselectAll;
	private StudentGridAdapter adapter;
	
	public interface AttendanceTakenListener {
		public void onAttendanceTaken();
	}

	public static AttendanceDialog newInstance(int classId, int sessionId) {
		AttendanceDialog instance = new AttendanceDialog();
		
		Bundle args = new Bundle();
        args.putInt("classId", classId);
        args.putInt("sessionId", sessionId);
        instance.setArguments(args);
        return instance;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		classId = getArguments().getInt("classId");
		sessionId = getArguments().getInt("sessionId");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	LayoutInflater inflater = getActivity().getLayoutInflater();
    	View dialogView = inflater.inflate(R.layout.dialog_take_attendance, null);
    	
    	studentList = (GridView) dialogView.findViewById(R.id.dialog_take_attendance_student_list);
		selectAll = (Button) dialogView.findViewById(R.id.dialog_attendance_select_all);
		deselectAll = (Button) dialogView.findViewById(R.id.dialog_attendance_select_none);
		progress = (ProgressBar) dialogView.findViewById(R.id.dialog_take_attendance_progress);
		
		//Start the progress bar until we're done d/ling everything
		showProgressBar(true);
		
		//Download the classlist and existing attendance list
    	new DownloadClassList().execute(classId, this);
    	new GetAttendanceList().execute(sessionId, this);
    	
    	builder.setView(dialogView)
    		.setPositiveButton("Save", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    			}
    		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				dismiss();
    			}
    		}).setTitle("Take Attendance");
    	
    	//Set listeners for select buttons:
    	selectAll.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				adapter.setAllSelected(true);
			}
    	});
    	deselectAll.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				adapter.setAllSelected(false);
			}
    	});
    	
    	//This is necessary to override the default auto-close feature of
    	//the positive button
    	final AlertDialog alert = builder.create();
    	alert.setOnShowListener(new DialogInterface.OnShowListener() {
			public void onShow(DialogInterface arg0) {
                Button button = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
	    				saveAttendance();
					}
                });
			}
        });
    	
    	return alert;
	}
	
	private void saveAttendance() {
		List<Student> att = 
				adapter.getAttendanceList();
		
		showProgressBar(true);
		new RecordAttendance().execute(sessionId, att, this);
	}

	private void showProgressBar(boolean b) {
		int mainElementsVisiblity = b ? View.GONE : View.VISIBLE;
		int progressVisiblity = b ? View.VISIBLE : View.GONE;
		
		selectAll.setVisibility(mainElementsVisiblity);
		deselectAll.setVisibility(mainElementsVisiblity);
		studentList.setVisibility(mainElementsVisiblity);
		progress.setVisibility(progressVisiblity);
	}

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            callback = (AttendanceTakenListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AttendanceTakenListener");
        }
    }

	/**
	 * Gets called when the DownloadClassList tasks returns
	 */
	public void onDownloadClassListComplete(List<Student> students) {
		//Close progress bar, show gridview
		showProgressBar(false);
		
		adapter = new StudentGridAdapter(
				getActivity(),
				R.layout.student_grid_item,
				students);

		studentList.setAdapter(adapter);
	}

	/**
	 * Gets called when the recording of the attendance in the DB finishes
	 */
	public void onAttendanceRecorded() { //TODO: 2783
		callback.onAttendanceTaken();
		dismiss();
	}

	/**
	 * Returned when we've gotten the list of students marked as attended
	 */
	public void onAttendanceListReceived(List<Integer> studentIds) {
		adapter.setStudentsSelected(studentIds);
	}
}
