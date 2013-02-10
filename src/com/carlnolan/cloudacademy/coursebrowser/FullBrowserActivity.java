package com.carlnolan.cloudacademy.coursebrowser;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.coursebrowser.CourseBrowserFragment.CourseBrowserLessonSelectedListener;
import com.carlnolan.cloudacademy.courses.Lesson;
import com.carlnolan.cloudacademy.inclass.LessonViewerFragment;

public class FullBrowserActivity extends FragmentActivity
	implements CourseBrowserLessonSelectedListener {
	private CourseBrowserFragment courseBrowser;
	private LessonViewerFragment lessonViewer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_course_browser);
		
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
	
	/*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go up
                Intent intent = new Intent(this, InClassViewer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/
}
