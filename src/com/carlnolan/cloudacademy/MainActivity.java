package com.carlnolan.cloudacademy;

import java.util.Calendar;
import java.util.Date;

import com.carlnolan.cloudacademy.coursebrowser.CourseBrowserFragment;
import com.carlnolan.cloudacademy.coursebrowser.CourseBrowserFragment.CourseBrowserLessonSelectedListener;
import com.carlnolan.cloudacademy.courses.Lesson;
import com.carlnolan.cloudacademy.inclass.Exam;
import com.carlnolan.cloudacademy.inclass.ExamViewerFragment;
import com.carlnolan.cloudacademy.inclass.Homework;
import com.carlnolan.cloudacademy.inclass.HomeworkViewerFragment;
import com.carlnolan.cloudacademy.inclass.InClassViewer;
import com.carlnolan.cloudacademy.inclass.LessonViewerFragment;
import com.carlnolan.cloudacademy.planner.DayViewerFragment;
import com.carlnolan.cloudacademy.planner.ScheduleFragment;
import com.carlnolan.cloudacademy.scheduling.Session;
import com.carlnolan.cloudacademy.usermanagement.User;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;
import com.carlnolan.cloudacademy.workload.SwipeRightGestureListener;
import com.carlnolan.cloudacademy.workload.WorkloadBrowserFragment;
import com.carlnolan.cloudacademy.workload.WorkloadListFragment;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MainActivity extends FragmentActivity
		implements ActionBar.TabListener,
		DayViewerFragment.OnScheduleDayChangedListener,
		ScheduleFragment.OnSessionSelectedListener,
		User.GetCurrentUser.OnGetUserCompleteListener,
		CourseBrowserLessonSelectedListener,
		HomeworkViewerFragment.HomeworkViewerCallback,
		WorkloadBrowserFragment.WorkloadDateSelectedListener,
		WorkloadListFragment.WorkloadItemSelectedListener,
		SwipeRightGestureListener.RightSwipeListener {

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    
    private ActionBar actionBar;
    
    /**
     * Container resources ids and framelayouts
     */
    private int leftContainerId;
    private int rightContainerId;
    private FrameLayout leftContainer;
    private FrameLayout rightContainer;
    
    /**
     * Ids for layout types used internally
     */
    private static final int HALF_HALF_CONTAINER_LAYOUT = 0;
    private static final int ONE_CONTAINER_LAYOUT = 1;
    
    /**
     * Fragment tags
     */
    private static final String HOMEWORK_VIEWER_FRAGMENT_TAG = "HOMEWORK_VIEWER_FRAGMENT";
    private static final String EXAM_VIEWER_FRAGMENT_TAG = "EXAM_VIEWER_FRAGMENT";
    
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
        
        //Check if authenticated user is a teacher or pupil
        int userId = WebServiceInterface.getInstance().getUserId();
        new User.GetCurrentUser(this, userId).execute();
    }
    
    /**
     * Override the touch event and pass to my detector
     */
    @Override 
    public boolean onTouchEvent(MotionEvent event){ 
    	gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * Do anything that needed the user info to be downloaded
     */
	public void onUserComplete() {
        // Set up the action bar.
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // For each of the sections in the app, add a tab to the action bar.
        actionBar.addTab(actionBar.newTab().setText(R.string.title_class_tab).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.title_planner_tab).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.title_workload_tab).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.title_progress_tab).setTabListener(this));
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

    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ignore) {
    	Context context = getBaseContext();
    	FragmentManager fragmentManager = getSupportFragmentManager();
    	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // When the given tab is selected, show the tab contents in the container
        if(tab.getText().equals(context.getString(R.string.title_class_tab))) {    		
        	if(browser == null) {
        		browser = CourseBrowserFragment.newInstance(false);
        		fragmentTransaction.replace(leftContainerId, browser);
        	} else {
            	fragmentTransaction.attach(browser);
        	}
        	
        	if(lessonViewer == null) {
        		lessonViewer = LessonViewerFragment.newInstance(false);
        		fragmentTransaction.replace(rightContainerId, lessonViewer);
        	} else {
            	fragmentTransaction.attach(lessonViewer);
        	}

        } else if(tab.getText().equals(context.getString(R.string.title_planner_tab))) {    		
        	if(calendar == null) {
        		calendar = new DayViewerFragment();
        		fragmentTransaction.replace(leftContainerId, calendar);
        	} else {
            	fragmentTransaction.attach(calendar);
        	}
        	
        	if(schedule == null) {
        		schedule = new ScheduleFragment();
        		fragmentTransaction.replace(rightContainerId, schedule);
        	} else {
            	fragmentTransaction.attach(schedule);
        	}
        } else if(tab.getText().equals(context.getString(R.string.title_workload_tab))) {
        	setUpWorkloadLayout(fragmentTransaction);
        } else {
        	
        }
        
        fragmentTransaction.commit();
    }

    private void setUpWorkloadLayout(FragmentTransaction fragmentTransaction) {
    	if(homeworkViewer == null) {
    		if(workloadBrowser == null) {
        		workloadBrowser = WorkloadBrowserFragment.newInstance();
        		fragmentTransaction.replace(leftContainerId, workloadBrowser);
        	} else {
            	fragmentTransaction.attach(workloadBrowser);
        	}
        	
        	if(workloadList == null) {
        		workloadList = WorkloadListFragment.newInstance();
        		fragmentTransaction.replace(rightContainerId, workloadList);
        	} else {
            	fragmentTransaction.attach(workloadList);
        	}
    	} else {
    		if(workloadList == null) {
        		workloadList = WorkloadListFragment.newInstance();
        		fragmentTransaction.replace(leftContainerId, workloadList);
        	} else {
            	fragmentTransaction.attach(workloadList);
        	}
    		fragmentTransaction.attach(homeworkViewer);
    	}
	}

	public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ignore) {
    	Context context = getBaseContext();
    	FragmentManager fragmentManager = getSupportFragmentManager();
    	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // When the given tab is unselected, remove it
        if(tab.getText().equals(context.getString(R.string.title_class_tab))) {        	
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
        }
        
        fragmentTransaction.commit();
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
		Log.d("cloudacademy", "Session selected");

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

    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
    }

    /**
     * Called when the course browser selects a lesson
     */
	public void courseBrowserLessonSelected(Lesson lesson) {
		//show it on the lesson viewer
		lessonViewer.setLesson(lesson);
		lessonViewer.loadLesson();
	}

	public void homeworkCompletionChanged(Homework homework) {
		// TODO Auto-generated method stub
		
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
		//test
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		
		WorkloadListFragment tempInstance = WorkloadListFragment.newInstance();
		homeworkViewer = HomeworkViewerFragment.newInstance();

		//set up animations:
		ft.setCustomAnimations(
				R.anim.slide_in_right,
				R.anim.slide_out_left);
		
		ft.remove(workloadList);
		ft.replace(leftContainerId, tempInstance, HOMEWORK_VIEWER_FRAGMENT_TAG);
		ft.replace(rightContainerId, homeworkViewer);
		
		ft.commit();
		manager.executePendingTransactions();
		
		//set workloadList as this new instance
		workloadList = tempInstance;
	}

	/**
	 * Called when the GestureDetector detects a swipe from left to right
	 */
	public void onSwipeRight() {
		returnToWorkloadBrowser();
	}

	/**
	 * If we are showing the third panel in the workload tab this method
	 * pops the stack to return to normal
	 */
	private void returnToWorkloadBrowser() {
		FragmentManager manager = getSupportFragmentManager();
		
		boolean thirdPanelShowing =
				homeworkViewer != null
				|| manager.findFragmentByTag(EXAM_VIEWER_FRAGMENT_TAG) != null;
		if(thirdPanelShowing) {
			//if we are showing the homework viewer or exam viewer then we
			//want to return to showing the WorkloadBrowser (ie the first panel and hide
			//the third one), we can achieve this by popping from the back stack as the 
			//transaction which added the homework/exam viewer was added to the back stack
			FragmentTransaction ft = manager.beginTransaction();

			//set up animations:
			ft.setCustomAnimations(
					R.anim.slide_in_left,
					R.anim.slide_out_right);
			
        	ft.replace(leftContainerId, workloadBrowser);
			ft.replace(rightContainerId, WorkloadListFragment.newInstance());
			
			ft.commit();
			
			homeworkViewer = null;
		}
	}
}
