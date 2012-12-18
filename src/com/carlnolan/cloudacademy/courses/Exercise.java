package com.carlnolan.cloudacademy.courses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.AsyncTask;

import com.carlnolan.cloudacademy.webservice.WebServiceInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Exercise extends Content {
	
	public interface OnExerciseCreatedListener {
		public void onExerciseCreated(Exercise newExercise);
	}
	
	@Override
	public boolean isExercise() {
		return true;
	}

	public static List<Exercise> buildExercisesFromJSON(String json) {
		Gson gson = new GsonBuilder()
			.create();
		Exercise [] exerciseArray = gson.fromJson(json, Exercise[].class);
		
		return new ArrayList<Exercise>(Arrays.asList(exerciseArray));
	}

	public static void addNewExercise(OnExerciseCreatedListener c, String name,
			String description) {
		new AddNewExercise(c).execute(name, description);
	}
	
	private static class AddNewExercise extends AsyncTask<String, Void, Exercise> {
		private OnExerciseCreatedListener callback;
		
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
}
