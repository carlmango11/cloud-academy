package com.carlnolan.cloudacademy.inclass;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.carlnolan.cloudacademy.LoginActivity;
import com.carlnolan.cloudacademy.MainActivity;
import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.courses.Content;
import com.carlnolan.cloudacademy.courses.CourseListFragment.OnCourseSelectedListener;
import com.carlnolan.cloudacademy.courses.Exercise;
import com.carlnolan.cloudacademy.courses.LearningMaterial;
import com.carlnolan.cloudacademy.courses.Lesson;
import com.carlnolan.cloudacademy.scheduling.Session;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;
import com.carlnolan.cloudacademy.asynctasks.DownloadExercises;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class HomeworkViewerFragment extends Fragment {
	private Homework currentHomework;
	
	private RelativeLayout contentPanel;
	
	private TextView title;
	private TextView description;
	private TextView dueDate;
	private TextView teacherName;
	private TextView completed;
	private Button lesson;
	private Button course;
	private Button content;
	
	private ProgressDialog progressDialog;
	
	//Unsupported Toast duration
	private static final int UNSUPPORTED_FILETYPE_TOAST_DURATION = 4;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.homework_viewer, container, false);
	}
	
	private void setFonts() {
		Typeface crayonFont = Typeface.createFromAsset(getActivity().getAssets(), "CrayonCrumble.ttf");
		teacherName.setTypeface(crayonFont);
		dueDate.setTypeface(crayonFont);
		completed.setTypeface(crayonFont);
		
		//Others
		TextView temp = (TextView) getActivity().findViewById(R.id.homework_due_text);
		temp.setTypeface(crayonFont);
		temp = (TextView) getActivity().findViewById(R.id.homework_for_text);
		temp.setTypeface(crayonFont);
	}
    
	@Override
	public void onStart() {
		super.onStart();
        
        //Bind xml controls:
        contentPanel = (RelativeLayout) getActivity().findViewById(R.id.homework_viewer_content_panel);
        
        title = (TextView) getActivity().findViewById(R.id.homework_title);
		description = (TextView) getActivity().findViewById(R.id.homework_description);
		completed = (TextView) getActivity().findViewById(R.id.homework_completed_text);
		dueDate = (TextView) getActivity().findViewById(R.id.homework_due_date_text);
		teacherName = (TextView) getActivity().findViewById(R.id.homework_teacher_name);

		lesson = (Button) getActivity().findViewById(R.id.homework_lesson_button);
		course = (Button) getActivity().findViewById(R.id.homework_course_button);
		content = (Button) getActivity().findViewById(R.id.homework_content_button);
		
		setFonts();
		
		Log.d("carl", "Started Lesson Viewer");
	}

	public void loadHomework(Homework homework) {
		currentHomework = homework;
		
		title.setText(currentHomework.toString());
		description.setText(currentHomework.getDescription());		
		teacherName.setText(currentHomework.getReceivingTeacher());
		
		//Format date for textView
		Format sdf = new SimpleDateFormat("EEEEEEEEE, d MMMMMMMM yyyy");
		String dueDateText = sdf.format(currentHomework.getDueDate().getTime());
		dueDate.setText(dueDateText);
		
		//Set buttons
		lesson.setText(currentHomework.getAccompanyingLessonName());
		course.setText(currentHomework.getCourseName());
		
		//Set the completedText		
		if(currentHomework.isComplete()) {
			completed.setText("COMPLETED");
			completed.setTextColor(
					getActivity().getResources().getColor(
							R.color.Green));
		} else {
			completed.setText("NOT COMPLETED");
			completed.setTextColor(
					getActivity().getResources().getColor(
							R.color.Red));
		}
	}
}
