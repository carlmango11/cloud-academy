package com.carlnolan.cloudacademy.courses;

import com.carlnolan.cloudacademy.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class LessonContentFragment extends Fragment {
	private Lesson thisLesson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onStart() {
		super.onStart();
		
		Log.d("carl", "Started Content Viewer");
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
		View defaultView = inflater.inflate(R.layout.lesson_content, container, false);
    	
    	LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT);
		p1.weight = 0.5f;
		defaultView.setLayoutParams(p1);
		
		return defaultView;
    }
}
