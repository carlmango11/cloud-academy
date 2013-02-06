package com.carlnolan.cloudacademy.coursebrowser;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.carlnolan.cloudacademy.asynctasks.DownloadCourses;
import com.carlnolan.cloudacademy.asynctasks.DownloadCourses.OnCoursesDownloadedListener;
import com.carlnolan.cloudacademy.asynctasks.DownloadSections.DownloadSectionsResponder;
import com.carlnolan.cloudacademy.asynctasks.DownloadSections;
import com.carlnolan.cloudacademy.coursebrowser.CourseBrowserFragment.ArrayListFragment;
import com.carlnolan.cloudacademy.courses.Course;
import com.carlnolan.cloudacademy.courses.Section;

public class CourseTabsAdapter extends FragmentStatePagerAdapter
	implements OnCoursesDownloadedListener {
	private List<CourseTabFragment> fragments;
	
	public CourseTabsAdapter(FragmentManager fm) {
		super(fm);
		
		fragments = new ArrayList<CourseTabFragment>();
		new DownloadCourses(this).execute();
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public Fragment getItem(int index) {
		return fragments.get(index);
	}
	
	@Override
    public CharSequence getPageTitle(int position) {
        if(fragments == null) {
        	return "N/A";
        }
        
        return fragments.get(position).getTitle();
    }

	public void onCoursesDownloaded(ArrayList<Course> result) {
		//Build a fragment for each cou
		for(int i=0;i<result.size();i++) {
			CourseTabFragment thisTab = CourseTabFragment.newInstance(result.get(i));
			fragments.add(thisTab);
		}
		
		notifyDataSetChanged();
	}
}
