package com.carlnolan.cloudacademy.progress;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import com.carlnolan.cloudacademy.MainActivity;
import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.asynctasks.DownloadCourses;
import com.carlnolan.cloudacademy.asynctasks.DownloadCourses.OnCoursesDownloadedListener;
import com.carlnolan.cloudacademy.bo.ExamGrade;
import com.carlnolan.cloudacademy.configuration.AcademyProperties;
import com.carlnolan.cloudacademy.courses.Course;
import com.carlnolan.cloudacademy.inclass.AddExamDialog;
import com.carlnolan.cloudacademy.inclass.AttachLessonDialog;
import com.carlnolan.cloudacademy.inclass.AttendanceDialog;
import com.carlnolan.cloudacademy.inclass.HomeworkFromWhereDialog;
import com.carlnolan.cloudacademy.usermanagement.User;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.Spinner;
import android.widget.ViewFlipper;

public class ProgressViewerFragment extends Fragment implements
		OnCoursesDownloadedListener {
	private WebView graphView;
	private ViewFlipper flipper;
	private ListView courseList;
	private RadioButton all;
	private RadioButton specific;
	private ListView examList;
	private Button back;
	private ProgressBar gradeSpinner;
	private ProgressBar courseSpinner;

	private AsyncTask<Void, Void, List<ExamGrade>> currentTask;
	private List<Course> courses;
	private Course selectedCourse;
	private List<ExamGrade> grades;
	private boolean isTeacher;

	public static ProgressViewerFragment newInstance() {
		ProgressViewerFragment instance = new ProgressViewerFragment();

		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	/*
	 * @Override public void onAttach(Activity activity) {
	 * super.onAttach(activity);
	 * 
	 * try { callback = (WorkloadDateSelectedListener) activity; }
	 * catch(ClassCastException e) { Log.d("carl", "Could not cast class");
	 * throw new ClassCastException(activity.toString() +
	 * " upcoming! must implement " + "WorkloadDateSelectedListener"); } }
	 */

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.progress_menu, menu);

		// Only teachers can record grades
		if (!AcademyProperties.getInstance().getUser().isTeacher()) {
			MenuItem iconToRemove = menu.findItem(R.id.progress_record_grades);
			iconToRemove.setVisible(false);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.progress_record_grades:
			// start first dialog
			SelectCourseDialog dialog = SelectCourseDialog.newInstance();
			dialog.show(getActivity().getSupportFragmentManager(),
					"SELECT_COURSE_DIALOG");
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		graphView = (WebView) getActivity().findViewById(
				R.id.progress_graph_view);
		courseList = (ListView) getActivity().findViewById(
				R.id.progress_course_list);
		flipper = (ViewFlipper) getActivity().findViewById(
				R.id.progress_flipper);
		all = (RadioButton) getActivity().findViewById(R.id.progress_radio_all);
		specific = (RadioButton) getActivity().findViewById(
				R.id.progress_radio_specific);
		back = (Button) getActivity().findViewById(R.id.progress_back);
		examList = (ListView) getActivity().findViewById(
				R.id.progress_exam_list);
		gradeSpinner = (ProgressBar) getActivity().findViewById(R.id.progress_grade_spinner);
		courseSpinner = (ProgressBar) getActivity().findViewById(R.id.progress_course_spinner);

		// find out if the user is a teacher or not
		isTeacher = AcademyProperties.getInstance().getUser().isTeacher();

		// setup click events for radios
		all.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				setCourseListEnabled(false);
				loadGraph(-1);
			}
		});
		specific.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				downloadCourses();
				setCourseListEnabled(true);
			}
		});

		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// cancel any outstanding asynctask
				if (currentTask != null) {
					currentTask.cancel(true);
				}

				selectedCourse = null;
				backToAllCourses();
			}
		});

		// fire off courses download
		downloadCourses();

		setUpWebView();

		// if specfic is selected then enable the courselist
		if (specific.isChecked()) {
			setCourseListEnabled(true);
		} else {
			setCourseListEnabled(false);
		}

		// if theres a selected course, show graph for it, otherwise -1
		int courseId = -1;
		if (selectedCourse != null) {
			courseId = selectedCourse.getId();
			downloadExamGrades(courseId);
			flipper.setDisplayedChild(1);
		}
		loadGraph(courseId);
	}

	private void backToAllCourses() {
		flipper.setInAnimation(getActivity(), R.anim.slide_in_left);
		flipper.setOutAnimation(getActivity(), R.anim.slide_out_right);
		flipper.showNext();
	}

	private void setCourseListEnabled(boolean e) {
		courseList.setEnabled(e);

		int colour = e ? Color.WHITE : Color.GRAY;
		courseList.setBackgroundColor(colour);
	}

	private void downloadCourses() {
		//show course spinner
		courseSpinner.setVisibility(View.VISIBLE);
		
		// TODO: Add spinner
		new DownloadCourses(this).execute();
	}

	private void setUpWebView() {
		WebSettings settings = graphView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setBuiltInZoomControls(false);

		graphView.requestFocusFromTouch();
		graphView.setWebViewClient(new WebViewClient());
		graphView.setWebChromeClient(new WebChromeClient());
	}

	private void loadGraph(final int courseId) {
		String url = AcademyProperties.getInstance().getChartingUrl();

		User user = AcademyProperties.getInstance().getUser();
		String postData = WebServiceInterface.getInstance()
				.getAuthPostParameters()
				+ "&course="
				+ courseId
				+ "&user="
				+ user.getId();

		graphView.postUrl(url, EncodingUtils.getBytes(postData, "base64"));
	}

	private void gradesDownloaded(List<ExamGrade> results) {
		//hide spinner
		gradeSpinner.setVisibility(View.GONE);
		
		currentTask = null;
		grades = results;

		// to strings
		String[] gradeStrings = new String[results.size()];
		for (int i = 0; i < gradeStrings.length; i++) {
			gradeStrings[i] = grades.get(i).toString();
		}

		examList.setAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_activated_1, gradeStrings));

		examList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				DialogFragment dialog = null;
				if (isTeacher) {
					// show the proper report
				} else {
					// show the simplified dialog
					dialog = StudentGradeReportDialog.newInstance(grades
							.get(pos));
				}

				dialog.show(getActivity().getSupportFragmentManager(),
						"GRADE_REPORT");
			}
		});
	}

	/**
	 * Called when a course is selected and we can ask which exam they want to
	 * grade
	 * 
	 * @param id
	 */
	public void recordGradesForCourseId(int id) {
		SelectExamDialog dialog = SelectExamDialog.newInstance(id);
		dialog.show(getActivity().getSupportFragmentManager(),
				"SELECT_EXAM_DIALOG");
	}

	/**
	 * Called when we have chosen an exam to record grades for
	 * 
	 * @param id
	 */
	public void recordGradesForExamId(int id) {
		// launch the relevant activity
		final Intent intent = new Intent(getActivity(), RecordGrades.class);

		Bundle b = new Bundle();
		b.putInt("EXAM_ID", id);
		intent.putExtras(b);

		startActivity(intent);
	}

	private void showGraphForCourse(final int pos) {
		// set selected course
		selectedCourse = courses.get(pos);

		// load graph for selected course
		loadGraph(selectedCourse.getId());

		// reset the examlist and start downloading grades
		examList.setAdapter(null);

		downloadExamGrades(courses.get(pos).getId());

		// show exam list on next screen:
		flipper.setInAnimation(getActivity(), R.anim.slide_in_right);
		flipper.setOutAnimation(getActivity(), R.anim.slide_out_left);
		flipper.showNext();
	}

	private void downloadExamGrades(final int courseId) {
		//show spinner 
		gradeSpinner.setVisibility(View.VISIBLE);
		
		// download exam results for this course
		currentTask = new AsyncTask<Void, Void, List<ExamGrade>>() {
			@Override
			protected List<ExamGrade> doInBackground(Void... params) {
				List<ExamGrade> results = WebServiceInterface.getInstance()
						.getGradesForCourse(courseId);
				return results;
			}

			@Override
			protected void onPostExecute(List<ExamGrade> result) {
				super.onPostExecute(result);
				gradesDownloaded(result);
			}
		};
		currentTask.execute();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.progress_fragment, null);
	}

	public void onCoursesDownloaded(ArrayList<Course> result) {
		//hide spinner
		courseSpinner.setVisibility(View.GONE);
		
		//save results
		courses = result;

		String[] strings = new String[courses.size()];
		for (int i = 0; i < courses.size(); i++) {
			strings[i] = courses.get(i).toString();
		}

		courseList.setAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_activated_1, strings));

		courseList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// flip view to specific course
				showGraphForCourse(pos);
			}
		});
	}
}
