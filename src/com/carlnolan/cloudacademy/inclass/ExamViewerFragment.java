package com.carlnolan.cloudacademy.inclass;

import java.text.Format;
import java.text.SimpleDateFormat;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.configuration.AcademyProperties;
import com.carlnolan.cloudacademy.courses.Content;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ExamViewerFragment extends Fragment {
	private Exam currentExam;
	
	private TextView title;
	private TextView description;
	private Button course;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.exam_viewer, container, false);
	}
    
	@Override
	public void onStart() {
		super.onStart();
		
		title = (TextView) getActivity().findViewById(R.id.exam_title);
		description = (TextView) getActivity().findViewById(R.id.exam_description_text);
		course = (Button) getActivity().findViewById(R.id.exam_course_button);
		
		Log.d("carl", "Started Exam Viewer");
	}
	
	/**
	 * Called to make the viewer load in a piece of homework
	 * @param homework
	 */
	public void loadExam(Exam exam) {
		currentExam = exam;
		
		title.setText(currentExam.getName());
		description.setText(currentExam.getDescription());
		course.setText(currentExam.getCourseName());
	}
}
