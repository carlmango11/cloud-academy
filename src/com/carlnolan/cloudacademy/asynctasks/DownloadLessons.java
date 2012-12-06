package com.carlnolan.cloudacademy.asynctasks;

import java.util.ArrayList;

import android.os.AsyncTask;

import com.carlnolan.cloudacademy.courses.Lesson;
import com.carlnolan.cloudacademy.courses.Section;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

public class DownloadLessons extends AsyncTask<Object, Void, ArrayList<Lesson>> {
	public interface DownloadLessonsResponder {
		public void onDownloadLessonsComplete(ArrayList<Lesson> lessons);
	}
	
	private DownloadLessonsResponder callback;
	
	@Override
	protected ArrayList<Lesson> doInBackground(Object... params) {
		int id = (Integer) params[0];
		callback = (DownloadLessonsResponder) params[1];
		
		ArrayList<Lesson> ls = WebServiceInterface.getInstance().getLessons(id);
		return ls;
	}

	@Override
	protected void onPostExecute(ArrayList<Lesson> result) {
		super.onPostExecute(result);
		
		callback.onDownloadLessonsComplete(result);
	}
}
