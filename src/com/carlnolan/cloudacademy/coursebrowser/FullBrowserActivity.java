package com.carlnolan.cloudacademy.coursebrowser;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.coursebrowser.CourseBrowserFragment.CourseBrowserLessonSelectedListener;
import com.carlnolan.cloudacademy.courses.Lesson;
import com.carlnolan.cloudacademy.inclass.InClassViewer;
import com.carlnolan.cloudacademy.inclass.LessonViewerFragment;

public class FullBrowserActivity extends FragmentActivity
	implements CourseBrowserLessonSelectedListener {
	private ActionBar actionBar;
	
	private CourseBrowserFragment courseBrowser;
	private LessonViewerFragment lessonViewer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_course_browser);
		
		//Get action bar and enabled 'up' button
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		Bundle bundle = getIntent().getExtras();
		int courseId = bundle.getInt("COURSE_ID");
		int sectionId = bundle.getInt("SECTION_ID");
		int lessonId = bundle.getInt("LESSON_ID");
		
		lessonViewer = LessonViewerFragment.newInstance(false);
		courseBrowser = CourseBrowserFragment.newInstance(false, courseId, sectionId, lessonId);
		
		//Add the lessonViewer
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.full_browser_browser_frame, courseBrowser);
		transaction.add(R.id.full_browser_lesson_viewer_frame, lessonViewer);
		transaction.commit();
	}

    /**
     * Called when the course browser selects a lesson
     */
	public void courseBrowserLessonSelected(Lesson lesson) {
		//show it on the lesson viewer
		lessonViewer.setLesson(lesson);
		lessonViewer.loadLesson();
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	//Close this activity to return to previous screen
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
}
