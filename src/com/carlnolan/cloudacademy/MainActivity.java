package com.carlnolan.cloudacademy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.carlnolan.cloudacademy.configuration.AcademyProperties;
import com.carlnolan.cloudacademy.coursebrowser.CourseBrowserFragment;
import com.carlnolan.cloudacademy.coursebrowser.CourseBrowserFragment.CourseBrowserLessonSelectedListener;
import com.carlnolan.cloudacademy.courses.Lesson;
import com.carlnolan.cloudacademy.inclass.Exam;
import com.carlnolan.cloudacademy.inclass.ExamViewerFragment;
import com.carlnolan.cloudacademy.inclass.Homework;
import com.carlnolan.cloudacademy.inclass.HomeworkViewerFragment;
import com.carlnolan.cloudacademy.inclass.InClassViewer;
import com.carlnolan.cloudacademy.inclass.LessonViewerFragment;
import com.carlnolan.cloudacademy.notifications.HomeworkAssignedNotification;
import com.carlnolan.cloudacademy.planner.DayViewerFragment;
import com.carlnolan.cloudacademy.planner.ScheduleFragment;
import com.carlnolan.cloudacademy.progress.ProgressViewerFragment;
import com.carlnolan.cloudacademy.progress.SelectExamDialog;
import com.carlnolan.cloudacademy.progress.SelectCourseDialog;
import com.carlnolan.cloudacademy.scheduling.Session;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;
import com.carlnolan.cloudacademy.workload.SwipeRightGestureListener;
import com.carlnolan.cloudacademy.workload.WorkloadBrowserFragment;
import com.carlnolan.cloudacademy.workload.WorkloadListFragment;
import com.google.android.gcm.GCMRegistrar;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MainActivity extends FragmentActivity
		implements ActionBar.TabListener,
		DayViewerFragment.OnScheduleDayChangedListener,
		ScheduleFragment.OnSessionSelectedListener,
		CourseBrowserLessonSelectedListener,
		HomeworkViewerFragment.HomeworkViewerCallback,
		WorkloadBrowserFragment.WorkloadDateSelectedListener,
		WorkloadListFragment.WorkloadItemSelectedListener,
		SwipeRightGestureListener.RightSwipeListener,
		SelectCourseDialog.CourseSelectedCallback,
		SelectExamDialog.ExamToGradeSelectedListener {

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    public static final String PRESET_TAB_STRING = "PRESET_TAB";
    public static final String SENDER_ID = "313750621136";
    
    private ActionBar actionBar;
    
    /**
     * Container resources ids and framelayouts
     */
    private int leftContainerId;
    private int rightContainerId;
    private FrameLayout leftContainer;
    private FrameLayout rightContainer;
    
    /**
     * Ids for layout types
     */
    private static final int HALF_HALF_CONTAINER_LAYOUT = 0;
    private static final int ONE_CONTAINER_LAYOUT = 1;
    
    /**
     * These are the fragments for the COURSES tab
     */
    private CourseBrowserFragment browser;
    private LessonViewerFragment lessonViewer;
    
    /**
     * Fragments for PLANNER tab
     */
    private DayViewerFragment calendar;
    private ScheduleFragment schedule;
    
    /**
     * Fragments for WORKLOAD tab
     */
    private WorkloadBrowserFragment workloadBrowser;
    private WorkloadListFragment workloadList;
    private ExamViewerFragment examViewer;
    private HomeworkViewerFragment homeworkViewer;
    private boolean inHomeworkViewer;
    
    /**
     * Fragments for PROGRESS tab
     */
    private ProgressViewerFragment progressFragment;
    
    private GestureDetectorCompat gestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        leftContainerId = R.id.main_container_left_container;
        rightContainerId = R.id.main_container_right_container;
        leftContainer = (FrameLayout) findViewById(leftContainerId);
        rightContainer = (FrameLayout) findViewById(rightContainerId);
        
        //set up gesture recogniser
        SwipeRightGestureListener listener =
        		new SwipeRightGestureListener(this);
        gestureDetector = new GestureDetectorCompat(this, listener);
        
        //homework viewer isnt being shown at the beginning:
        inHomeworkViewer = false;
        
        // Set up the action bar.
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // For each of the sections in the app, add a tab to the action bar.
        actionBar.addTab(actionBar.newTab().setText(R.string.title_syllabus_tab).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.title_planner_tab).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.title_workload_tab).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.title_progress_tab).setTabListener(this));
        
        registerGCM();
        selectPresetTab();
    }

	/**
     * Registers with GCM if we haven't already and then updates the DB with the reg id
     * regardless of whether we already registered. Hopefully that will ensure the reg
     * id in the DB is always correct
     */
    private void registerGCM() {
    	//Make sure GCM is possible:
    	GCMRegistrar.checkDevice(this);
    	GCMRegistrar.checkManifest(this);
    	
    	//Check for existing registration
    	final String regId = GCMRegistrar.getRegistrationId(this);
    	if (regId.equals("")) {
    		GCMRegistrar.register(this, SENDER_ID);
    	} else {
    		new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... arg0) {
		    		WebServiceInterface.getInstance().registerGCMId(regId);
		    		return null;
				}
    		}.execute();
    	}
	}

	/**
     * If we're in the workload tab AND the homework viewer is showing
     * we should return to the workload browser layout
     * otherwise normal back behaviour
     */
	@Override
	public void onBackPressed() {
		if(inWorkloadAndHomeworkViewer()) {
			returnToWorkloadBrowser();
		} else {
			super.onBackPressed();
		}
	}

	/**
     * Override the touch event and pass to my detector
     */
    @Override 
    public boolean onTouchEvent(MotionEvent event){ 
    	gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

	@Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	/**
	 * If the PRESET_TAB variable is set this method will set the tab
	 * to the correct one
	 */
    private void selectPresetTab() {
		Bundle params = getIntent().getExtras();
		if(params == null) {
			return;
		}
		
		String presetTab = params.getString(PRESET_TAB_STRING);
		if(presetTab == null) {
			return;
		}
		
		for(int i=0;i<actionBar.getTabCount();i++) {
			if(actionBar.getTabAt(i).getText().equals(presetTab)) {
				actionBar.setSelectedNavigationItem(i);
				break;
			}
		}
	}

    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ignore) {
    	Context context = getBaseContext();
    	FragmentManager fragmentManager = getSupportFragmentManager();
    	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // When the given tab is selected, show the tab contents in the container
        if(tab.getText().equals(context.getString(R.string.title_syllabus_tab))) {    		
        	setUpSyllabusLayout(fragmentTransaction);
        } else if(tab.getText().equals(context.getString(R.string.title_planner_tab))) {    		
        	setUpPlannerLayout(fragmentTransaction);
        } else if(tab.getText().equals(context.getString(R.string.title_workload_tab))) {
        	setUpWorkloadLayout(fragmentTransaction);
        } else {
    		setLayout(ONE_CONTAINER_LAYOUT);
        	setUpProgressLayout(fragmentTransaction);
        }
        
        fragmentTransaction.commit();
    }

	/**
     * Sets up the transaction to build the syllabus tab layout
     * @param fragmentTransaction
     */
    private void setUpSyllabusLayout(FragmentTransaction fragmentTransaction) {
    	if(browser == null) {
    		browser = CourseBrowserFragment.newInstance(false);
    		fragmentTransaction.add(leftContainerId, browser);
    	} else {
        	fragmentTransaction.attach(browser);
    	}
    	
    	if(lessonViewer == null) {
    		lessonViewer = LessonViewerFragment.newInstance(false);
    		fragmentTransaction.add(rightContainerId, lessonViewer);
    	} else {
        	fragmentTransaction.attach(lessonViewer);
    	}
    }
    
    /**
     * Sets up the transaction to build the planner tab layout
     * @param fragmentTransaction
     */
    private void setUpPlannerLayout(FragmentTransaction fragmentTransaction) {
    	if(calendar == null) {
    		calendar = new DayViewerFragment();
    		fragmentTransaction.add(leftContainerId, calendar);
    	} else {
        	fragmentTransaction.attach(calendar);
    	}
    	
    	if(schedule == null) {
    		schedule = new ScheduleFragment();
    		fragmentTransaction.add(rightContainerId, schedule);
    	} else {
        	fragmentTransaction.attach(schedule);
    	}
    }

    /**
     * Sets up the transaction to build the workload tab layout
     * @param fragmentTransaction
     */
    private void setUpWorkloadLayout(FragmentTransaction fragmentTransaction) {
    	if(workloadBrowser == null) {
    		workloadBrowser = WorkloadBrowserFragment.newInstance();
    		fragmentTransaction.add(leftContainerId, workloadBrowser);
    	}
		
		if(workloadList == null) {
    		workloadList = WorkloadListFragment.newInstance();
    		fragmentTransaction.add(rightContainerId, workloadList);
    	}
    	
    	if(homeworkViewer == null) {
    		homeworkViewer = HomeworkViewerFragment.newInstance();
    		fragmentTransaction.add(rightContainerId, homeworkViewer);
    		fragmentTransaction.detach(homeworkViewer);
    	}
    	//TODO: Optimise the transaction by calling detach later on
    	
    	Calendar preselectedDate = null;
    	//Check to see if we have preselected a date
    	Intent intent = getIntent();
    	String presetDueDateString = intent.getStringExtra(HomeworkAssignedNotification.PRESET_HOMEWORK_DUE_DATE);
		int presetHomeworkId = intent.getIntExtra(HomeworkAssignedNotification.PRESET_HOMEWORK_ID, -1);
    	if(presetDueDateString != null) {
    		//whatever started the activity wants us to go directly to a due date and piece of h/w
    		
    		//parse date:
    		preselectedDate = Calendar.getInstance();
    	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	    try {
    	    	preselectedDate.setTime(sdf.parse(presetDueDateString));
    		} catch (ParseException e) {
    			e.printStackTrace();
    			Log.e("cloudacademy", "Error parsing server response");
    		}
    	    
    	    setTransactionForHomeworkViewer(fragmentTransaction, false);
    	    
    	    //workloadBrowser.setSelectedDate(preselectedDate);
    	    workloadList.setDate(preselectedDate);
    	    workloadList.setSelectedHomework(presetHomeworkId);
    	    
    	    //delete the variable that caused all this so that if the
    	    //user returns to workload tab it wont preselect anything
    	    intent.removeExtra(HomeworkAssignedNotification.PRESET_HOMEWORK_DUE_DATE);
    	}
    	
    	if(inHomeworkViewer) {
    		//workloadlist should already be on the left side and homeworkviewer should be on the right
    		fragmentTransaction.attach(workloadList);
    		fragmentTransaction.attach(homeworkViewer);
    	} else if(preselectedDate == null) {
    		fragmentTransaction.attach(workloadBrowser);
    		fragmentTransaction.attach(workloadList);
    	}
	}
    
    /**
     * Sets up the transaction to build the progress tab
     * I think it will be a lot simpler than the other tabs
     * @param fragmentTransaction
     */
    private void setUpProgressLayout(FragmentTransaction fragmentTransaction) {
    	if(progressFragment == null) {
    		progressFragment = new ProgressViewerFragment();
    		fragmentTransaction.add(leftContainerId, progressFragment);
    	} else {
        	fragmentTransaction.attach(progressFragment);
    	}
	}

	public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ignore) {
    	Context context = getBaseContext();
    	FragmentManager fragmentManager = getSupportFragmentManager();
    	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // When the given tab is unselected, remove it
        if(tab.getText().equals(context.getString(R.string.title_syllabus_tab))) {        	
        	if(browser != null) {
            	fragmentTransaction.detach(browser);
        	}
        	if(lessonViewer != null) {
            	fragmentTransaction.detach(lessonViewer);
        	}
        } else if(tab.getText().equals(context.getString(R.string.title_planner_tab))) {
        	if(calendar != null) {
        		fragmentTransaction.detach(calendar);
        	}
        	if(schedule != null) {
        		fragmentTransaction.detach(schedule);
        	}
        } else if(tab.getText().equals(context.getString(R.string.title_workload_tab))) {
        	if(workloadBrowser != null) {
        		fragmentTransaction.detach(workloadBrowser);
        	}
        	if(workloadList != null) {
        		fragmentTransaction.detach(workloadList);
        	}
        	if(homeworkViewer != null) {
        		fragmentTransaction.detach(homeworkViewer);
        	}
        } else {
        	if(progressFragment != null) {
        		setLayout(HALF_HALF_CONTAINER_LAYOUT);
        		fragmentTransaction.detach(progressFragment);
        	}
        }
        
        fragmentTransaction.commit();
    }

    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
    }
    
    /**
     * Changes the containers layouts, unused atm
     * @param style
     */
    private void setLayout(int style) {
    	if(style == ONE_CONTAINER_LAYOUT) {
    		//just show the left container
    		LinearLayout.LayoutParams leftParams =
    				new LinearLayout.LayoutParams(
    						LinearLayout.LayoutParams.MATCH_PARENT,
    						LinearLayout.LayoutParams.MATCH_PARENT);
    		leftContainer.setLayoutParams(leftParams);
    		
    		LinearLayout.LayoutParams rightParams =
    				new LinearLayout.LayoutParams(0, 0);
    		rightContainer.setLayoutParams(rightParams);
    	} else if(style == HALF_HALF_CONTAINER_LAYOUT) {
    		//50/50 layout
    		LinearLayout.LayoutParams bothParams =
    				new LinearLayout.LayoutParams(
    						0,
    						LinearLayout.LayoutParams.MATCH_PARENT);
    		bothParams.weight = 1;
    		
    		leftContainer.setLayoutParams(bothParams);
    		rightContainer.setLayoutParams(bothParams);
    	}
    }
	
	public void onSessionSelected(Session session) {
		Intent intent = new Intent(this, InClassViewer.class);
		Bundle b = new Bundle();
		b.putParcelable("thisSession", session);
		intent.putExtras(b);
		
		this.startActivity(intent);
	}
	
	public void onScheduleDayChanged(Date newDate) {
		//Update the day schedule fragment if it exists
		Log.d("cloudacademy", "Schedule Day Changed: " + newDate);
		
		if(schedule != null) {
			schedule.setDate(newDate);
		}
	}

    /**
     * Called when the course browser selects a lesson
     */
	public void courseBrowserLessonSelected(Lesson lesson) {
		//show it on the lesson viewer
		lessonViewer.setLesson(lesson);
		lessonViewer.loadLesson();
	}

	/**
	 * Called from the homeworkViewer Fragment in Workload
	 */
	public void homeworkCompletionChanged(Homework homework) {
		workloadList.updateCompletionMarker(homework);
	}

	/**
	 * Called by WorkloadBrowserFragment when a new date has been selected
	 */
	public void onWorkloadDateSelected(Calendar date) {
		workloadList.setDate(date);
	}

	/**
	 * Called by WorkloadListFragment when the user selects an exam
	 * it should show the exam
	 */
	public void onExamSelected(Exam exam) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Called by WorkloadListFragment when the user selects Homework
	 * it should show the Homework
	 */
	public void onHomeworkSelected(Homework homework) {
		if(!inHomeworkViewer) {
			goToHomeworkViewer(null);
		}
		
		//load homework
		homeworkViewer.loadHomework(homework);
	}

	/**
	 * Called when a course is selected from a dialog in the Progress tab
	 */
	public void courseSelected(int id) {
		progressFragment.recordGradesForCourseId(id);
	}

	/**
	 * Called when the user has selected an exam to record grades for 
	 */
	public void examToGradeSelected(int id) {
		progressFragment.recordGradesForExamId(id);
	}

	/**
	 * Called when the GestureDetector detects a swipe from left to right
	 */
	public void onSwipeRight() {
		if(inWorkloadAndHomeworkViewer()) {
			returnToWorkloadBrowser();
		}
	}
	
	private boolean inWorkloadAndHomeworkViewer() {
		ActionBar.Tab tab = actionBar.getSelectedTab();
		
		boolean thirdPanelShowing =
				inHomeworkViewer;
		//the swipe should only happen if we're in the workload tab
		// and the homework viewer is actually showing
		 return tab.getText().equals(getString(R.string.title_workload_tab))
				&& thirdPanelShowing;
	}

	/**
	 * If we are showing the third panel in the workload tab this method
	 * pops the stack to return to normal
	 */
	private void returnToWorkloadBrowser() {
		FragmentManager manager = getSupportFragmentManager();
		
		//if we are showing the homework viewer or exam viewer then we
		//want to return to showing the WorkloadBrowser (ie the first panel and hide
		//the third one), we can achieve this by popping from the back stack as the 
		//transaction which added the homework/exam viewer was added to the back stack
		FragmentTransaction ft = manager.beginTransaction();

		WorkloadListFragment tempInstance = workloadList.cloneInstance();

		//set up animations:
		ft.setCustomAnimations(
				R.anim.slide_in_left,
				R.anim.slide_out_right);

		ft.remove(workloadList);
		ft.add(rightContainerId, tempInstance);
		ft.detach(homeworkViewer);
    	ft.attach(workloadBrowser);
		
		ft.commit();
		manager.executePendingTransactions();
		
		//set workloadList as this new instance
		workloadList = tempInstance;
		
		//no longer in homework viewer
		inHomeworkViewer = false;
	}

	private void goToHomeworkViewer(Calendar presetDueDate) {
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();

		//set up animations:
		ft.setCustomAnimations(
				R.anim.slide_in_right,
				R.anim.slide_out_left);

		setTransactionForHomeworkViewer(ft, true);
		
		ft.commit();
		manager.executePendingTransactions();
	}
	
	/**
	 * Sets up the fragment transaction to transition to the homework viewer layout.
	 * @param ft The transaction to set up
	 * @param clone True if you want to preserve the state of the existing workloadListFragment
	 * by cloning, false if you want/don't mind a new one (better performance)
	 */
	private void setTransactionForHomeworkViewer(FragmentTransaction ft, boolean clone) {
		//New instance of workloadList for moving to left
	    WorkloadListFragment oldInstance = workloadList;
	    
	    if(clone) {
	    	workloadList = oldInstance.cloneInstance();
	    } else {
	    	workloadList = WorkloadListFragment.newInstance();
	    }
	    
		ft.remove(oldInstance);
		ft.add(leftContainerId, workloadList);
		ft.detach(workloadBrowser);
		ft.attach(homeworkViewer);

		//were now in homework viewer
	    inHomeworkViewer = true;
	}
}
