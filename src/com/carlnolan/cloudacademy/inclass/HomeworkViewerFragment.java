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
	Homework.UpdateHomeworkCompletionCallback {
	private Homework currentHomework;
	private HomeworkViewerCallback callback;
	
	private RelativeLayout contentPanel;
	
	private TextView title;
	private TextView description;
	private TextView dueDate;
	private TextView teacherName;
	private TextView inText;
	private Button completed;
	private Button lesson;
	private Button course;
	private ImageButton content;
	
	private ProgressDialog progressDialog;
	private ProgressBar completionProgress;
	
	public interface HomeworkViewerCallback {
		public void homeworkCompletionChanged(Homework homework);
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
		inText = (TextView) getActivity().findViewById(R.id.homework_in_text);
		
		lesson = (Button) getActivity().findViewById(R.id.homework_lesson_button);
		course = (Button) getActivity().findViewById(R.id.homework_course_button);
		content = (ImageButton) getActivity().findViewById(R.id.homework_content_button);
		
		completionProgress = (ProgressBar) getActivity().findViewById(R.id.homework_completion_progress);
		
		setFonts();
		
		Log.d("carl", "Started Lesson Viewer");
	}
	
	/**
	 * Called to change the state of this homework to
	 * completed/not completed.
	 * @param c
	 */
	private void setCompleted(boolean c) {
		currentHomework.setIsCompleteAndUpdate(c, this);
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
		
		completed.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ConfirmCompletionDialog confirmDialog
					= new ConfirmCompletionDialog();
				confirmDialog.show(getFragmentManager(), "confirmDialog");
			}
		});
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
		description.setText(currentHomework.getDescription());
		
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
		if(currentHomework.getAccompanyingLessonName() != null) {
			lesson.setText(currentHomework.getAccompanyingLessonName());
			
			//Make visible (may be invisible from other h/w
			lesson.setVisibility(View.VISIBLE);
			inText.setVisibility(View.VISIBLE);
			
			lesson.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), FullBrowserActivity.class);
					
					intent.putExtra("COURSE_ID", currentHomework.getCourseId());
					intent.putExtra("SECTION_ID", currentHomework.getAccompanyingSectionId());
					intent.putExtra("LESSON_ID", currentHomework.getAccompanyingLessonId());
					
					startActivity(intent);
				}
			});
		} else {
			//Homework is custom so doesnt have a lesson attached, hide buttons
			lesson.setVisibility(View.GONE);
			inText.setVisibility(View.GONE);
		}
		
		//Set up the course button
		course.setText(currentHomework.getCourseName());
		
		//set up click listener for course:
		course.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), FullBrowserActivity.class);
				
				intent.putExtra("COURSE_ID", currentHomework.getCourseId());
				intent.putExtra("SECTION_ID", -1);
				intent.putExtra("LESSON_ID", -1);
				
				startActivity(intent);
			}
		});
		
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
	
	/**
	 * Dialog which asks user to confirm that they have finished a
	 * piece of homework.
	 * @author Carl
	 *
	 */
	private class ConfirmCompletionDialog extends DialogFragment {
		@Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
			
			int alertMessageId;
			if(currentHomework.isComplete()) {
				alertMessageId = R.string.homework_confirm_noncompletion;
			} else {
				alertMessageId = R.string.homework_confirm_completion;
			}
			
			alert.setTitle(alertMessageId)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						completed.setVisibility(View.GONE);
						completionProgress.setVisibility(View.VISIBLE);
						setCompleted(!currentHomework.isComplete());
					}
				}).setNegativeButton("No", null);
			
			return alert.create();
		}
	}
}
