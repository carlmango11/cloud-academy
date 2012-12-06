package com.carlnolan.cloudacademy.inclass;

import java.util.ArrayList;

import com.carlnolan.cloudacademy.LoginActivity;
import com.carlnolan.cloudacademy.MainActivity;
import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.courses.Content;
import com.carlnolan.cloudacademy.courses.Lesson;
import com.carlnolan.cloudacademy.courses.LessonListFragment;
import com.carlnolan.cloudacademy.courses.LessonListFragment.OnLessonSelectedListener;
import com.carlnolan.cloudacademy.planner.DayViewerFragment.OnScheduleDayChangedListener;
import com.carlnolan.cloudacademy.scheduling.Session;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class SessionOverviewFragment extends Fragment {
	private OnLessonSelectedListener callback;
	private Session session;
	
	private TextView courseTitle;
	private TextView leadName;
	private TextView room;
	private TextView time;
	private ImageButton attachLesson;

	private LinearLayout lessonsToCoverList;
	private LinearLayout homeworkDueList;
	private LinearLayout examList;
	
	private ArrayList<LessonListItem> lessons;
	private Selectable selectedItem;
	
	private static final int LESSON_BUTTON_BACKGROUND = R.drawable.white_menu_item_gradient;
	private static final int LESSON_CLICKED_BUTTON_BACKGROUND = R.drawable.clicked_white_menu_item_gradient;
	
	public interface OnLessonSelectedListener {
		public void onLessonSelected(Lesson lesson);
		public void setLessonViewerVisibile(boolean visible);
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
    		
    		//Select the first lesson
    		if(i == 0) {
    			thisItem.setSelected(true);
    		}
    	}
    	
    	if(lessons.size() == 0) {
    		callback.setLessonViewerVisibile(false);
    	}
	}
	
	public Lesson getSelectedLesson() {
		if(selectedItem != null && selectedItem.isLessonListItem()) {
			return ((LessonListItem) selectedItem).getLesson();
		}
		return null;
	}
	
	/*void viewLesson(Lesson lesson) {
		Intent intent = new Intent(getActivity(), LessonViewerFragment.class);
		intent.putExtra("thisLesson", lesson);
		intent.putExtra("returnToSessionViewer", true);
        startActivity(intent);
	}*/

    /*private void addLessonClickListener(final View thisView, final Lesson thisLesson) {
		thisView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(selectedView != null) {
					Log.d("carl", "selectedView found");
					selectedView.setBackgroundResource(LESSON_BUTTON_BACKGROUND);
				}
				
				callback.onLessonSelected(thisLesson);
				thisView.setBackgroundResource(LESSON_CLICKED_BUTTON_BACKGROUND);
				selectedView = (TextView) thisView;
				Log.d("carl", "found:" + selectedView);
			}
		});
	}*/

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			callback = (OnLessonSelectedListener) activity;
		} catch(ClassCastException e) {
			Log.d("carl", "Could not cast class");
			throw new ClassCastException(activity.toString()
					+ " upcoming! must implement SessionOverviewFragment.OnSessionChangedListener");
		}
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
        
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        loadSession();

		Log.d("carl", "Started Session Overview");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.session_overview, container, false);
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
			
			new DownloadLessons().execute(session.getId());
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
	}
	
	public interface Selectable {
		void setSelected(boolean b);
		boolean isLessonListItem();
	}
}
