package com.carlnolan.cloudacademy.asynctasks;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.carlnolan.cloudacademy.courses.Lesson;
import com.carlnolan.cloudacademy.usermanagement.Student;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

public class GetAttendanceList extends AsyncTask<Object, Void, List<Integer>> {
	public interface GetAttendanceListListener {
		public void onAttendanceListReceived(List<Integer> studentIds);
	}
	
	private GetAttendanceListListener callback;
	
	@Override
	protected List<Integer> doInBackground(Object... params) {
		int id = (Integer) params[0];
		callback = (GetAttendanceListListener) params[1];

		List<Integer> ls = WebServiceInterface.getInstance().getAttendanceList(id);
		return ls;
	}

	@Override
	protected void onPostExecute(List<Integer> result) {
		super.onPostExecute(result);		
		callback.onAttendanceListReceived(result);
	}
}
