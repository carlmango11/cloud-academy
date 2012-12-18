package com.carlnolan.cloudacademy.asynctasks;

import java.util.List;

import android.os.AsyncTask;
import com.carlnolan.cloudacademy.courses.Exercise;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

public class AssignHomework extends AsyncTask<Void, Void, Boolean> {
	private OnHomeworkAssignedListener callback;
	private List<Exercise> exercises;
	private int classId;
	private int courseId;
	private String dueDate;
	
	public interface OnHomeworkAssignedListener {
		public void onHomeworkAssigned(boolean success);
	}
	
	public AssignHomework(OnHomeworkAssignedListener callback0,
			String dueDate0,
			List<Exercise> exercises0,
			int classId0,
			int courseId0) {
		callback = callback0;
		exercises = exercises0;
		classId = classId0;
		dueDate = dueDate0;
		courseId = courseId0;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		for(Exercise e:exercises) {
			boolean success = WebServiceInterface.getInstance().assignHomework(
					e, classId, courseId, dueDate);
			if(!success) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);
		callback.onHomeworkAssigned(success);
	}
}