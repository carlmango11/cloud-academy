package com.carlnolan.cloudacademy.inclass;

import java.util.ArrayList;
import java.util.List;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.asynctasks.DownloadExercises;
import com.carlnolan.cloudacademy.courses.Exercise;
import com.carlnolan.cloudacademy.courses.Lesson;
import com.carlnolan.cloudacademy.inclass.AttendanceDialog.AttendanceTakenListener;
import com.carlnolan.cloudacademy.inclass.SessionOverviewFragment.Selectable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

public class SelectExercisesDialog extends DialogFragment
	implements DownloadExercises.DownloadExercisesListener {
	
	private FromWhereSelectedListener callback;
	private int lessonId;
	private List<Exercise> exercises;
	private boolean [] selected;
	
	private ListView exerciseList;
	private ProgressBar prog;
	
	public interface FromWhereSelectedListener {
		public void onExercisesSelected(List<Exercise> exercises);
	}

	public static SelectExercisesDialog newInstance(int lessonId) {
		SelectExercisesDialog instance = new SelectExercisesDialog();
		
		Bundle args = new Bundle();
        args.putInt("lessonId", lessonId);
        instance.setArguments(args);
        return instance;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		lessonId = getArguments().getInt("lessonId");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	LayoutInflater inflater = getActivity().getLayoutInflater();
    	View dialogView = inflater.inflate(R.layout.dialog_select_exercises, null);
    	
    	exerciseList = (ListView) dialogView.findViewById(R.id.dialog_select_exercises_list);
		prog = (ProgressBar) dialogView.findViewById(R.id.dialog_select_exercises_progress);
		exerciseList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	
		//Start the progress bar until we're done d/ling everything
		showProgressBar(true);
		new DownloadExercises().execute(lessonId, this);
		//Prevent nulls, should never happen but w/e yano
		selected = new boolean[1];
		
		builder.setView(dialogView)
			.setPositiveButton("Assign As Homework", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					finishExerciseSelection();
				}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dismiss();
				}
			}).setTitle(R.string.homework_select_exercises);
		
		return builder.create();
	}

	private void showProgressBar(boolean b) {
		int mainElementsVisiblity = b ? View.GONE : View.VISIBLE;
		int progressVisiblity = b ? View.VISIBLE : View.GONE;
		
		exerciseList.setVisibility(mainElementsVisiblity);
		prog.setVisibility(progressVisiblity);
	}
	
	private void finishExerciseSelection() {
		List<Exercise> returnList = new ArrayList<Exercise>();
		
		for(int i=0;i<exercises.size();i++) {
			if(selected[i]) {
				returnList.add(exercises.get(i));
			}
		}
		
		callback.onExercisesSelected(returnList);
		dismiss();
	}

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            callback = (FromWhereSelectedListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement FromWhereSelectedListener");
        }
    }

	public void onExercisesDownloaded(List<Exercise> result) {
		exercises = result;
		selected = new boolean[exercises.size()];
		showProgressBar(false);
		
		String[] exerciseStrings = new String[exercises.size()];
		for(int i=0;i<exercises.size();i++) {
			exerciseStrings[i] = exercises.get(i).toString();
		}
		
		exerciseList.setAdapter(new ArrayAdapter<String>(
				getActivity(),
				android.R.layout.simple_list_item_activated_1,
				exerciseStrings));

		exerciseList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				selected[arg2] = !selected[arg2];
			}
		});
	}
}
