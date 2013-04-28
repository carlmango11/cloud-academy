package com.carlnolan.cloudacademy.progress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.asynctasks.DownloadClassList;
import com.carlnolan.cloudacademy.inclass.Homework;
import com.carlnolan.cloudacademy.inclass.StudentGridAdapter;
import com.carlnolan.cloudacademy.progress.GradeStudentDialog.StudentGradingFinishedListener;
import com.carlnolan.cloudacademy.usermanagement.Student;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RecordGrades extends FragmentActivity implements
		DownloadClassList.DownloadClassListResponder,
		StudentGradingFinishedListener {
	private GradesUpdatedListener callback;

	private ActionBar actionBar;
	private ProgressBar progress;
	private GridView grid;
	private RecordGradesGridAdapter adapter;

	private List<Student> students;
	private int examId;
	private int classId;
	
	public interface GradesUpdatedListener {
		public void gradesUpdated();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_grades_activity);
		setupHandles();

		// Set up the action bar.
		actionBar = getActionBar();

		// get passed session
		Bundle b = this.getIntent().getExtras();
		examId = b.getInt("EXAM_ID");

		// get classid;
		AsyncTask<Void, Void, Integer> getClassIdTask = new AsyncTask<Void, Void, Integer>() {
			@Override
			protected Integer doInBackground(Void... params) {
				return WebServiceInterface.getInstance().getClassIdFromExamId(
						examId);
			}

			@Override
			protected void onPostExecute(Integer result) {
				classIdReceived(result);
			}
		};
		getClassIdTask.execute();
	}

	private void setupHandles() {
		progress = (ProgressBar) findViewById(R.id.record_grades_progress);
		grid = (GridView) findViewById(R.id.record_grades_student_list);
	}

	private void classIdReceived(int id) {
		classId = id;

		// start class list download
		new DownloadClassList().execute(classId, this);
	}

	public void onDownloadClassListComplete(List<Student> result) {
		students = result;

		downloadExistingGrades();
	}

	private void downloadExistingGrades() {
		// download an existing grades
		AsyncTask<Void, Void, Map<Integer, Integer>> getExistingGradesTask = new AsyncTask<Void, Void, Map<Integer, Integer>>() {
			@Override
			protected Map<Integer, Integer> doInBackground(Void... params) {
				Map<Integer, Integer> grades = WebServiceInterface
						.getInstance().getExistingGrades(examId);
				return grades;
			}

			@Override
			protected void onPostExecute(Map<Integer, Integer> result) {
				existingGradesDownloaded(result);
			}
		};
		getExistingGradesTask.execute();
	}

	public void existingGradesDownloaded(Map<Integer, Integer> result) {
		// Close progress bar, show gridview
		progress.setVisibility(View.GONE);

		adapter = new RecordGradesGridAdapter(this,
				R.layout.record_grades_grid_item, students, result);

		grid.setAdapter(adapter);

		grid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				gradeStudent(pos);
			}
		});
	}

	private void gradeStudent(int pos) {
		GradeStudentDialog dialog = GradeStudentDialog.newInstance(
				students.get(pos), examId);
		dialog.show(this.getSupportFragmentManager(), "RECORD_GRADE_DIALOG");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; go up
			finish();
		case R.id.record_grades_finish:
			//callback.gradesUpdated();
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.record_grades_menu, menu);

		return true;
	}

	public void studentGradesChanged() {
		// refresh the grades of students
		downloadExistingGrades();
	}

	public static class GradesHolder {
		int user;
		int grade;

		public static Map<Integer, Integer> buildMapFromGrades(String json) {
			Gson gson = new GsonBuilder().create();

			GradesHolder[] array = gson.fromJson(json, GradesHolder[].class);

			Map<Integer, Integer> map = new HashMap<Integer, Integer>();
			for (GradesHolder gh : array) {
				map.put(gh.user, gh.grade);
			}

			return map;
		}
	}
}
