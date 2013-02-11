package com.carlnolan.cloudacademy.inclass;

import com.carlnolan.cloudacademy.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddExamDialog extends DialogFragment {
	private EditText name;
	private EditText description;
	
	private ExamAddedListener callback;
	
	private static final int INPUT_ERROR_DURATION = 4;
	
	public interface ExamAddedListener {
		public void onExamSelected(String name, String description);
	}

	public static AddExamDialog newInstance() {
		AddExamDialog instance = new AddExamDialog();
		
        return instance;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	LayoutInflater inflater = getActivity().getLayoutInflater();
    	View dialogView = inflater.inflate(R.layout.dialog_add_exam, null);
    	
    	name = (EditText) dialogView.findViewById(R.id.dialog_add_exam_name);
		description = (EditText) dialogView.findViewById(R.id.dialog_add_exam_description);
    	
    	builder.setView(dialogView)
    		.setPositiveButton("Schedule", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    			}
    		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				dismiss();
    			}
    		}).setTitle("Enter Exam Details");
    	
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
									R.string.add_exam_input_error,
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
		callback.onExamSelected(name.getText().toString(),
				description.getText().toString());
		dismiss();
	}

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            callback = (ExamAddedListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ExamAddedListener");
        }
    }
}
