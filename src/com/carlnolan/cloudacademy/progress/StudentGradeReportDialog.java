package com.carlnolan.cloudacademy.progress;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.asynctasks.DownloadCourses;
import com.carlnolan.cloudacademy.asynctasks.DownloadCourses.OnCoursesDownloadedListener;
import com.carlnolan.cloudacademy.bo.ExamGrade;
import com.carlnolan.cloudacademy.courses.Course;
import com.carlnolan.cloudacademy.inclass.Exam;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StudentGradeReportDialog extends DialogFragment {
	private TextView dateText;
	private TextView gradeText;
	private TextView courseText;

	public static StudentGradeReportDialog newInstance(ExamGrade grade) {
		StudentGradeReportDialog instance = new StudentGradeReportDialog();
		
		//date to fancy string
		Calendar date = grade.getDate();
	    SimpleDateFormat formatter = new SimpleDateFormat("d MMMMMMMM yyyy");
		String dateString = formatter.format(date.getTime());
		
		Bundle args = new Bundle();
		args.putInt("GRADE", grade.getGrade());
		args.putString("EXAM_NAME", grade.toString());
		args.putString("DATE", dateString);
		args.putString("COURSE", grade.getCourseName());
        instance.setArguments(args);

		return instance;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String dateString = getArguments().getString("DATE");
		String examName = getArguments().getString("EXAM_NAME");
		int grade = getArguments().getInt("GRADE");
		String courseName = getArguments().getString("COURSE");
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_student_grade_report, null);
		
		dateText = (TextView) dialogView.findViewById(R.id.dialog_student_grade_report_date);
		gradeText = (TextView) dialogView.findViewById(R.id.dialog_student_grade_report_grade);
		courseText = (TextView) dialogView.findViewById(R.id.dialog_student_grade_report_course);
		
		dateText.setText("Taken " + dateString);
		gradeText.setText(grade + "%");
		courseText.setText(courseName);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setView(dialogView)
			.setTitle(examName);
		
		return builder.create();
	}
}
