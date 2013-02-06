package com.carlnolan.cloudacademy.asynctasks;

import java.util.ArrayList;

import android.os.AsyncTask;

import com.carlnolan.cloudacademy.configuration.AcademyProperties;
import com.carlnolan.cloudacademy.courses.Course;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

public class DownloadCourses extends AsyncTask<Void, Void, ArrayList<Course>> {
	private OnCoursesDownloadedListener callback;
	
	public interface OnCoursesDownloadedListener {
		public void onCoursesDownloaded(ArrayList<Course> result);
	}
	
	public DownloadCourses(OnCoursesDownloadedListener c) {
		callback = c;
	}

	@Override
	protected ArrayList<Course> doInBackground(Void... params) {
		ArrayList<Course> ls = WebServiceInterface.getInstance().getCourses(
				AcademyProperties.getInstance().getUser().isTeacher());
		return ls;
	}

	@Override
	protected void onPostExecute(ArrayList<Course> result) {
		super.onPostExecute(result);
		callback.onCoursesDownloaded(result);
	}
}
