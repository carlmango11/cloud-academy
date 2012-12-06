package com.carlnolan.cloudacademy.asynctasks;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.util.SparseIntArray;

import com.carlnolan.cloudacademy.courses.Exercise;
import com.carlnolan.cloudacademy.inclass.Pair;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

public class AddNewExercise extends AsyncTask<String, Void, Exercise> {
	private OnExerciseCreatedListener callback;
	
	public interface OnExerciseCreatedListener {
		public void onExerciseCreated(Exercise newExercise);
	}
	
	public AddNewExercise(OnExerciseCreatedListener callback0) {
		callback = callback0;
	}
	
	@Override
	protected Exercise doInBackground(String... params) {
		return WebServiceInterface.getInstance().addNewExercise(params[0], params[1]);
	}

	@Override
	protected void onPostExecute(Exercise newExercise) {
		super.onPostExecute(newExercise);
		callback.onExerciseCreated(newExercise);
	}
}