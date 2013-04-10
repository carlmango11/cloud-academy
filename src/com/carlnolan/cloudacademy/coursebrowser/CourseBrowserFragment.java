package com.carlnolan.cloudacademy.coursebrowser;

import java.util.ArrayList;
import java.util.List;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;

import com.carlnolan.cloudacademy.asynctasks.DownloadCourses;
import com.carlnolan.cloudacademy.asynctasks.DownloadCourses.OnCoursesDownloadedListener;
import com.carlnolan.cloudacademy.asynctasks.DownloadLessons;
import com.carlnolan.cloudacademy.asynctasks.DownloadSections;
import com.carlnolan.cloudacademy.courses.Course;
import com.carlnolan.cloudacademy.courses.Lesson;
import com.carlnolan.cloudacademy.courses.Section;
import com.carlnolan.cloudacademy.inclass.SessionOverviewFragment.OnInClassItemSelectedListener;

import com.carlnolan.cloudacademy.R;

public class CourseBrowserFragment extends Fragment implements
		OnCoursesDownloadedListener,
		DownloadSections.DownloadSectionsResponder,
		DownloadLessons.DownloadLessonsResponder {
	private CourseBrowserLessonSelectedListener callback;

	/** Boolean which decides whether fragment should 'weight' itself */
	private boolean weighted;

	// ids of courses/lessons to select when they become available
	private int selectedCourseId;
	private int selectedSectionId;
	private int selectedLessonId;

	private List<Course> courses;
	private List<Section> sections;
	private List<Lesson> lessons;
	private Course selectedCourse;
	private Section selectedSection;
	private Lesson selectedLesson;
	private ViewFlipper flipper;

	// Course names list stuff:
	private ListView courseList;

	// Section/lesson lists stuff:
	private ListView sectionListView;
	private ListView lessonListView;
	private TextView courseTitle;
	private Button backButton;

	public interface CourseBrowserLessonSelectedListener {
		public void courseBrowserLessonSelected(Lesson lesson);
	}

	public static CourseBrowserFragment newInstance(boolean weighted) {
		return newInstance(weighted, -1, -1, -1);
	}

	public static CourseBrowserFragment newInstance(boolean weighted,
			int courseId, int sectionId, int lessonId) {
		CourseBrowserFragment instance = new CourseBrowserFragment();

		Bundle args = new Bundle();
		args.putInt("COURSE_ID", courseId);
		args.putInt("SECTION_ID", sectionId);
		args.putInt("LESSON_ID", lessonId);
		args.putBoolean("WEIGHTED", weighted);
		instance.setArguments(args);

		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		selectedCourseId = getArguments().getInt("COURSE_ID");
		selectedSectionId = getArguments().getInt("SECTION_ID");
		selectedLessonId = getArguments().getInt("LESSON_ID");
		weighted = getArguments().getBoolean("WEIGHTED");
	}

	@Override
	public void onStart() {
		super.onStart();

		flipper = (ViewFlipper) getActivity().findViewById(
				R.id.course_browser_flipper);

		sectionListView = (ListView) getActivity().findViewById(
				R.id.course_browser_section_list);
		lessonListView = (ListView) getActivity().findViewById(
				R.id.course_browser_lesson_list);
		courseTitle = (TextView) getActivity().findViewById(
				R.id.course_browser_course_title);
		backButton = (Button) getActivity().findViewById(
				R.id.course_browser_back_button);

		courseList = (ListView) getActivity().findViewById(
				R.id.course_browser_courses_list);

		// set ListView adapters:
		sections = new ArrayList<Section>();
		lessons = new ArrayList<Lesson>();
		lessonListView.setAdapter(new ArrayAdapter<Lesson>(getActivity(),
				android.R.layout.simple_list_item_activated_1, lessons));
		sectionListView.setAdapter(new ArrayAdapter<Section>(getActivity(),
				android.R.layout.simple_list_item_activated_1, sections));

		// set listviews selection mode:
		sectionListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		sectionListView.setItemChecked(0, true);
		lessonListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				flipper.setInAnimation(getActivity(),
						android.R.anim.slide_in_left);
				flipper.setOutAnimation(getActivity(),
						android.R.anim.slide_out_right);
				flipper.showNext();
			}
		});

		// download courses
		new DownloadCourses(this).execute();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View defaultView = inflater.inflate(R.layout.course_browser_fragment,
				container, false);

		if (weighted) {
			LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(0,
					LinearLayout.LayoutParams.MATCH_PARENT);
			p1.weight = 0.5f;
			defaultView.setLayoutParams(p1);
		}

		return defaultView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			callback = (CourseBrowserLessonSelectedListener) activity;
		} catch (ClassCastException e) {
			Log.d("carl", "Could not cast class");
			throw new ClassCastException(
					activity.toString()
							+ " upcoming! must implement CourseBrowserLessonSelectedListener");
		}
	}

	/**
	 * Called when a course has been selected from the list
	 * 
	 * @param course
	 */
	private void courseSelected(Course course) {
		selectedCourse = course;

		flipper.setInAnimation(inFromRightAnimation());
		flipper.setOutAnimation(outToLeftAnimation());
		flipper.showNext();

		// set up new view
		courseTitle.setText(selectedCourse.toString());
		new DownloadSections(this, selectedCourse.getId()).execute();
	}

	/**
	 * Called when a section has been selected from the list
	 * 
	 * @param course
	 */
	private void sectionSelected(Section section) {
		selectedSection = section;
		new DownloadLessons(this, selectedSection.getId()).execute();
	}

	/**
	 * Called when a lesson has been selected from the list
	 * 
	 * @param course
	 */
	private void lessonSelected(Lesson lesson) {
		selectedLesson = lesson;
		callback.courseBrowserLessonSelected(lesson);
	}

	public void onCoursesDownloaded(ArrayList<Course> result) {
		courses = result;
		
		CourseBrowserCourseAdapter adapter =
				new CourseBrowserCourseAdapter(getActivity(),
						R.layout.course_browser_course_item,
						courses);
		courseList.setAdapter(adapter);
		courseList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				courseSelected(courses.get(position));
			}
		});

		// do a check to see if any of these courses have been pre-selected
		if (selectedCourseId != -1) {
			for (Course c : result) {
				if (c.getId() == selectedCourseId) {
					courseSelected(c);
				}
			}
		}
	}

	public void onDownloadSectionsComplete(ArrayList<Section> newSections,
			int courseId) {
		sections = newSections;

		sectionListView.setAdapter(new ArrayAdapter<Section>(getActivity(),
				android.R.layout.simple_list_item_activated_1, newSections));
		sectionListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				sectionListView.setItemChecked(position, true);
				sectionSelected(sections.get(position));
			}
		});

		// Reset Lessons list view also:
		lessons.clear();
		notifyListViewChanged(lessonListView);

		// do a check to see if any of these sections have been pre-selected
		boolean foundSelected = false;
		if (selectedSectionId != -1) {
			for (int i = 0; i < sections.size(); i++) {
				if (sections.get(i).getId() == selectedSectionId) {
					sectionSelected(sections.get(i));
					sectionListView.setItemChecked(i, true);
					selectedSectionId = -1;
					foundSelected = true;
					break;
				}
			}
		}

		// If we have more than one section and selectedSectionId wasnt found
		if (newSections.size() > 0 && !foundSelected) {
			// set the first one checked:
			sectionListView.setItemChecked(0, true);
			sectionSelected(sections.get(0));
		}
	}

	private void notifyListViewChanged(ListView listView) {
		ArrayAdapter adapter = ((ArrayAdapter) listView.getAdapter());
		adapter.notifyDataSetChanged();
	}

	public void onDownloadLessonsComplete(ArrayList<Lesson> result) {
		lessons = result;

		lessonListView.setAdapter(new ArrayAdapter<Lesson>(getActivity(),
				android.R.layout.simple_list_item_activated_1, lessons));
		lessonListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				lessonListView.setItemChecked(position, true);
				lessonSelected(lessons.get(position));
			}
		});

		// do a check to see if any of these sections have been pre-selected
		if (selectedLessonId != -1) {
			for (int i = 0; i < lessons.size(); i++) {
				if (lessons.get(i).getId() == selectedLessonId) {
					lessonListView.setItemChecked(i, true);
					lessonSelected(lessons.get(i));
					selectedLessonId = -1;
					break;
				}
			}
		}
	}

	/**
	 * Builds an slide transition used by ViewFlipper
	 * @return
	 */
	private Animation inFromRightAnimation() {
		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(250);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}

	/**
	 * Builds an slide transition used by ViewFlipper
	 * @return
	 */
	private Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(250);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}
}
