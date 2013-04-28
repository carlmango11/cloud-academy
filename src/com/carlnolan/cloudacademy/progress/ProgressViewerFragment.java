package com.carlnolan.cloudacademy.progress;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import com.carlnolan.cloudacademy.MainActivity;
import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.asynctasks.DownloadCourses;
import com.carlnolan.cloudacademy.asynctasks.DownloadCourses.OnCoursesDownloadedListener;
import com.carlnolan.cloudacademy.configuration.AcademyProperties;
import com.carlnolan.cloudacademy.courses.Course;
import com.carlnolan.cloudacademy.inclass.AddExamDialog;
import com.carlnolan.cloudacademy.inclass.AttachLessonDialog;
import com.carlnolan.cloudacademy.inclass.AttendanceDialog;
import com.carlnolan.cloudacademy.inclass.HomeworkFromWhereDialog;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.RadioButton;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.ViewFlipper;

public class ProgressViewerFragment extends Fragment
	implements OnCoursesDownloadedListener {
	private WebView graphView;
	private ViewFlipper flipper;
	private ListView courseList;
	private RadioButton all;
	private RadioButton specific;
	private ListView examList;
	private Button back;
	
	private List<Course> courses;

	public static ProgressViewerFragment newInstance() {
		ProgressViewerFragment instance = new ProgressViewerFragment();
		
		return instance;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

	/*@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			callback = (WorkloadDateSelectedListener) activity;
		} catch(ClassCastException e) {
			Log.d("carl", "Could not cast class");
			throw new ClassCastException(activity.toString()
					+ " upcoming! must implement " +
					"WorkloadDateSelectedListener");
		}
	}*/

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.progress_menu, menu);
	    
	    //Only teachers can record grades
	    if(!AcademyProperties.getInstance().getUser().isTeacher()) {
	    	MenuItem iconToRemove = menu.findItem(R.id.progress_record_grades);
	    	iconToRemove.setVisible(false);
	    }
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	        case R.id.progress_record_grades:
	        	//start first dialog
	        	SelectCourseDialog dialog = SelectCourseDialog.newInstance();
	        	dialog.show(getActivity().getSupportFragmentManager(), "SELECT_COURSE_DIALOG");
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public void onStart() {
		super.onStart();
		
		graphView = (WebView) getActivity().findViewById(R.id.progress_graph_view);
		courseList = (ListView) getActivity().findViewById(R.id.progress_course_list);
		flipper = (ViewFlipper) getActivity().findViewById(R.id.progress_flipper);
		all = (RadioButton) getActivity().findViewById(R.id.progress_radio_all);
		specific = (RadioButton) getActivity().findViewById(R.id.progress_radio_specific);
		back = (Button) getActivity().findViewById(R.id.progress_back);
		examList = (ListView) getActivity().findViewById(R.id.progress_exam_list);
		
		setCourseListEnabled(false);
		
		//setup click events for radios
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
				backToAllCourses();
			}
		});
		
		//fire off courses download
		downloadCourses();
		
		setUpWebView();
		loadGraph(-1);
	}
	
	private void backToAllCourses() {
		flipper.setInAnimation(getActivity(),
				R.anim.slide_in_left);
		flipper.setOutAnimation(getActivity(),
				R.anim.slide_out_right);
		flipper.showNext();
	}
	
	private void setCourseListEnabled(boolean e) {
		courseList.setEnabled(e);

		int colour = e ? Color.WHITE : Color.GRAY;
		courseList.setBackgroundColor(colour);
	}
	
	private void downloadCourses() {
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

	private void loadGraph(int courseId) {
		String url = AcademyProperties.getInstance().getChartingUrl();
		url += "gradeProgress.php";
		
		String postData = WebServiceInterface.getInstance().getAuthPostParameters()
				+ "&course=" + courseId
				+ "&user=" + AcademyProperties.getInstance().getUser().getId();
		
		graphView.postUrl(url, EncodingUtils.getBytes(postData, "base64"));
	}

	/**
	 * Called when acourse is selected and we can ask which exam they want to grade
	 * @param id
	 */
	public void recordGradesForCourseId(int id) {
		SelectExamDialog dialog = SelectExamDialog.newInstance(id);
		dialog.show(getActivity().getSupportFragmentManager(), "SELECT_EXAM_DIALOG");
	}

	/**
	 * Called when we have chosen an exam to record grades for
	 * @param id
	 */
	public void recordGradesForExamId(int id) {
		//launch the relevant activity
		final Intent intent = new Intent(getActivity(), RecordGrades.class);
		
		Bundle b = new Bundle();
		b.putInt("EXAM_ID", id);
		intent.putExtras(b);
		
        startActivity(intent);
	}

	private void showGraphForCourse(int pos) {
		//load graph for selected course
		loadGraph(courses.get(pos).getId());
		
		
		
		//show exam list on next screen:
		flipper.setInAnimation(getActivity(),
				R.anim.slide_in_right);
		flipper.setOutAnimation(getActivity(),
				R.anim.slide_out_left);
		flipper.showNext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        return inflater.inflate(R.layout.progress_fragment, null);
	}

	public void onCoursesDownloaded(ArrayList<Course> result) {
		courses = result;
		
		String [] strings = new String[courses.size()];
		for(int i=0;i<courses.size();i++) {
			strings[i] = courses.get(i).toString();
		}
		
		courseList.setAdapter(new ArrayAdapter<String>(
					getActivity(),
					android.R.layout.simple_list_item_activated_1,
					strings
				));
		
		courseList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				//flip view to specific course
				showGraphForCourse(pos);
			}
		});
	}
}
