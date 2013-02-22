package com.carlnolan.cloudacademy.asynctasks;

import java.util.Calendar;
import java.util.List;

import android.os.AsyncTask;

import com.carlnolan.cloudacademy.courses.Exercise;
import com.carlnolan.cloudacademy.inclass.Homework;
import com.carlnolan.cloudacademy.scheduling.Session;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

public class DownloadHomeworkDueForRange extends AsyncTask<Void, Void, List<Homework>> {
	private DownloadHomeworkDueForRangeListener callback;
	private Calendar start;
	private Calendar end;
	
	public interface DownloadHomeworkDueForRangeListener {
		public void onHomeworkRangeDownloaded(List<Homework> result);
	}
	
	public DownloadHomeworkDueForRange(DownloadHomeworkDueForRangeListener c,
			Calendar s, Calendar e) {
		callback = c;
		start = s;
		end = e;
	}
	
	@Override
	protected List<Homework> doInBackground(Void... params) {
		List<Homework> ls = WebServiceInterface.getInstance()
				.getHomeworkDueForRange(start, end);
		return ls;
	}

	@Override
	protected void onPostExecute(List<Homework> result) {
		super.onPostExecute(result);
		callback.onHomeworkRangeDownloaded(result);
	}
}
