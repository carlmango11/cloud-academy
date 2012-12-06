package com.carlnolan.cloudacademy.asynctasks;

import java.util.ArrayList;

import android.os.AsyncTask;

import com.carlnolan.cloudacademy.courses.Section;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

public class DownloadSections extends AsyncTask<Integer, Void, ArrayList<Section>> {
	public interface DownloadSectionsResponder {
		public void onDownloadSectionsComplete(ArrayList<Section> sections);
	}
	
	private DownloadSectionsResponder callback;
	
	public DownloadSections(DownloadSectionsResponder r) {
		callback = r;
	}
	
	@Override
	protected ArrayList<Section> doInBackground(Integer... params) {
		int id = params[0];
		
		ArrayList<Section> ls = WebServiceInterface.getInstance().getSections(id);
		return ls;
	}

	@Override
	protected void onPostExecute(ArrayList<Section> result) {
		super.onPostExecute(result);
		
		callback.onDownloadSectionsComplete(result);
	}
}
