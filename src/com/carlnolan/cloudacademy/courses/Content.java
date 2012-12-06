package com.carlnolan.cloudacademy.courses;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import android.util.Log;
import android.webkit.MimeTypeMap;

import com.carlnolan.cloudacademy.configuration.AcademyProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class Content {
	private int id;
	private String name;
	private String filename;
	private String description;
	
	public abstract boolean isExercise();
	
	/*private static final Map<String, String> ANDROID_TYPE_MAPPINGS =
			new HashMap<String, String>();
	static {
		ANDROID_TYPE_MAPPINGS.put("jpg", "")
	};*/
	
	public String toString() {
		return name;
	}

	public int getId() {
		return id;
	}
	
	public String getFilename() {
		return filename;
	}

	/**
	 * Get the URL used to retrieve this file from the server
	 * @return
	 */
	public String getURL(int lessonId) {
		String url = "";
		try {
			String encodedFilename = URLEncoder.encode(filename, "utf-8");
			
			url = AcademyProperties.getInstance().getFileProviderUrl();
			url += "lesson=" + lessonId + "&file=" + encodedFilename;
		} catch(UnsupportedEncodingException e) {
			Log.e("cloudacademy", "Cannot encode filename: " + filename);
		}
		
		return url;
	}
	
	public static String getAndroidType(String filename) {
		String mimeType = "";
		Log.d("carl", "filenameContent:"+filename);
		
		if(filename.lastIndexOf('.') != -1) {
			String ext = filename.substring(
					filename.lastIndexOf('.'),
					filename.length());
	    	mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.substring(1));

			Log.d("carl", "ext:"+ext);
			Log.d("carl", "mimeType:"+mimeType);
	    	if(mimeType == null) {
	    		mimeType = "";
	    	}
		}

		return mimeType;
	}

	public String getDescription() {
		return description;
	}
}
