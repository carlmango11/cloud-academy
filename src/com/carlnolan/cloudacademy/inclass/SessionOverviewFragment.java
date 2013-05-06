package com.carlnolan.cloudacademy.inclass;

import java.util.ArrayList;
import java.util.List;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.asynctasks.DownloadHomeworkDue;
import com.carlnolan.cloudacademy.asynctasks.DownloadHomeworkDue.DownloadHomeworkDueListener;
import com.carlnolan.cloudacademy.configuration.AcademyProperties;
import com.carlnolan.cloudacademy.courses.Lesson;
import com.carlnolan.cloudacademy.scheduling.Session;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SessionOverviewFragment extends Fragment
	implements DownloadHomeworkDueListener,
	Session.DownloadExamsCallback {
	
	private OnInClassItemSelectedListener callback;
	private Session session;
	
	private TextView courseTitle;
	private TextView leadName;
	private TextView room;
	private TextView time;

	private LinearLayout lessonsToCoverList;
	private LinearLayout homeworkDueList;
	private LinearLayout examList;
	
	//"None" messages
	private TextView noLessons;
	private TextView noHomework;
	private TextView noExams;
	
	private ArrayList<LessonListItem> lessons;
	private ArrayList<HomeworkListItem> homework;
	private ArrayList<ExamListItem> exams;
	private Selectable selectedItem;
	
	private static final int LESSON_BUTTON_BACKGROUND = R.drawable.white_menu_item_gradient;
	private static final int LESSON_CLICKED_BUTTON_BACKGROUND = R.drawable.clicked_white_menu_item_gradient;
	
	public interface OnInClassItemSelectedListener {
		public void onLessonSelected(Lesson lesson);
		public void onHomeworkSelected(Homework homework);
		public void onExamSelected(Exam exam);
	}

	@Override
	public void onStart() {
		super.onStart();
		
        //Hook up all the views:
		courseTitle = (TextView) getActivity().findViewById(R.id.session_overview_course_title);
		leadName = (TextView) getActivity().findViewById(R.id.session_overview_lead_name);
		room = (TextView) getActivity().findViewById(R.id.session_overview_room);
		time = (TextView) getActivity().findViewById(R.id.session_overview_time);
		
		lessonsToCoverList = (LinearLayout) getActivity().findViewById(R.id.lessons_to_cover);
		homeworkDueList = (LinearLayout) getActivity().findViewById(R.id.homework_due);
		examList = (LinearLayout) getActivity().findViewById(R.id.exam_list);
		
		noLessons = (TextView) getActivity().findViewById(R.id.session_overview_no_lessons);
		noHomework = (TextView) getActivity().findViewById(R.id.session_overview_no_homework);
		noExams = (TextView) getActivity().findViewById(R.id.session_overview_no_exams);
        
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        loadSession();

		Log.d("carl", "Started Session Overview");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void updateLessonListList(ArrayList<Lesson> result) {
    	lessonsToCoverList.removeAllViews();
    	LayoutInflater inflater = getActivity().getLayoutInflater();
		lessons = new ArrayList<LessonListItem>();
		
    	for(int i=0; i<result.size(); i++) {
    		TextView thisView = (TextView)inflater.inflate(R.layout.lesson_list_item, null);
    		LessonListItem thisItem = new LessonListItem(result.get(i), thisView);
    		lessons.add(thisItem);
    		
    		lessonsToCoverList.addView(thisItem.getView());
    	}

    	//if empty:
    	if(lessons.size() == 0) {
    		noLessons.setVisibility(View.VISIBLE);
    	} else {
    		noLessons.setVisibility(View.GONE);
    	}

    	//download homework now
		new DownloadHomeworkDue(this).execute(session);
	}

	public void updateHomeworkList(List<Homework> result) {
    	homeworkDueList.removeAllViews();
    	LayoutInflater inflater = getActivity().getLayoutInflater();
		homework = new ArrayList<HomeworkListItem>();
		
    	for(int i=0; i<result.size(); i++) {
    		LinearLayout thisView = (LinearLayout)inflater.inflate(R.layout.homework_list_item, null);
    		HomeworkListItem thisItem = new HomeworkListItem(result.get(i), thisView);
    		homework.add(thisItem);
    		
    		homeworkDueList.addView(thisItem.getView());
    	}
    	
    	//if empty:
    	if(homework.size() == 0) {
    		noHomework.setVisibility(View.VISIBLE);
    	} else {
    		noHomework.setVisibility(View.GONE);
    	}
	}

	public void updateExamList(List<Exam> result) {
		examList.removeAllViews();
    	LayoutInflater inflater = getActivity().getLayoutInflater();
		exams = new ArrayList<ExamListItem>();
		
    	for(int i=0; i<result.size(); i++) {
    		LinearLayout thisView = (LinearLayout)inflater.inflate(R.layout.exam_list_item, null);
    		ExamListItem thisItem = new ExamListItem(result.get(i), thisView);
    		exams.add(thisItem);
    		
    		examList.addView(thisItem.getView());
    	}

    	//if empty:
    	if(exams.size() == 0) {
    		noExams.setVisibility(View.VISIBLE);
    	} else {
    		noExams.setVisibility(View.GONE);
    	}
	}
	
	public Lesson getSelectedLesson() {
		if(selectedItem != null && selectedItem.isLessonListItem()) {
			return ((LessonListItem) selectedItem).getLesson();
		}
		return null;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			callback = (OnInClassItemSelectedListener) activity;
		} catch(ClassCastException e) {
			Log.d("carl", "Could not cast class");
			throw new ClassCastException(activity.toString()
					+ " upcoming! must implement SessionOverviewFragment.OnSessionChangedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//return inflater.inflate(R.layout.session_overview, container, false);
		View defaultView = inflater.inflate(R.layout.session_overview, container, false);

		LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
		p1.weight = 0.5f;
		defaultView.setLayoutParams(p1);
		
		return defaultView;
	}
	
	public void setSession(Session s) {
		session = s;
	}

	public void loadSession() {
		if(session != null) {
			getActivity().setTitle(session.getCourseName());
			
			courseTitle.setText(session.getCourseName());
			leadName.setText(session.getLeadName());
			room.setText(session.getRoom());
			time.setText(session.getStartsNice());
			
			downloadSessionData();
		}
	}
	
	public void downloadSessionData() {
		if(session != null) {
			new DownloadLessons().execute(session.getId());
		}
	}

	public void onHomeworkDownloaded(List<Homework> result) {
		updateHomeworkList(result);
		
		//finally download exams
		session.downloadExams(this);
	}

	public void updateHomeworkCompletionState(Homework newHomework) {
		for(HomeworkListItem thisItem:homework) {
			if(thisItem.getId() == newHomework.getId()) {
				thisItem.setCompletionState(newHomework.isComplete());
				break;
			}
		}
	}

	/**
	 * Called when exam objects are finished downloading
	 */
	public void examsDownloaded(List<Exam> exams) {
		updateExamList(exams);
		
		//now that they're all downloaded we can select a preselected one
		reselectSelectedItem();
	}
	
	/**
	 * Checks to see if there was an item in the list that
	 * was already selected and selects it
	 */
	private void reselectSelectedItem() {
		//Select the first lesson
        if(selectedItem != null) {
        	//reselect the selected item
        	List<Selectable> items = new ArrayList<Selectable>(lessons.size() + homework.size() + exams.size());
        	items.addAll(lessons);
        	items.addAll(homework);
        	items.addAll(exams);
        	
        	int i=0;
        	while(i<items.size()) {
    			System.out.println("NO FIND " + selectedItem.getId());
        		if(items.get(i).equals(selectedItem)) {
        			System.out.println("FOUND SELECTED " + selectedItem.getId());
        			items.get(i).setSelected(true);
        			break;
        		}
        		i++;
        	}
        } else if(lessons.size() > 0) {
			//just selected the first lesson if there is one
        	lessons.get(0).setSelected(true);
		}
	}
    
	private class DownloadLessons extends AsyncTask<Integer, Void, ArrayList<Lesson>> {
		@Override
		protected ArrayList<Lesson> doInBackground(Integer... params) {
			ArrayList<Lesson> ls = WebServiceInterface.getInstance()
					.getLessonsForSession(params[0]);
			return ls;
		}
		
		@Override
		protected void onPostExecute(ArrayList<Lesson> result) {
			super.onPostExecute(result);
			updateLessonListList(result);
		}
	}
	
	private class LessonListItem implements Selectable {
		private Lesson lesson;
		private TextView view;
		
		LessonListItem(Lesson l, TextView v) {
			lesson = l;
			view = v;
			
			view.setText(lesson.getName());
			
			view.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					setSelected(true);
				}
			});
		}
		
		public Lesson getLesson() {
			return lesson;
		}
		
		public boolean isLessonListItem() {
			return true;
		}
		
		public void setSelected(boolean b) {
			if(b) {
				if(selectedItem != null) {
					selectedItem.setSelected(false);
				}
				
				callback.onLessonSelected(lesson);
				view.setBackgroundResource(LESSON_CLICKED_BUTTON_BACKGROUND);
				
				selectedItem = this;
			} else {
				view.setBackgroundResource(LESSON_BUTTON_BACKGROUND);
			}
		}
		
		TextView getView() {
			return view;
		}
		
		public boolean isHomeworkItem() {
			return false;
		}

		public int getId() {
			return lesson.getId();
		}
		
		public boolean equals(Object o) {
			LessonListItem e = null;
			try {
	    		e = (LessonListItem) o;
	    	} catch(ClassCastException ex) {
	    		return false;
	    	}
			return lesson.getId() == e.getId();
		}
	}
	
	private class HomeworkListItem implements Selectable {
		private Homework homework;
		private LinearLayout view;
		private TextView name;
		
		HomeworkListItem(Homework h, LinearLayout v) {
			homework = h;
			view = v;
			name = (TextView) view.findViewById(R.id.homework_list_item_name);
			
			name.setText(homework.toString());
			name.setCompoundDrawablePadding(5);
			
			if(!AcademyProperties.getInstance().getUser().isTeacher()) {
				setCompletionState(homework.isComplete());
			}
				
			view.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					setSelected(true);
				}
			});
		}
		
		/**
		 * Used to change the X or tick icon if the homewokr is
		 * set as completed/incomplete
		 */
		public void setCompletionState(boolean complete) {
			int correctIcon;
			if(complete) {
				correctIcon = R.drawable.homework_complete_marker;
			} else {
				correctIcon = R.drawable.homework_incomplete_marker;
			}
			name.setCompoundDrawablesWithIntrinsicBounds(correctIcon, 0, 0, 0);
		}
		
		public Homework getHomework() {
			return homework;
		}
		
		public boolean isHomeworkItem() {
			return true;
		}
		
		public void setSelected(boolean b) {
			if(b) {
				if(selectedItem != null) {
					selectedItem.setSelected(false);
				}
				
				callback.onHomeworkSelected(homework);
				view.setBackgroundResource(LESSON_CLICKED_BUTTON_BACKGROUND);
				
				selectedItem = this;
			} else {
				view.setBackgroundResource(LESSON_BUTTON_BACKGROUND);
			}
		}
		
		LinearLayout getView() {
			return view;
		}

		public boolean isLessonListItem() {
			return false;
		}

		public int getId() {
			return homework.getId();
		}
		
		public boolean equals(Object o) {
			HomeworkListItem e = null;
			try {
	    		e = (HomeworkListItem) o;
	    	} catch(ClassCastException ex) {
	    		return false;
	    	}
			return homework.getId() == e.getId();
		}
	}
	
	private class ExamListItem implements Selectable {
		private Exam exam;
		private LinearLayout view;
		private TextView name;
		
		ExamListItem(Exam e, LinearLayout v) {
			exam = e;
			view = v;
			name = (TextView) view.findViewById(R.id.exam_list_item_name);
			
			name.setText(exam.toString());
				
			view.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					setSelected(true);
				}
			});
		}
		
		public boolean isHomeworkItem() {
			return false;
		}
		
		public void setSelected(boolean b) {
			if(b) {
				if(selectedItem != null) {
					selectedItem.setSelected(false);
				}
				
				callback.onExamSelected(exam);
				view.setBackgroundResource(LESSON_CLICKED_BUTTON_BACKGROUND);
				
				selectedItem = this;
			} else {
				view.setBackgroundResource(LESSON_BUTTON_BACKGROUND);
			}
		}
		
		LinearLayout getView() {
			return view;
		}

		public boolean isLessonListItem() {
			return false;
		}

		public int getId() {
			return exam.getId();
		}
		
		public boolean equals(Object o) {
			ExamListItem e = null;
			try {
	    		e = (ExamListItem) o;
	    	} catch(ClassCastException ex) {
	    		return false;
	    	}
			return exam.getId() == e.getId();
		}
	}
	
	public interface Selectable {
		void setSelected(boolean b);
		boolean isLessonListItem();
		boolean isHomeworkItem();
		int getId();
		boolean equals(Object o);
	}
}
