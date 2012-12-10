package com.carlnolan.cloudacademy.asynctasks;

import java.util.List;

import android.os.AsyncTask;

import com.carlnolan.cloudacademy.courses.Exercise;
import com.carlnolan.cloudacademy.inclass.Homework;
import com.carlnolan.cloudacademy.scheduling.Session;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

public class DownloadHomeworkDue extends AsyncTask<Session, Void, List<Homework>> {
	private DownloadHomeworkDueListener callback;
	
	public interface DownloadHomeworkDueListener {
		public void onHomeworkDownloaded(List<Homework> result);
	}
	
	public DownloadHomeworkDue(DownloadHomeworkDueListener c) {
		callback = c;
	}
	
	@Override
	protected List<Homework> doInBackground(Session... params) {
		Session sessionDue = (Session) params[0];
		
		List<Homework> ls = WebServiceInterface.getInstance()
				.getHomeworkDue(
						sessionDue.getCourseId(),
						sessionDue.getStartDateSQL(),
						sessionDue.getClassId());
		return ls;
	}

	@Override
	protected void onPostExecute(List<Homework> result) {
		super.onPostExecute(result);
		callback.onHomeworkDownloaded(result);
	}
}
