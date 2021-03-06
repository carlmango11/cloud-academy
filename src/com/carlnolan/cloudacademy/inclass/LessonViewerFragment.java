package com.carlnolan.cloudacademy.inclass;

import java.util.List;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.courses.Content;
import com.carlnolan.cloudacademy.courses.Exercise;
import com.carlnolan.cloudacademy.courses.LearningMaterial;
import com.carlnolan.cloudacademy.courses.Lesson;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;
import com.carlnolan.cloudacademy.asynctasks.DownloadExercises;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LessonViewerFragment extends Fragment
	implements DownloadExercises.DownloadExercisesListener,
	Content.ContentDownloadCallback {
	private OnContentSelectedListener callback;
	private Lesson lesson;
	
	/**
	 * True if this view should have its weighting set
	 */
	private boolean weighted;
	
	private TextView noLessonsView;
	
	//None views:
	private View noExercises;
	private View noLearningMaterial;
	
	private TextView title;
	private TextView description;
	private View descriptionBorder;
	private TextView learningMaterialHeader;
	private TextView exercisesHeader;
	private LinearLayout learningMaterialList;
	private LinearLayout exerciseList;
	private ProgressDialog progressDialog;
	
	private List<LearningMaterial> learningMaterial;
	private List<Exercise> exercises;
	
	//Downloading progress bars
	private ProgressBar learningMaterialProgress;
	private ProgressBar exercisesProgress;
	
	//Alpha for "none" views
	private static final int NONE_ALPHA = 70;
	
	public interface OnContentSelectedListener {
		public void onContentSelected(Content content);
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            weighted = getArguments().getBoolean("WEIGHTED");
        }
    }
	
	public static LessonViewerFragment newInstance(boolean weighted) {
		LessonViewerFragment frag = new LessonViewerFragment();
		
		Bundle args = new Bundle();
		args.putBoolean("WEIGHTED", weighted);
		frag.setArguments(args);
		
		return frag;
	}
	
	private void updateLearningMaterialList(List<LearningMaterial> newMaterial) {
		learningMaterialProgress.setVisibility(View.GONE);
    	LayoutInflater inflater = getActivity().getLayoutInflater();
    	learningMaterialList.removeAllViews();
    	
    	for(int i=0; i<newMaterial.size(); i++) {
    		View thisView = inflater.inflate(R.layout.learning_material_list_item, learningMaterialList, false);
    		TextView materialTitle = (TextView)thisView.findViewById(R.id.learning_material_list_item_name);
    		TextView materialDescription = (TextView)thisView.findViewById(R.id.learning_material_description);
    		ImageButton downloadMaterial = (ImageButton)thisView.findViewById(R.id.learning_material_open_content);
    		
    		materialTitle.setText(newMaterial.get(i).toString());
    		materialDescription.setText(newMaterial.get(i).getDescription());
    		
    		if(newMaterial.get(i).getFilename().length() > 0) {
        		addContentClickListener(downloadMaterial, newMaterial.get(i));
        		downloadMaterial.setVisibility(View.VISIBLE);
    		} else {
    			downloadMaterial.setVisibility(View.GONE);
    		}
    		
    		learningMaterialList.addView(thisView);
    	}
    	
    	if(newMaterial.size() == 0) {
    		noLearningMaterial.setVisibility(View.VISIBLE);
    	} else {
    		noLearningMaterial.setVisibility(View.GONE);
    	}
	}
	
	private void updateExerciseList(List<Exercise> newExercises) {
		exercisesProgress.setVisibility(View.GONE);
    	LayoutInflater inflater = getActivity().getLayoutInflater();
    	exerciseList.removeAllViews();
    	
    	for(int i=0; i<newExercises.size(); i++) {
    		View thisView = inflater.inflate(R.layout.exercise_list_item, exerciseList, false);
    		TextView exerciseTitle = (TextView)thisView.findViewById(R.id.exercise_list_item_name);
    		TextView exerciseDescription = (TextView)thisView.findViewById(R.id.exercise_list_item_description);
    		ImageButton downloadExercise = (ImageButton)thisView.findViewById(R.id.exercise_list_open_content);
    		
    		exerciseTitle.setText(newExercises.get(i).toString());
    		exerciseDescription.setText(newExercises.get(i).getDescription());
    		
    		if(newExercises.get(i).getFilename().length() > 0) {
        		addContentClickListener(downloadExercise, newExercises.get(i));
        		downloadExercise.setVisibility(View.VISIBLE);
    		} else {
    			downloadExercise.setVisibility(View.GONE);
    		}
    		
    		exerciseList.addView(thisView);
    	}
    	
    	if(newExercises.size() == 0) {
    		noExercises.setVisibility(View.VISIBLE);
    	} else {
    		noExercises.setVisibility(View.GONE);
    	}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View defaultView = inflater.inflate(R.layout.lesson_viewer, container, false);
		
		if(weighted) {
			LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
			p1.weight = 0.5f;
			defaultView.setLayoutParams(p1);
		}
		
		return defaultView;
	}

	/**
	 * Adds the appropriate listener to the supplied View
	 * @param thisView
	 * @param thisContent
	 */
    private void addContentClickListener(View thisView, final Content thisContent) {
    	OnClickListener thisListener = null;
		if(thisContent.isURL()) {
			thisListener = new OnClickListener() {
				public void onClick(View arg0) {
					thisContent.visit(getActivity());
				}
			};
		} else {
			thisListener = new Content.ContentClickListener(
					thisContent,
					lesson.getId(),
					progressDialog,
					this);
		}
    	
    	thisView.setOnClickListener(thisListener);
	}
    
    private void setNoneAlpha() {
    	Drawable background = noLearningMaterial.getBackground();
    	background.setAlpha(NONE_ALPHA);
    	background = noExercises.getBackground();
    	background.setAlpha(NONE_ALPHA);
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
        noLessonsView = (TextView) getActivity().findViewById(R.id.lesson_viewer_no_lessons);
        
        title = (TextView) getActivity().findViewById(R.id.lesson_title);
		description = (TextView) getActivity().findViewById(R.id.lesson_description);
		descriptionBorder = (View) getActivity().findViewById(R.id.lesson_viewer_description_border);
		learningMaterialList = (LinearLayout) getActivity().findViewById(R.id.material_list);
		exerciseList = (LinearLayout) getActivity().findViewById(R.id.exercise_list);

		//Get download progress bars
		learningMaterialProgress = (ProgressBar) getActivity().findViewById(R.id.learning_material_download_progress);
		exercisesProgress = (ProgressBar) getActivity().findViewById(R.id.exercises_download_progress);
		
		//Get "none" views
		noLearningMaterial = (View) getActivity().findViewById(R.id.lesson_viewer_no_learning_material);
		noExercises = (View) getActivity().findViewById(R.id.lesson_viewer_no_exercises);
        setNoneAlpha();
		
		//Set typeface of headers
		Typeface crayonFont = Typeface.createFromAsset(getActivity().getAssets(), "CrayonCrumble.ttf");  
		learningMaterialHeader = (TextView) getActivity().findViewById(R.id.lesson_viewer_learning_material);
		//learningMaterialHeader.setTypeface(crayonFont);
		exercisesHeader = (TextView) getActivity().findViewById(R.id.lesson_viewer_exercises);
		//exercisesHeader.setTypeface(crayonFont);

		//Show "no lessons selected" message
		showMainScreen(false);
		
		Log.d("carl", "Started Lesson Viewer");
	}
	
	public void setLesson(Lesson lesson0) {
		lesson = lesson0;
	}

	public void loadLesson() {
		title.setText(lesson.getName());
		description.setText(lesson.getDescription());
		clearLists();
		
		//Show loading circles
		learningMaterialProgress.setVisibility(View.VISIBLE);
		exercisesProgress.setVisibility(View.VISIBLE);
		
		//Hide "none" bars
    	noExercises.setVisibility(View.GONE);
    	noLearningMaterial.setVisibility(View.GONE);

		showMainScreen(true);
    	
    	//Download content
		new DownloadLessonMaterial().execute(lesson.getId());
		new DownloadExercises().execute(lesson.getId(), this);
	}
	
	/**
	 * If true sets all the components to visible and the No Lesson Selected to gone
	 */
	private void showMainScreen(boolean show) {
		int mainComps = show ? View.VISIBLE : View.GONE;
		int noLessonsComps = !show ? View.VISIBLE : View.GONE;
		
		title.setVisibility(mainComps);
		description.setVisibility(mainComps);
		descriptionBorder.setVisibility(mainComps);
		learningMaterialHeader.setVisibility(mainComps);
		exercisesHeader.setVisibility(mainComps);
		
		noLessonsView.setVisibility(noLessonsComps);
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

	/**
	 * Called when content has been downloaded
	 */
	public void contentDownloaded(String location) {
		Content.openContent(getActivity(), location);
	}
}
