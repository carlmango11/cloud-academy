package com.carlnolan.cloudacademy.inclass;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.asynctasks.DownloadLessons;
import com.carlnolan.cloudacademy.asynctasks.DownloadSections;
import com.carlnolan.cloudacademy.courses.Lesson;
import com.carlnolan.cloudacademy.courses.Section;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SelectLessonDialog extends DialogFragment
	implements DownloadSections.DownloadSectionsResponder,
	DownloadLessons.DownloadLessonsResponder {
	
	private int courseId;
	private ListView sectionList;
	private ListView lessonList;
	private List<Section> sections;
	private List<Lesson> lessons;
	
	private OnLessonSelectedFromDialogListener callback;
	
	public interface OnLessonSelectedFromDialogListener {
		public void onLessonSelectedFromDialog(Lesson selectedLesson);
	}

	public static SelectLessonDialog newInstance(int courseId) {
		SelectLessonDialog instance = new SelectLessonDialog();
		
		Bundle args = new Bundle();
        args.putInt("courseId", courseId);
        instance.setArguments(args);
        return instance;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		courseId = getArguments().getInt("courseId");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	LayoutInflater inflater = getActivity().getLayoutInflater();
    	View dialogView = inflater.inflate(R.layout.dialog_select_lesson, null);
    	
    	sectionList = (ListView) dialogView.findViewById(R.id.dialog_select_section_list);
    	lessonList = (ListView) dialogView.findViewById(R.id.dialog_select_lesson_list);
    	sectionList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    	
    	sections = new ArrayList<Section>();
    	lessons = new ArrayList<Lesson>();
    	
    	//Populate listViews
    	new DownloadSections(this).execute(courseId);
    	
    	//Retrieve views and attach handlers
    	builder.setView(dialogView)
    		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				dismiss();
    			}
    		}).setTitle("Select Lesson");
    	
    	return builder.create();
	}

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            callback = (OnLessonSelectedFromDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement OnLessonSelectedFromDialogListener");
        }
    }

	public void onDownloadSectionsComplete(ArrayList<Section> sections) {
		this.sections = sections;
		
		String[] sectionStrings = new String[sections.size()];
		for(int i=0;i<sections.size();i++) {
			sectionStrings[i] = sections.get(i).toString();
		}
		
		sectionList.setAdapter(new ArrayAdapter<String>(
				getActivity(),
				android.R.layout.simple_list_item_activated_1,
				sectionStrings));

		sectionList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				loadSection(arg2);
			}
		});
	}
	
	private void loadSection(int index) {
		new DownloadLessons().execute(sections.get(index).getId(), this);
	}
	
	private void selectSession(int index) {
		callback.onLessonSelectedFromDialog(lessons.get(index));
		dismiss();
	}

	public void onDownloadLessonsComplete(ArrayList<Lesson> lessons) {
		this.lessons = lessons;
		
		String[] lessonStrings = new String[lessons.size()];
		for(int i=0;i<lessons.size();i++) {
			lessonStrings[i] = lessons.get(i).toString();
		}
		
		lessonList.setAdapter(new ArrayAdapter<String>(
				getActivity(),
				android.R.layout.simple_list_item_activated_1,
				lessonStrings));
    	
    	lessonList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				selectSession(arg2);
			}
    	});
	}
}
