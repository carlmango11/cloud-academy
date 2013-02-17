package com.carlnolan.cloudacademy;

import java.util.Date;

import com.carlnolan.cloudacademy.configuration.AcademyProperties;
import com.carlnolan.cloudacademy.coursebrowser.CourseBrowserFragment;
import com.carlnolan.cloudacademy.coursebrowser.CourseBrowserFragment.CourseBrowserLessonSelectedListener;
import com.carlnolan.cloudacademy.courses.Content;
import com.carlnolan.cloudacademy.courses.Course;
import com.carlnolan.cloudacademy.courses.CourseListFragment;
import com.carlnolan.cloudacademy.courses.Lesson;
import com.carlnolan.cloudacademy.courses.Section;
import com.carlnolan.cloudacademy.courses.SectionListFragment;
import com.carlnolan.cloudacademy.courses.LessonListFragment;
import com.carlnolan.cloudacademy.inclass.InClassViewer;
import com.carlnolan.cloudacademy.inclass.LessonViewerFragment;
import com.carlnolan.cloudacademy.planner.DayViewerFragment;
import com.carlnolan.cloudacademy.planner.ScheduleFragment;
import com.carlnolan.cloudacademy.scheduling.Session;
import com.carlnolan.cloudacademy.usermanagement.User;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;
import com.carlnolan.cloudacademy.workload.WorkloadBrowser;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends FragmentActivity
		implements ActionBar.TabListener,
		DayViewerFragment.OnScheduleDayChangedListener,
		ScheduleFragment.OnSessionSelectedListener,
		User.GetCurrentUser.OnGetUserCompleteListener,
		CourseBrowserLessonSelectedListener {

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    
    private ActionBar actionBar;
    
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
    
    private WorkloadBrowser workloadBrowser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Check if authenticated user is a teacher or pupil
        int userId = WebServiceInterface.getInstance().getUserId();
        new User.GetCurrentUser(this, userId).execute();
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

    	Log.d("carl", "SWITCH:" + tab.getText());
        // When the given tab is selected, show the tab contents in the container
        if(tab.getText().equals(context.getString(R.string.title_class_tab))) {
        	Log.d("carl", "Entered section");
        	
        	if(browser == null) {
        		browser = CourseBrowserFragment.newInstance(true);
        		fragmentTransaction.add(R.id.main_container, browser);
        	} else {
            	fragmentTransaction.attach(browser);
        	}
        	
        	if(lessonViewer == null) {
        		lessonViewer = LessonViewerFragment.newInstance(true);
        		fragmentTransaction.add(R.id.main_container, lessonViewer);
        	} else {
            	fragmentTransaction.attach(lessonViewer);
        	}

        } else if(tab.getText().equals(context.getString(R.string.title_planner_tab))) {
        	if(calendar == null) {
        		calendar = new DayViewerFragment();
        		fragmentTransaction.add(R.id.main_container, calendar);
        	} else {
            	fragmentTransaction.attach(calendar);
        	}
        	
        	if(schedule == null) {
        		schedule = new ScheduleFragment();
        		fragmentTransaction.add(R.id.main_container, schedule);
        	} else {
            	fragmentTransaction.attach(schedule);
        	}
        } else if(tab.getText().equals(context.getString(R.string.title_workload_tab))) {
        	if(workloadBrowser == null) {
        		workloadBrowser = WorkloadBrowser.newInstance();
        		fragmentTransaction.add(R.id.main_container, workloadBrowser);
        	} else {
            	fragmentTransaction.attach(workloadBrowser);
        	}
        } else {
        	
        }
        
        fragmentTransaction.commit();
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
        } else {
        }
        
        fragmentTransaction.commit();
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
		//lessonViewer.setVisible(true);
	}
}
