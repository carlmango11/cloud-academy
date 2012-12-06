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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class LessonViewerFragment extends Fragment
	implements DownloadExercises.DownloadExercisesListener {
	private OnContentSelectedListener callback;
	private Lesson lesson;
	
	private LinearLayout headingPanel;
	private LinearLayout contentPanel;
	private TextView noLessonsView;
	private TextView title;
	private TextView description;
	private LinearLayout learningMaterialList;
	private LinearLayout exerciseList;
	private ProgressDialog progressDialog;
	
	private List<LearningMaterial> learningMaterial;
	private List<Exercise> exercises;
	
	//Unsupported Toast duration
	private static final int UNSUPPORTED_FILETYPE_TOAST_DURATION = 4;
	
	public interface OnContentSelectedListener {
		public void onContentSelected(Content content);
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
       /* Intent i = getIntent();
        Bundle b = this.getIntent().getExtras();
        lesson = (Lesson) b.getParcelable("thisLesson");
        returnToSessionViewer = b.getBoolean("returnToSessionViewer");
        
        //Set up action bar:
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);*/
    }
    
   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go up
                Intent intent;
                
                if(returnToSessionViewer) {
                	intent = new Intent(this, SessionOverviewFragment.class);
                } else {
                	intent = new Intent(this, MainActivity.class);
                }
                
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/
	
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
		thisView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//Open the content
		    	new DownloadFile().execute(
		    		thisContent.getURL(lesson.getId()),
		    		thisContent.getFilename());
			}
		});
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
        headingPanel = (LinearLayout) getActivity().findViewById(R.id.lesson_viewer_header_panel);
        contentPanel = (LinearLayout) getActivity().findViewById(R.id.lesson_viewer_content_panel);
        noLessonsView = (TextView) getActivity().findViewById(R.id.lesson_viewer_no_lessons);
        
        title = (TextView) getActivity().findViewById(R.id.lesson_title);
		description = (TextView) getActivity().findViewById(R.id.lesson_description);
		learningMaterialList = (LinearLayout) getActivity().findViewById(R.id.material_list);
		exerciseList = (LinearLayout) getActivity().findViewById(R.id.exercise_list);

		Log.d("carl", "Started Lesson Viewer");
	}

	void openContent(String path) {
    	File fileToOpen = new File(path);
    	String type = Content.getAndroidType(path);
    
    	//Check if we got a proper type
    	if(type.length() > 0) {
    		//Open file
        	Intent i = new Intent();
        	i.setAction(android.content.Intent.ACTION_VIEW);
        	i.setDataAndType(Uri.fromFile(fileToOpen), type);
        	startActivity(i);
    	} else {
    		//Show unsupported dialog
    		Toast toast = Toast.makeText(getActivity(),
    				R.string.unsupported_filetype_error,
    				UNSUPPORTED_FILETYPE_TOAST_DURATION);
    		toast.show();
    	}
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
		
		new DownloadLessonMaterial().execute(lesson.getId());
		new DownloadExercises().execute(lesson.getId(), this);
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
	
	private class DownloadFile extends AsyncTask<String, Integer, String> {
	    @Override
	    protected String doInBackground(String... sUrl) {
	    	String result = "";
	    	
	        try {
	            URL url = new URL(sUrl[0]);
	            URLConnection connection = url.openConnection();
	            connection.connect();
	            // this will be useful so that you can show a typical 0-100% progress bar
	            int fileLength = connection.getContentLength();

	            // download the file
	            InputStream input = new BufferedInputStream(url.openStream());
	            
	            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	            String newFileLocation = dir + "/" + sUrl[1];
	            OutputStream output = new FileOutputStream(newFileLocation);

	            byte data[] = new byte[1024];
	            long total = 0;
	            int count;
	            while ((count = input.read(data)) != -1) {
	                total += count;
	                // publishing the progress....
	                publishProgress((int) (total * 100 / fileLength));
	                output.write(data, 0, count);
	            }

	            output.flush();
	            output.close();
	            input.close();
	            result = newFileLocation;
	        } catch (Exception e) {
	        }
	        return result;
	    }
		
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        progressDialog.show();
	    }

	    @Override
		protected void onPostExecute(String path) {
			super.onPostExecute(path);
			progressDialog.dismiss();
			
			if(path.length() > 0) {
				openContent(path);
			}
		}

		@Override
	    protected void onProgressUpdate(Integer... progress) {
	        super.onProgressUpdate(progress);
	        progressDialog.setProgress(progress[0]);
	    }
	}

	public void setVisible(boolean b) {
		int mainVis = b ? View.VISIBLE : View.GONE;
		int noLessonVis = !b ? View.VISIBLE : View.GONE;
		
		headingPanel.setVisibility(mainVis);
		contentPanel.setVisibility(mainVis);
		noLessonsView.setVisibility(noLessonVis);
	}
}
