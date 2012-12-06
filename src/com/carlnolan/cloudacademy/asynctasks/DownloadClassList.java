package com.carlnolan.cloudacademy.asynctasks;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.carlnolan.cloudacademy.courses.Lesson;
import com.carlnolan.cloudacademy.usermanagement.Student;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

public class DownloadClassList extends AsyncTask<Object, Void, List<Student>> {
	public interface DownloadClassListResponder {
		public void onDownloadClassListComplete(List<Student> students);
	}
	
	private DownloadClassListResponder callback;
	
	@Override
	protected List<Student> doInBackground(Object... params) {
		int id = (Integer) params[0];
		callback = (DownloadClassListResponder) params[1];

		List<Student> ls = WebServiceInterface.getInstance().getClassList(id);
		return ls;
	}

	@Override
	protected void onPostExecute(List<Student> result) {
		super.onPostExecute(result);
		
		callback.onDownloadClassListComplete(result);
	}
}
