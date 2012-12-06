package com.carlnolan.cloudacademy.asynctasks;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.SparseIntArray;

import com.carlnolan.cloudacademy.inclass.Pair;
import com.carlnolan.cloudacademy.usermanagement.Student;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

public class CheckAttendanceIsTaken extends AsyncTask<Object, Void, Boolean> {
	private OnAttendanceTakenListener callback;
	
	public interface OnAttendanceTakenListener {
		public void onAttendanceTakenResultReturned(boolean taken);
	}

	@Override
	protected Boolean doInBackground(Object... params) {
		int sessionId = (Integer) params[0];
		callback = (OnAttendanceTakenListener) params[1];
		
		//Set session as "attendance taken":
		boolean result =
				WebServiceInterface.getInstance().checkAttendanceTaken(sessionId);
		
		return result;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		callback.onAttendanceTakenResultReturned(result);
	}
}