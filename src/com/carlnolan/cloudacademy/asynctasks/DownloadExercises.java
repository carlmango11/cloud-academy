package com.carlnolan.cloudacademy.asynctasks;

import java.util.List;

import android.os.AsyncTask;

import com.carlnolan.cloudacademy.courses.Exercise;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

public class DownloadExercises extends AsyncTask<Object, Void, List<Exercise>> {
	private DownloadExercisesListener callback;
	
	public interface DownloadExercisesListener {
		public void onExercisesDownloaded(List<Exercise> result);
	}
	
	@Override
	protected List<Exercise> doInBackground(Object... params) {
		int lessonId = (Integer) params[0];
		callback = (DownloadExercisesListener) params[1];
		
		List<Exercise> ls = WebServiceInterface.getInstance().getExercises(lessonId);
		return ls;
	}

	@Override
	protected void onPostExecute(List<Exercise> result) {
		super.onPostExecute(result);
		callback.onExercisesDownloaded(result);
	}
}
