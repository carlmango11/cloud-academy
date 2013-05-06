package com.carlnolan.cloudacademy.inclass;

import java.text.SimpleDateFormat;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.coursebrowser.FullBrowserActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ExamViewerFragment extends Fragment {
	private Exam currentExam;
	
	private TextView title;
	private TextView description;
	private TextView details;
	private ImageButton course;

	public static ExamViewerFragment newInstance() {
		ExamViewerFragment instance = new ExamViewerFragment();
		return instance;
	}

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
		description = (TextView) getActivity().findViewById(R.id.exam_description);
		course = (ImageButton) getActivity().findViewById(R.id.exam_go_button);
		details = (TextView) getActivity().findViewById(R.id.exam_from_box_details);
	}
	
	/**
	 * Called to make the viewer load in a piece of homework
	 * @param homework
	 */
	public void loadExam(final Exam exam) {
		currentExam = exam;
		
		title.setText(currentExam.getName());
		description.setText(currentExam.getDescription());
		
	    SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy");
		String dateString = formatter.format(exam.getDate().getTime());
		details.setText(exam.getCourseName() + "\n" + dateString);
		
		//Set up course button to open the course browser on the relevant course
		course.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), FullBrowserActivity.class);
				
				intent.putExtra("COURSE_ID", exam.getCourseId());
				intent.putExtra("SECTION_ID", -1);
				intent.putExtra("LESSON_ID", -1);
				
				startActivity(intent);
			}
		});
	}
}
