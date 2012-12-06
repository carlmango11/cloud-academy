package com.carlnolan.cloudacademy.asynctasks;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.SparseIntArray;

import com.carlnolan.cloudacademy.inclass.Pair;
import com.carlnolan.cloudacademy.usermanagement.Student;
import com.carlnolan.cloudacademy.webservice.WebServiceAuthentication;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

public class TestAuthentication extends AsyncTask<WebServiceAuthentication, Void, Boolean> {
	private TestAuthenticationResultListener callback;
	
	public interface TestAuthenticationResultListener {
		public void onTestAuthenticationResultReceived(boolean result);
	}
	
	public TestAuthentication(TestAuthenticationResultListener c0) {
		callback = c0;
	}

	@Override
	protected Boolean doInBackground(WebServiceAuthentication... params) {		
		boolean result =
				WebServiceInterface.getInstance().testAuthentication(params[0]);
		
		return result;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		callback.onTestAuthenticationResultReceived(result);
	}
}