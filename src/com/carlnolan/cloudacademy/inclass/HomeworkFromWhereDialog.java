package com.carlnolan.cloudacademy.inclass;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.courses.Lesson;
import com.carlnolan.cloudacademy.inclass.AttendanceDialog.AttendanceTakenListener;
import com.carlnolan.cloudacademy.inclass.SessionOverviewFragment.Selectable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

public class HomeworkFromWhereDialog extends DialogFragment {
	private FromWhereSelectedListener callback;
	private String [] options;
	private Lesson selectedLesson;
	
	public interface FromWhereSelectedListener {
		public void onFromThisLessonSelected(Lesson thisLesson);
		public void onDifferentLessonSelected();
		public void onCustomSelected();
	}
	
	public HomeworkFromWhereDialog(Context context, Lesson selectedLesson0) {
		selectedLesson = selectedLesson0;
		
		options = context.getResources()
				.getStringArray(R.array.homework_from_where_options);
		
		if(selectedLesson == null) {
			String [] temp = options;
			options = new String[2];
			options[0] = temp[1];
			options[1] = temp[2];
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle(R.string.homework_from_where_title)
	           .setItems(options, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	            	   if(selectedLesson == null) {
	            		   which += 1;
	            	   }
	            	   
	            	   if(which == 0) {
	            		   callback.onFromThisLessonSelected(selectedLesson);
	            	   } else if(which == 1) {
	            		   callback.onDifferentLessonSelected();
	            	   } else if(which == 2) {
	            		   callback.onCustomSelected();
	            	   }
	           }
	    });
	    return builder.create();
	}

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            callback = (FromWhereSelectedListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement FromWhereSelectedListener");
        }
    }
}
