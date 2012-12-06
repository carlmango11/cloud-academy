package com.carlnolan.cloudacademy.asynctasks;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.SparseIntArray;

import com.carlnolan.cloudacademy.inclass.Pair;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

public class AttachLessons extends AsyncTask<Object, Void, Void> {
	private OnLessonsAttachedListener callback;
	
	public interface OnLessonsAttachedListener {
		public void onLessonsAttachComplete();
	}
	
	@Override
	protected Void doInBackground(Object... params) {
		ArrayList<Pair<Integer,Integer>> links = (ArrayList<Pair<Integer,Integer>>) params[0];
		callback = (OnLessonsAttachedListener) params[1]; 
		
		for(int i=0;i<links.size();i++) {
			int session = links.get(i).getLeft();
			int lesson = links.get(i).getRight();
			WebServiceInterface.getInstance().attachLessonToSession(session, lesson);
		}
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		callback.onLessonsAttachComplete();
	}
}