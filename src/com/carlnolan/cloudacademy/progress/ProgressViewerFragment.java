package com.carlnolan.cloudacademy.progress;

import java.util.Calendar;

import com.carlnolan.cloudacademy.MainActivity;
import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.configuration.AcademyProperties;
import com.carlnolan.cloudacademy.inclass.AddExamDialog;
import com.carlnolan.cloudacademy.inclass.AttachLessonDialog;
import com.carlnolan.cloudacademy.inclass.AttendanceDialog;
import com.carlnolan.cloudacademy.inclass.HomeworkFromWhereDialog;

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
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.CalendarView.OnDateChangeListener;

public class ProgressViewerFragment extends Fragment {
	private WebView graphView;

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
	        	//record grades
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public void onStart() {
		super.onStart();
		
		graphView = (WebView) getActivity().findViewById(R.id.progress_graph_view);
		setUpWebView();
		
		loadGraph();
	}

	private void setUpWebView() {
		WebSettings settings = graphView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setBuiltInZoomControls(true);
		
		graphView.requestFocusFromTouch();
		graphView.setWebViewClient(new WebViewClient());
		graphView.setWebChromeClient(new WebChromeClient());
	}

	private void loadGraph() {
		String url = AcademyProperties.getInstance().getChartingUrl();
		url += "lineProgress.html";
		
		graphView.loadUrl(url);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        return inflater.inflate(R.layout.progress_fragment, null);
	}
}
