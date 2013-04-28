package com.carlnolan.cloudacademy.progress;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.usermanagement.Student;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class GradeStudentDialog extends DialogFragment {
	
	private static final int INPUT_ERROR_DURATION = 4;
	
	private StudentGradingFinishedListener callback;

	private Student student;
	private int examId;
	
	private ImageView image;
	private TextView name;
	private EditText score;
	private ProgressBar progress;

	public interface StudentGradingFinishedListener {
		public void studentGradesChanged();
	}

	public static GradeStudentDialog newInstance(Student s, int examId0) {
		GradeStudentDialog instance = new GradeStudentDialog();
		instance.setStudent(s);
		instance.setExamId(examId0);

		return instance;
	}

	private void setExamId(int examId0) {
		examId = examId0;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_grade_student, null);
		
		image = (ImageView) dialogView.findViewById(R.id.dialog_grade_student_image);
		name = (TextView) dialogView.findViewById(R.id.dialog_grade_student_name);
		score = (EditText) dialogView.findViewById(R.id.dialog_grade_student_score);
		
		Bitmap studentPic = student.getPhoto();
		if(studentPic != null) {
			image.setImageBitmap(studentPic);
		}
		name.setText(student.getFirstname() + " " + student.getSurname());
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.grade_student_dialog_title)
				.setView(dialogView)
				.setNegativeButton(R.string.grade_student_dialog_cancel, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int id) {
	    				dismiss();
	    			}
	    		}).setPositiveButton(R.string.grade_student_dialog_save, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int id) {
	    				//pointless, going to be overwritten in a minute
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
	    				//This is what will happen on click of positive button
						int thisGrade = Integer.parseInt("" + score.getText());
						if(thisGrade <= 100 && thisGrade >= 0) {
							saveGrade(thisGrade);
							dismiss();
						} else {
							Toast toast = Toast.makeText(getActivity(),
									R.string.dialog_grade_student_error,
									INPUT_ERROR_DURATION);
							toast.show();
						}
					}
                });
			}
        });
    	
		return alert;
	}
	
	private void saveGrade(final int g) {
		AsyncTask<Void, Void, Integer> saveGrade =
				new AsyncTask<Void, Void, Integer>() {
					@Override
					protected Integer doInBackground(Void... params) {
						WebServiceInterface.getInstance().saveGrade(examId, student.getId(), g);
						return g;
					}
					
					@Override
					protected void onPostExecute(final Integer result) {
						onGradeSaved(g);
					}
		};
		saveGrade.execute();
	}
	
	private void onGradeSaved(int g) {
		callback.studentGradesChanged();
	}
	
	public void setStudent(Student s) {
		student = s;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			callback = (StudentGradingFinishedListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement StudentGradingFinishedListener");
		}
	}
}
