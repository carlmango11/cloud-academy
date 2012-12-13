package com.carlnolan.cloudacademy.inclass;

import java.util.Calendar;

import com.carlnolan.cloudacademy.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.RadioButton;

public class DueDateDialog extends DialogFragment {
	
	private HomeworkDueDateSelectedListener callback;
	
	private DatePicker datePicker;
	private RadioButton nextClass;
	private RadioButton otherDate;
	
	public interface HomeworkDueDateSelectedListener {
		public void onDueDateSelected(int date, int month, int year);
		public void onNextClassSelected();
	}

	public static DueDateDialog newInstance() {
		DueDateDialog instance = new DueDateDialog();
        return instance;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	LayoutInflater inflater = getActivity().getLayoutInflater();
    	View dialogView = inflater.inflate(R.layout.dialog_homework_due, null);
    	
    	datePicker = (DatePicker) dialogView.findViewById(R.id.dialog_date_picker);
    	nextClass = (RadioButton) dialogView.findViewById(R.id.dialog_next_session_button);
    	otherDate = (RadioButton) dialogView.findViewById(R.id.dialog_specific_date_button);
    	
    	//Set up listeners for radios. When "next class" is selected we want to disable the picker
    	nextClass.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				datePicker.setEnabled(false);
			}
    	});
    	otherDate.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				datePicker.setEnabled(true);
			}
    	});
    	datePicker.setEnabled(false);
    	
		builder.setView(dialogView)
			.setPositiveButton("Finish", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {					
					if(nextClass.isChecked()) {
						callback.onNextClassSelected();
					} else {
						callback.onDueDateSelected(
								datePicker.getDayOfMonth(),
								datePicker.getMonth(),
								datePicker.getYear());
					}
				}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dismiss();
				}
			}).setTitle(R.string.homework_select_due_date);
		
		return builder.create();
	}

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            callback = (HomeworkDueDateSelectedListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement HomeworkDueDateSelectedListener");
        }
    }
}
