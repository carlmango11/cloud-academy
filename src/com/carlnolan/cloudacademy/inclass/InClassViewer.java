package com.carlnolan.cloudacademy.inclass;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.carlnolan.cloudacademy.MainActivity;
import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.asynctasks.AssignHomework;
import com.carlnolan.cloudacademy.asynctasks.AttachLessons;
import com.carlnolan.cloudacademy.asynctasks.CheckAttendanceIsTaken;
import com.carlnolan.cloudacademy.courses.Exercise;
import com.carlnolan.cloudacademy.courses.Lesson;
import com.carlnolan.cloudacademy.scheduling.Session;

public class InClassViewer extends Activity
	implements SessionOverviewFragment.OnInClassItemSelectedListener,
	AttachLessonDialog.OnLessonsAttachedListener,
	AttachLessons.OnLessonsAttachedListener,
	AttendanceDialog.AttendanceTakenListener,
	CheckAttendanceIsTaken.OnAttendanceTakenListener,
	HomeworkFromWhereDialog.FromWhereSelectedListener,
	SelectLessonDialog.OnLessonSelectedFromDialogListener,
	SelectExercisesDialog.FromWhereSelectedListener,
	DueDateDialog.HomeworkDueDateSelectedListener,
	AssignHomework.OnHomeworkAssignedListener,
	CustomHomeworkDialog.CustomHomeworkListener,
	Exercise.OnExerciseCreatedListener,
	HomeworkViewerFragment.HomeworkViewerCallback {
	
    private ActionBar actionBar;
    private MenuItem attendanceMenuItem;
    private ProgressDialog progressDialog;
    
    /**
     * These are the fragments used in InClassViewer
     */
    private SessionOverviewFragment overview;
    private LessonViewerFragment lessonViewer;
    private HomeworkViewerFragment homeworkViewer;
    private ExamViewerFragment examViewer;
    private Fragment currentContentFragment;
    
    private Session currentSession;
    /**
     * This variable stores the exercises to be set as HW.
     * Its a messy way to do it, if the user cancels assigning hw
     * halfway through then were left with this. The alternative
     * is parcelling the list into the DUeDateDialog but that would
     * prob be a big performance hit.
     */
    private List<Exercise> assignableExercises;
	
    /**
     * Ids for different dialogs
     */
	private static final int DIALOG_ATTACH_LESSON = 0;
	
	/**
	 * Toast durations
	 */
	private static final int HOMEWORK_ASSIGNED_TOAST_DURATION = 4;
	
	/**
	 * Drawable for green attendance icon
	 */
	private final int ATTENDANCE_TAKEN_ICON_ID = R.drawable.take_attendance_green;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.in_class_layout);

        // Set up the action bar.
        actionBar = getActionBar();

        //get passed session
        Bundle b = this.getIntent().getExtras();
        currentSession = (Session) b.getParcelable("thisSession");
        
        overview = (SessionOverviewFragment)
        		getFragmentManager().findFragmentById(R.id.inclass_overview_fragment);
        lessonViewer = new LessonViewerFragment();
        homeworkViewer = new HomeworkViewerFragment();
        examViewer = new ExamViewerFragment();
		currentContentFragment = lessonViewer;

        //Add the lessonViewer
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.add(R.id.inclass_content_frame, lessonViewer);
		transaction.commit();

        //load session
        overview.setSession(currentSession);
    }

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go up
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.attach_lesson:
            	AttachLessonDialog.newInstance(currentSession.getCourseId())
            		.show(getFragmentManager(), "attachLessonDialog");
            	return true;
            case R.id.take_attendance:
            	AttendanceDialog.newInstance(currentSession.getClassId(), currentSession.getId())
            		.show(getFragmentManager(), "takeAttendanceDialog");
            	return true;
            case R.id.assign_homework:
            	
            	HomeworkFromWhereDialog fromWhereDialog =
            			new HomeworkFromWhereDialog(
            					this,
            					overview.getSelectedLesson());
            	
            	fromWhereDialog.show(getFragmentManager(), "homeworkFromWhereDialog");
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.session_menu, menu);

	    attendanceMenuItem = menu.findItem(R.id.take_attendance);
        //check if attendance has been taken:
        new CheckAttendanceIsTaken().execute(currentSession.getId(), this);
	    
	    if(!currentSession.isLedByCurrentUser()) {
		    MenuItem iconToRemove = menu.findItem(R.id.attach_lesson);
		    iconToRemove.setVisible(false);

		    iconToRemove = menu.findItem(R.id.assign_homework);
		    iconToRemove.setVisible(false);
		    
		    attendanceMenuItem.setVisible(false);
	    }
	    
	    return true;
	}
	
	/**
	 * Called when showDialog is called. The id of the dialog called
	 * is passed
	 */
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog;
	    
	    switch(id) {
	    case DIALOG_ATTACH_LESSON:
	    	dialog = null;
	        break;
	    default:
	        dialog = null;
	    }
	    
	    return dialog;
	}

	/**
	 * Called by the LessonViewer fragment when the user chooses
	 * to attach lessons to the current session
	 */
	public void onLessonsAttached(Set<Lesson> lessons) {
		ArrayList<Pair<Integer,Integer>> ls = new ArrayList<Pair<Integer,Integer>>();
		
		Iterator<Lesson> i = lessons.iterator();
		while(i.hasNext()) {
			ls.add(new Pair<Integer, Integer>(currentSession.getId(), i.next().getId()));
		}
		
		new AttachLessons().execute(ls, this);
	}

	/**
	 * Called by a fragment when a new lesson is selected
	 */
	public void onLessonSelected(Lesson lesson) {
		if(currentContentFragment != lessonViewer) {
			//Set the currentContentFragment as this:
			currentContentFragment = lessonViewer;
			
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.inclass_content_frame, lessonViewer);
			transaction.commit();

			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.executePendingTransactions();
		}

		//Set the lesson
		lessonViewer.setLesson(lesson);
		lessonViewer.loadLesson();
		lessonViewer.setVisible(true);
	}

	/**
	 * Called by the SessionOverview when homework is selected
	 */
	public void onHomeworkSelected(Homework homework) {
		if(currentContentFragment != homeworkViewer) {
			//Set the currentContentFragment as this:
			currentContentFragment = homeworkViewer;
			
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.inclass_content_frame, homeworkViewer);
			transaction.commit();

			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.executePendingTransactions();
		}
		
		homeworkViewer.loadHomework(homework);
	}

	/**
	 * Called by SessionOverview when an exam is selected
	 */
	public void onExamSelected(Exam exam) {
		if(currentContentFragment != examViewer) {
			//Set the currentContentFragment as this:
			currentContentFragment = examViewer;
			
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.inclass_content_frame, examViewer);
			transaction.commit();

			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.executePendingTransactions();
		}
		
		examViewer.loadExam(exam);
	}
	
	/**
	 * Makes the lesson viewer in-visible
	 */
	public void setLessonViewerVisibile(boolean b) {
		if(b) {
			lessonViewer.setVisible(true);
		} else {
			lessonViewer.setVisible(false);
		}
	}

	/**
	 * Called when AttachLessons returns
	 */
	public void onLessonsAttachComplete() {
		overview.loadSession();
	}

	/**
	 * Called when the attendance has been taken
	 * Sets the icon to green
	 */
	public void onAttendanceTaken() {
		Drawable attendanceTakenIcon =
				this.getResources().getDrawable(ATTENDANCE_TAKEN_ICON_ID);
		attendanceMenuItem.setIcon(attendanceTakenIcon);
	}

	/**
	 * Called when we get a result on whether attendance has
	 * been taken or not
	 */
	public void onAttendanceTakenResultReturned(boolean taken) {
		//If it has been taken call the method which sets the icon green
		if(taken) {
			onAttendanceTaken();
		}
	}

	/**
	 * Called if the user wants to select an exercise from this lesson
	 */
	public void onFromThisLessonSelected(Lesson selectedLesson) {
		showSelectExerciseDialog(selectedLesson);
	}

	/**
	 * Called if the user wants to select an exercise from a different
	 * lesson in the course
	 */
	public void onDifferentLessonSelected() {
		SelectLessonDialog selectLessonDialog =
				SelectLessonDialog.newInstance(currentSession.getCourseId());
		selectLessonDialog.show(getFragmentManager(), "selectLessonDialog");
	}

	/**
	 * Called if the user wishes to create their own homework piece
	 */
	public void onCustomSelected() {
		CustomHomeworkDialog customHomeworkDialog =
				CustomHomeworkDialog.newInstance();
		customHomeworkDialog.show(getFragmentManager(), "customHomeworkDialog");
	}

	/**
	 * Called from the SelectLessonDialog returns
	 */
	public void onLessonSelectedFromDialog(Lesson selectedLesson) {
		showSelectExerciseDialog(selectedLesson);
	}

	private void showSelectExerciseDialog(Lesson selectedLesson) {
		SelectExercisesDialog selectExercisesDialog =
				SelectExercisesDialog.newInstance(selectedLesson.getId());
		selectExercisesDialog.show(getFragmentManager(), "selectExercisesDialog");
	}

	/**
	 * Called when SelectedExercisesDialog returns
	 */
	public void onExercisesSelected(List<Exercise> exercises) {
		//Get a due date and then we're ready to write to the db
		assignableExercises = exercises;
		
		DueDateDialog dueDateDialog =
				DueDateDialog.newInstance();
		dueDateDialog.show(getFragmentManager(), "dueDateDialog");
	}

	/**
	 * Gets called after user has selected a specific date that the homework
	 * will be due for
	 */
	public void onDueDateSelected(int date, int month, int year) {
		String dueDate = year + "-" + (month + 1) + "-" + date;
		
		showProgressDialog(true);
		new AssignHomework(
				this,
				dueDate,
				assignableExercises,
				currentSession.getClassId(),
				currentSession.getCourseId()).execute();
	}

	/**
	 * Gets called if the user selects "next class" as the due date.
	 */
	public void onNextClassSelected() {
		showProgressDialog(true);
		new AssignHomework(
				this,
				"",
				assignableExercises,
				currentSession.getClassId(),
				currentSession.getCourseId()).execute();
	}

	/**
	 * Called when homework assigning is completely finished
	 */
	public void onHomeworkAssigned(boolean success) {
		showProgressDialog(false);
		if(success) {
			Toast toast = Toast.makeText(InClassViewer.this,
					R.string.homework_assigned_successfully,
					HOMEWORK_ASSIGNED_TOAST_DURATION);
			toast.show();
		} else {
			AlertDialog.Builder problemDialog =
					new AlertDialog.Builder(InClassViewer.this);
			
			problemDialog.setMessage(R.string.no_upcoming_session_found)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		}
	}
	
	/**
	 * Shows and closes the progress dialog
	 * @param show True to show, false to hide
	 */
	private void showProgressDialog(boolean show) {
		if(show) {
			progressDialog = ProgressDialog.show(InClassViewer.this, "", 
	                "Working. Please wait...", true);
		} else {
			progressDialog.dismiss();
		}
	}

	/**
	 * Called when the user has filled in the CustomHomework dialog
	 */
	public void onCustomHomeworkSelected(String name, String description) {
		//Create the new exercise
		Exercise.addNewExercise(this, name, description);
	}

	/**
	 * Called when the new (probably custom) exercise is created
	 */
	public void onExerciseCreated(Exercise newExercise) {
		//Pass the newly created exercise to the method that will ask the
		//user for a date
		List<Exercise> ls = new ArrayList<Exercise>();
		ls.add(newExercise);
		onExercisesSelected(ls);
	}

	/**
	 * Called by the homeworkViewer when homework has changed completionstate
	 */
	public void homeworkCompletionChanged(Homework homework) {
		overview.updateHomeworkCompletionState(homework);
	}
}
