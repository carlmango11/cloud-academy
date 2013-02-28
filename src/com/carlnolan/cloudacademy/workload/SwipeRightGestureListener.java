package com.carlnolan.cloudacademy.workload;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class SwipeRightGestureListener extends SimpleOnGestureListener {
	private RightSwipeListener callback;
	
	private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	
	public interface RightSwipeListener {
		public void onSwipeRight();
	}
	
	public SwipeRightGestureListener(RightSwipeListener c) {
		callback = c;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            
            if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                callback.onSwipeRight();
            }
        } catch (Exception e) {
            // nothing
        }
        return false;
	}
}
