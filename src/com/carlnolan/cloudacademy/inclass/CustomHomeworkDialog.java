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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class CustomHomeworkDialog extends DialogFragment {
	private EditText name;
	private EditText description;
	
	private CustomHomeworkListener callback;
	
	private static final int INPUT_ERROR_DURATION = 4;
	
	public interface CustomHomeworkListener {
		public void onCustomHomeworkSelected(String name, String description);
	}

	public static CustomHomeworkDialog newInstance() {
		CustomHomeworkDialog instance = new CustomHomeworkDialog();
		
        return instance;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	LayoutInflater inflater = getActivity().getLayoutInflater();
    	View dialogView = inflater.inflate(R.layout.dialog_custom_homework, null);
    	
    	name = (EditText) dialogView.findViewById(R.id.dialog_custom_name);
		description = (EditText) dialogView.findViewById(R.id.dialog_custom_description);
    	
    	builder.setView(dialogView)
    		.setPositiveButton("Assign", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    			}
    		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				dismiss();
    			}
    		}).setTitle("Enter Homework Details");
    	
    	//This is necessary to override the default auto-close feature of
    	//the positive button
    	final AlertDialog alert = builder.create();
    	alert.setOnShowListener(new DialogInterface.OnShowListener() {
			public void onShow(DialogInterface arg0) {
                Button button = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
	    				//This is what will happen on click of positive button
						if(name.getText().length() != 0) {
							saveCustom();
						} else {
							Toast toast = Toast.makeText(getActivity(),
									R.string.custom_homework_input_error,
									INPUT_ERROR_DURATION);
							toast.show();
						}
					}
                });
			}
        });
    	
    	return alert;
	}
	
	public void saveCustom() {
		callback.onCustomHomeworkSelected(name.getText().toString(),
				description.getText().toString());
		dismiss();
	}

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            callback = (CustomHomeworkListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement CustomHomeworkListener");
        }
    }
}
