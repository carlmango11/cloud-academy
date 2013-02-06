package com.carlnolan.cloudacademy.coursebrowser;

import java.util.ArrayList;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.asynctasks.DownloadSections;
import com.carlnolan.cloudacademy.courses.Course;
import com.carlnolan.cloudacademy.courses.Section;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CourseTabFragment extends Fragment
	implements DownloadSections.DownloadSectionsResponder {
	private TextView courseTitleView;
	private ListView sectionListView;
	private ListView lessonListView;
	
	private int courseId;
	private String courseTitle;
	private ArrayList<Section> sections;
	
	static CourseTabFragment newInstance(Course course) {
		CourseTabFragment f = new CourseTabFragment();

        Bundle args = new Bundle();
        args.putString("name", course.toString());
        args.putInt("id", course.getId());
        f.setArguments(args);

        return f;
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		courseId = getArguments().getInt("id");
		courseTitle = getArguments().getString("name");
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
		return inflater.inflate(R.layout.course_browser_tab, container, false);
     }

	@Override
	public void onStart() {
		super.onStart();
		
		courseTitleView = (TextView) getActivity().findViewById(R.id.course_browser_title);
		sectionListView = (ListView) getActivity().findViewById(R.id.course_browser_section_list);
		lessonListView = (ListView) getActivity().findViewById(R.id.course_browser_lesson_list);
		
		courseTitleView.setText(courseTitle);

		if(sections == null) {
			new DownloadSections(this, courseId).execute();
		}
	}

	public void onDownloadSectionsComplete(ArrayList<Section> newSections,
			int courseId) {
		sections = newSections;
		String [] sectionStrings = new String[newSections.size()];
    	
    	for(int i=0; i<newSections.size(); i++) {
    		sectionStrings[i] = newSections.get(i).toString();
    	}
		
		sectionListView.setAdapter(new ArrayAdapter<String>(
					getActivity(),
					android.R.layout.simple_list_item_activated_1,
					sectionStrings
				));
	}
	
	public String getTitle() {
		return courseTitle;
	}
}
