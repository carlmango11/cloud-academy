package com.carlnolan.cloudacademy.courses;

import java.util.ArrayList;
import java.util.Calendar;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.scheduling.Session;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;
import com.carlnolan.cloudacademy.R;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class UpcomingClassesFragment extends ListFragment {

	private OnClassSelectedListener callback;
	private ArrayList<Session> upcomingClasses;

    public interface OnClassSelectedListener {
        public void onClassSelected(Session session);
    }
    
    void updateList(ArrayList<Session> newSessions) {
    	Session [] sessions = new Session[newSessions.size()];
    	sessions = newSessions.toArray(sessions);
        
       /* setListAdapter(
        		new UpcomingClassesAdapter(
        				getActivity(),
        				R.layout.upcoming_classes_list_item,
        				sessions
        		)
        );*/
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        String [] lsa = new String[] {"Downloading..."};
        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, lsa));

        Calendar cal = Calendar.getInstance();
        new DownloadUpcomingClasses().execute(cal);
    }

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		callback.onClassSelected(upcomingClasses.get(position));
		getListView().setItemChecked(position, true);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		try {
			callback = (OnClassSelectedListener) activity;
		} catch(ClassCastException e) {
			Log.d("carl", "Could not cast class");
			throw new ClassCastException(activity.toString()
					+ " upcoming! must implement UpcomingClassesFragment.OnClassSelectedListener");
		}
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 *//*
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View defaultView = super.onCreateView(inflater, container, savedInstanceState);

		defaultView.setLayoutParams(
				new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 2
				)
		);
		
		return defaultView;
	}*/
    
	private class DownloadUpcomingClasses extends AsyncTask<Calendar, Void, ArrayList<Session>> {
		@Override
		protected ArrayList<Session> doInBackground(Calendar... days) {
			Log.d("carl", "running!!!!");
			//ArrayList<Session> ls = WebServiceInterface.getSessionsForDate(
			//		Calendar.getInstance());
			return new ArrayList<Session>();
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(ArrayList<Session> result) {
			super.onPostExecute(result);
			upcomingClasses = result;
			updateList(result);
		}
	}
}
