package com.carlnolan.cloudacademy;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.carlnolan.cloudacademy.inclass.Homework;
import com.carlnolan.cloudacademy.notifications.HomeworkAssignedNotification;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;
import com.google.android.gcm.GCMBaseIntentService;

/**
 * This class deals with all of the events from GCM
 * @author Carl
 *
 */
public class GCMIntentService extends GCMBaseIntentService {
	private static final String HOMEWORK_ASSIGNED_TYPE = "HOMEWORK_ASSIGNED";
	
	public GCMIntentService() {
		super(MainActivity.SENDER_ID);
	}
	
	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMessage(Context context, Intent message) {
		Bundle args = message.getExtras();
		String type = args.getString("TYPE");
		
		if(type.equals(HOMEWORK_ASSIGNED_TYPE)) {
			new HomeworkAssignedNotification(context, args).go();
		}
	}

	@Override
	protected void onRegistered(Context arg0, String regId) {
		System.out.println("registration received");
		WebServiceInterface.getInstance().registerGCMId(regId);
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
}
