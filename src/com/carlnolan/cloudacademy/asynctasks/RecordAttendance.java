package com.carlnolan.cloudacademy.asynctasks;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.SparseIntArray;

import com.carlnolan.cloudacademy.inclass.Pair;
import com.carlnolan.cloudacademy.usermanagement.Student;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

public class RecordAttendance extends AsyncTask<Object, Void, Void> {
	private OnAttendanceRecordedListener callback;
	private ProgressDialog prog;
	
	public interface OnAttendanceRecordedListener {
		public void onAttendanceRecorded();
	}

	@Override
	protected Void doInBackground(Object... params) {
		int sessionId = (Integer) params[0];
		ArrayList<Student> students = (ArrayList<Student>) params[1];
		callback = (OnAttendanceRecordedListener) params[2]; 

		//Set all students as not attended. This is so that remarking attendace
		//won't have an invisitble "append" effect.
		WebServiceInterface.getInstance().resetSessionAttendance(sessionId);
		for(Student s:students) {
			WebServiceInterface.getInstance().recordStudentAsAttended(sessionId, s.getId());
		}
		
		//Set session as "attendance taken":
		WebServiceInterface.getInstance().setSessionAttendanceTaken(sessionId);
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		callback.onAttendanceRecorded();
	}
}