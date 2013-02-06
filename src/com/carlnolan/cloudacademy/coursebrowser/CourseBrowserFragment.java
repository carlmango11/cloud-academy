package com.carlnolan.cloudacademy.coursebrowser;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.carlnolan.cloudacademy.asynctasks.DownloadCourses;
import com.carlnolan.cloudacademy.asynctasks.DownloadCourses.OnCoursesDownloadedListener;
import com.carlnolan.cloudacademy.courses.Course;

import com.carlnolan.cloudacademy.R;

public class CourseBrowserFragment extends Fragment {
	private ViewPager mViewPager;
	private CourseTabsAdapter mTabs;
	private PagerTitleStrip mTitle;

	@Override
	public void onStart() {
		super.onStart();

        mViewPager = (ViewPager) getActivity().findViewById(R.id.pager);
        mTitle = (PagerTitleStrip) getActivity().findViewById(R.id.pager_title_strip);
        
        mTabs = new CourseTabsAdapter(getFragmentManager());

        mViewPager.setAdapter(mTabs);
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
		View defaultView = inflater.inflate(R.layout.course_browser_fragment, container, false);

		LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
		p1.weight = 0.5f;
		defaultView.setLayoutParams(p1);
		
		return defaultView;
    }
	
	public static class ArrayListFragment extends Fragment {
        int mNum;

        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
         */
        static ArrayListFragment newInstance(int num) {
            ArrayListFragment f = new ArrayListFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            LinearLayout v = new LinearLayout(getActivity());
            TextView tv = new TextView(getActivity());
            tv.setText(""+mNum);
            v.addView(tv);
            return v;
        }
    }
}
