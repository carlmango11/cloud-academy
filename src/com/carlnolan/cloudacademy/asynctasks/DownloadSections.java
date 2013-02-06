package com.carlnolan.cloudacademy.asynctasks;

import java.util.ArrayList;

import android.os.AsyncTask;

import com.carlnolan.cloudacademy.courses.Course;
import com.carlnolan.cloudacademy.courses.Section;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

public class DownloadSections extends AsyncTask<Void, Void, ArrayList<Section>> {
	private DownloadSectionsResponder callback;
	private int courseId;
	
	public interface DownloadSectionsResponder {
		public void onDownloadSectionsComplete(ArrayList<Section> sections, int courseId);
	}
	
	public DownloadSections(DownloadSectionsResponder r, int id0) {
		callback = r;
		courseId = id0;
	}
	
	@Override
	protected ArrayList<Section> doInBackground(Void... params) {		
		ArrayList<Section> ls = WebServiceInterface.getInstance().getSections(courseId);
		return ls;
	}

	@Override
	protected void onPostExecute(ArrayList<Section> result) {
		super.onPostExecute(result);
		
		callback.onDownloadSectionsComplete(result, courseId);
	}
}
