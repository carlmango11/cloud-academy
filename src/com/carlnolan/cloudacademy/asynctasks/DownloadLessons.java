package com.carlnolan.cloudacademy.asynctasks;

import java.util.ArrayList;

import android.os.AsyncTask;

import com.carlnolan.cloudacademy.courses.Lesson;
import com.carlnolan.cloudacademy.courses.Section;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

public class DownloadLessons extends AsyncTask<Void, Void, ArrayList<Lesson>> {
	private DownloadLessonsResponder callback;
	private int sectionId;
	
	public interface DownloadLessonsResponder {
		public void onDownloadLessonsComplete(ArrayList<Lesson> lessons);
	}
	
	public DownloadLessons(DownloadLessonsResponder c0, int id0) {
		sectionId = id0;
		callback = c0;
	}
	
	@Override
	protected ArrayList<Lesson> doInBackground(Void... params) {
		ArrayList<Lesson> ls = WebServiceInterface.getInstance().getLessons(sectionId);
		return ls;
	}

	@Override
	protected void onPostExecute(ArrayList<Lesson> result) {
		super.onPostExecute(result);
		
		callback.onDownloadLessonsComplete(result);
	}
}
