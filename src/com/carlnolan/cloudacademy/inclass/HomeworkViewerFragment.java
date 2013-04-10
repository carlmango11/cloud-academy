package com.carlnolan.cloudacademy.inclass;

import java.text.Format;
import java.text.SimpleDateFormat;
import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.configuration.AcademyProperties;
import com.carlnolan.cloudacademy.coursebrowser.FullBrowserActivity;
import com.carlnolan.cloudacademy.courses.Content;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeworkViewerFragment extends Fragment
	implements Content.ContentDownloadCallback,
	Homework.UpdateHomeworkCompletionCallback,
	ConfirmCompletionDialog.HomeworkCompleteDialogCallback {
	
	private static final String FROM_LESSON_PREFIX = "Lesson - ";
	private static final String FROM_COURSE_PREFIX = "Course - ";
	
	private Homework currentHomework;
	private HomeworkViewerCallback callback;
	
	private RelativeLayout contentPanel;
	
	private TextView title;
	private TextView description;
	private TextView fromLesson;
	private TextView dueDate;
	private TextView teacherName;
	private Button completed;
	private ImageButton goButton;
	private ImageButton content;
	
	private ProgressDialog progressDialog;
	private ProgressBar completionProgress;
	
	public interface HomeworkViewerCallback {
		public void homeworkCompletionChanged(Homework homework);
	}

	public static HomeworkViewerFragment newInstance() {
		HomeworkViewerFragment instance = new HomeworkViewerFragment();
		return instance;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.homework_viewer, container, false);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			callback = (HomeworkViewerCallback) activity;
		} catch(ClassCastException e) {
			Log.d("carl", "Could not cast class");
			throw new ClassCastException(activity.toString()
					+ " upcoming! must implement " +
					"HomeworkViewerFragment.HomeworkViewerCallback");
		}
	}
	
	/**
	 * Set the font to the crayon one for the textViews we want.
	 */
	private void setFonts() {
		Typeface crayonFont = Typeface.createFromAsset(getActivity().getAssets(), "CrayonCrumble.ttf");
		teacherName.setTypeface(crayonFont);
		dueDate.setTypeface(crayonFont);
		completed.setTypeface(crayonFont);
		
		//Others
		TextView temp = (TextView) getActivity()
				.findViewById(R.id.homework_due_text);
		temp.setTypeface(crayonFont);
		temp = (TextView) getActivity().findViewById(R.id.homework_for_text);
		temp.setTypeface(crayonFont);
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
        contentPanel = (RelativeLayout) getActivity().findViewById(R.id.homework_viewer_content_panel);
        
        title = (TextView) getActivity().findViewById(R.id.homework_title);
		description = (TextView) getActivity().findViewById(R.id.homework_description);
		completed = (Button) getActivity().findViewById(R.id.homework_completed_text);
		dueDate = (TextView) getActivity().findViewById(R.id.homework_due_date_text);
		teacherName = (TextView) getActivity().findViewById(R.id.homework_teacher_name);
		fromLesson = (TextView) getActivity().findViewById(R.id.homework_from_lesson);
		
		goButton = (ImageButton) getActivity().findViewById(R.id.homework_go_button);
		content = (ImageButton) getActivity().findViewById(R.id.homework_content_button);
		
		completionProgress = (ProgressBar) getActivity().findViewById(R.id.homework_completion_progress);
		
		//setFonts();
		
		Log.d("carl", "Started Lesson Viewer");
	}
	
	/**
	 * Sets the completed view and requires a boolean
	 * signifing whether the user is a teacher or not
	 */
	private void setCompletedView() {
		completed.setVisibility(View.VISIBLE);
		//Set the completedText
		if(currentHomework.isComplete()) {
			completed.setText(R.string.homework_completed);
			completed.setTextColor(
					getActivity().getResources().getColor(
							R.color.Green));
		} else {
			completed.setText(R.string.homework_not_completed);
			completed.setTextColor(
					getActivity().getResources().getColor(
							R.color.Red));
		}
		
		final ConfirmCompletionDialog.HomeworkCompleteDialogCallback callback =
				this;
		completed.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ConfirmCompletionDialog confirmDialog
					= ConfirmCompletionDialog.newInstance(currentHomework, callback);
				confirmDialog.show(getFragmentManager(), "confirmDialog");
			}
		});
	}

	/**
	 * Called by the ComfirmCompletionDialog when a response is gotten
	 */
	public void onHomeworkCompleteDialogResponse(Homework updatedHomework, boolean complete) {
		completed.setVisibility(View.GONE);
		completionProgress.setVisibility(View.VISIBLE);
		updatedHomework.setIsCompleteAndUpdate(complete, this);
	}

	/**
	 * Called to make the viewer load in a piece of homework
	 * @param homework
	 */
	public void loadHomework(Homework homework) {
		currentHomework = homework;
		
		//Check if teacher or student
		boolean isTeacher =
				AcademyProperties.getInstance().getUser().isTeacher();
		
		title.setText(currentHomework.toString());
		
		//Set up description
		String descriptionString = currentHomework.getDescription();
		if(descriptionString.length() == 0) {
			description.setVisibility(View.GONE);
		} else {
			description.setText(descriptionString);
			description.setVisibility(View.VISIBLE);
		}
		
		if(isTeacher) {
			teacherName.setText(R.string.homework_for_text_teacher);
		} else {
			teacherName.setText(currentHomework.getReceivingTeacher());
		}
		
		//Format date for textView
		Format sdf = new SimpleDateFormat("EEEEEEEEE, d MMMMMMMM yyyy");
		String dueDateText = sdf.format(currentHomework.getDueDate().getTime());
		dueDate.setText(dueDateText);
		
		//Set up lesson button, check if theres a lesson to go with this exercsie
		String fromString = "";
		if(currentHomework.getAccompanyingLessonName() != null) {
			fromString = FROM_LESSON_PREFIX + currentHomework.getAccompanyingLessonName() +"\n";
			
			//show go button
			goButton.setVisibility(View.VISIBLE);
			
			goButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), FullBrowserActivity.class);
					
					intent.putExtra("COURSE_ID", currentHomework.getCourseId());
					intent.putExtra("SECTION_ID", currentHomework.getAccompanyingSectionId());
					intent.putExtra("LESSON_ID", currentHomework.getAccompanyingLessonId());
					
					startActivity(intent);
				}
			});
		} else {
			//hide go button
			goButton.setVisibility(View.GONE);
		}
		
		//Set up "from" label
		fromString += FROM_COURSE_PREFIX + currentHomework.getCourseName();
		fromLesson.setText(fromString);
		
		if(!isTeacher) {
			setCompletedView();
		}
		
		if(currentHomework.getFilename().length() > 0) {
			Content.ContentClickListener thisListener =
	    			new Content.ContentClickListener(
	    					currentHomework,
	    					currentHomework.getAccompanyingLessonId(),
	    					progressDialog,
	    					this);
			content.setOnClickListener(thisListener);
			content.setVisibility(View.VISIBLE);
		} else {
			content.setVisibility(View.GONE);
		}
	}

	/**
	 * Called when the file is downloaded
	 */
	public void contentDownloaded(String location) {
		Content.openContent(getActivity(), location);
	}

	/**
	 * Called when the database has been updated with the new completion
	 * value
	 */
	public void homeworkCompletionUpdated() {
		completionProgress.setVisibility(View.GONE);
		setCompletedView();
		callback.homeworkCompletionChanged(currentHomework);
	}
}
