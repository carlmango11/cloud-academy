package com.carlnolan.cloudacademy.inclass;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class LessonViewerFragment extends Fragment
	implements DownloadExercises.DownloadExercisesListener,
	Content.ContentDownloadCallback {
	private OnContentSelectedListener callback;
	private Lesson lesson;
	
	private ImageView headerView;
	private ImageView footerView;
	private FrameLayout contentPanel;
	private TextView noLessonsView;
	
	private TextView title;
	private TextView description;
	private LinearLayout learningMaterialList;
	private LinearLayout exerciseList;
	private ProgressDialog progressDialog;
	
	private List<LearningMaterial> learningMaterial;
	private List<Exercise> exercises;
	
	public interface OnContentSelectedListener {
		public void onContentSelected(Content content);
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	void updateLearningMaterialList(List<LearningMaterial> newMaterial) {
    	LayoutInflater inflater = getActivity().getLayoutInflater();
    	learningMaterialList.removeAllViews();
    	
    	for(int i=0; i<newMaterial.size(); i++) {
    		View thisView = inflater.inflate(R.layout.learning_material_list_item, null);
    		TextView materialTitle = (TextView)thisView.findViewById(R.id.learning_material_list_item_name);
    		TextView materialDescription = (TextView)thisView.findViewById(R.id.learning_material_description);
    		materialTitle.setText(newMaterial.get(i).toString());
    		materialDescription.setText(newMaterial.get(i).getDescription());
    		
    		addContentClickListener(thisView, newMaterial.get(i));
    		
    		learningMaterialList.addView(thisView);
    	}
	}
	
	void updateExerciseList(List<Exercise> newExercises) { 
    	LayoutInflater inflater = getActivity().getLayoutInflater();
    	exerciseList.removeAllViews();
    	
    	for(int i=0; i<newExercises.size(); i++) {
    		View thisView = inflater.inflate(R.layout.exercise_list_item, null);
    		TextView exerciseTitle = (TextView)thisView.findViewById(R.id.exercise_list_item_name);
    		TextView exerciseDescription = (TextView)thisView.findViewById(R.id.exercise_list_item_description);
    		exerciseTitle.setText(newExercises.get(i).toString());
    		exerciseDescription.setText(newExercises.get(i).getDescription());
    		
    		addContentClickListener(thisView, newExercises.get(i));
    		
    		exerciseList.addView(thisView);
    	}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.lesson_viewer, container, false);
	}

	/*@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			callback = (OnContentSelectedListener) activity;
		} catch(ClassCastException e) {
			Log.d("carl", "Could not cast class");
			throw new ClassCastException(activity.toString()
					+ " upcoming! must implement LessonOverviewFragment.OnContentSelectedListener");
		}
		
		thisLesson = new Lesson();
	}*/

    private void addContentClickListener(View thisView, final Content thisContent) {
    	Content.ContentClickListener thisListener =
    			new Content.ContentClickListener(
    					thisContent,
    					lesson.getId(),
    					progressDialog,
    					this);
    	
    	thisView.setOnClickListener(thisListener);
	}
    
	@Override
	public void onStart() {
		super.onStart();
        
        //progress dialog setup:
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Downloading Content...");
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        
        //Bind xml controls:
        headerView = (ImageView) getActivity().findViewById(R.id.lesson_viewer_header);
        contentPanel = (FrameLayout) getActivity().findViewById(R.id.lesson_viewer_content_panel);
        footerView = (ImageView) getActivity().findViewById(R.id.lesson_viewer_footer);
        noLessonsView = (TextView) getActivity().findViewById(R.id.lesson_viewer_no_lessons);
        
        title = (TextView) getActivity().findViewById(R.id.lesson_title);
		description = (TextView) getActivity().findViewById(R.id.lesson_description);
		learningMaterialList = (LinearLayout) getActivity().findViewById(R.id.material_list);
		exerciseList = (LinearLayout) getActivity().findViewById(R.id.exercise_list);

		//Set typeface of headers
		Typeface crayonFont = Typeface.createFromAsset(getActivity().getAssets(), "CrayonCrumble.ttf");  
		TextView header = (TextView) getActivity().findViewById(R.id.lesson_viewer_learning_material);
		header.setTypeface(crayonFont);
		header = (TextView) getActivity().findViewById(R.id.lesson_viewer_exercises);
		header.setTypeface(crayonFont);
		
		Log.d("carl", "Started Lesson Viewer");
	}

	/*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
		View defaultView = inflater.inflate(R.layout.lesson_overview, container, false);
    	
    	LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT);
		p1.weight = 0.5f;
		defaultView.setLayoutParams(p1);
		
		return defaultView;
    }*/
	
	public void setLesson(Lesson lesson0) {
		lesson = lesson0;
	}

	public void loadLesson() {
		title.setText(lesson.getName());
		description.setText(lesson.getDescription());
		clearLists();
		
		new DownloadLessonMaterial().execute(lesson.getId());
		new DownloadExercises().execute(lesson.getId(), this);
	}
	
	/**
	 * Clears out anything in the lists for incoming fresh data
	 * Gets rid of that lag effect while content is d/ling
	 */
	public void clearLists() {
    	learningMaterialList.removeAllViews();
    	exerciseList.removeAllViews();
	}

	/**
	 * Called when the list of downloaded exercises gets returned
	 * asynchroniosly.
	 */
	public void onExercisesDownloaded(List<Exercise> result) {
		exercises = result;
		updateExerciseList(result);
	}	
    
	private class DownloadLessonMaterial extends AsyncTask<Integer, Void, List<LearningMaterial>> {

		@Override
		protected List<LearningMaterial> doInBackground(Integer... params) {
			List<LearningMaterial> ls = WebServiceInterface.getInstance().getLearningMaterial(params[0]);
			return ls;
		}

		@Override
		protected void onPostExecute(List<LearningMaterial> result) {
			super.onPostExecute(result);
			learningMaterial = result;
			updateLearningMaterialList(result);
		}
	}

	public void setVisible(boolean b) {
		int mainVis = b ? View.VISIBLE : View.GONE;
		int noLessonVis = !b ? View.VISIBLE : View.GONE;
		
		headerView.setVisibility(mainVis);
		contentPanel.setVisibility(mainVis);
		footerView.setVisibility(mainVis);
		noLessonsView.setVisibility(noLessonVis);
	}

	/**
	 * Called when content has been downloaded
	 */
	public void contentDownloaded(String location) {
		Content.openContent(getActivity(), location);
	}
}
