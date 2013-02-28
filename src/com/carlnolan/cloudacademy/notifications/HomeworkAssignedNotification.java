package com.carlnolan.cloudacademy.notifications;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.carlnolan.cloudacademy.MainActivity;
import com.carlnolan.cloudacademy.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

/**
 * Used to create a notification for when homework is assigned to a user
 * @author Carl
 *
 */
public class HomeworkAssignedNotification {
	/**
	 * GCM variable names
	 */
	private static final String COURSE_NAME = "COURSE";
	private static final String HOMEWORK_ID = "ID";
	private static final String HOMEWORK_DUE = "DUE";
	private static final String EXERCISE_NAME = "EXERCISE";
	
	public static final String PRESET_HOMEWORK_DUE_DATE = "PRESET_HOMEWORK_DUE_DATE";
	public static final String PRESET_HOMEWORK_ID = "PRESET_HOMEWORK_ID";
	
	private static final int iconResource = R.drawable.homework_complete_marker;
	private static final int titleResource = R.string.homework_assigned_notification_title;

	private Context context;
	private String title;
	private String dueString;
	private int homeworkId;
	private String courseName;
	private String exerciseName;
	
	public HomeworkAssignedNotification(Context context0,
			Bundle args) {
		
		context = context0;
		title = context.getString(titleResource);
		
		courseName = args.getString(COURSE_NAME);
		exerciseName = args.getString(EXERCISE_NAME);
		homeworkId = args.getInt(HOMEWORK_ID);
		dueString = args.getString(HOMEWORK_DUE);
	}
	
	public void go() {
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(iconResource)
		        .setContentTitle(title)
		        .setContentText(courseName + ": " + exerciseName);
		
		//Create intent for when notification is touched
		Intent resultIntent = new Intent(context, MainActivity.class);
		
		//Add info to the intent so that MainActivity knows to bring the user to the homework
		resultIntent.putExtra(MainActivity.PRESET_TAB_STRING, context.getString(R.string.title_workload_tab));
		resultIntent.putExtra(PRESET_HOMEWORK_DUE_DATE, dueString);
		resultIntent.putExtra(PRESET_HOMEWORK_ID, homeworkId);

		// From Google:
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager notifManager =
		    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		notifManager.notify(0, mBuilder.build());
	}
}
