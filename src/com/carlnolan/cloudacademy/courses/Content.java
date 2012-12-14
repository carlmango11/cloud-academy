package com.carlnolan.cloudacademy.courses;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.configuration.AcademyProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class Content {
	private int id;
	private String name;
	private String filename;
	private String description;
	
	public abstract boolean isExercise();
	
	//Unsupported Toast duration
	private static final int UNSUPPORTED_FILETYPE_TOAST_DURATION = 4;
	
	/*private static final Map<String, String> ANDROID_TYPE_MAPPINGS =
			new HashMap<String, String>();
	static {
		ANDROID_TYPE_MAPPINGS.put("jpg", "")
	};*/
	
	/**
	 * Interface used by the async downloadContent task
	 * @author Carl
	 *
	 */
	public interface ContentDownloadCallback {
		public void contentDownloaded(String location);
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
	
	public void download(int lesson, ProgressDialog progress, ContentDownloadCallback call) {
		new DownloadFile(lesson, progress, call).execute();
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

	public static void openContent(Activity currentActivity, String path) {
    	File fileToOpen = new File(path);
    	String type = Content.getAndroidType(path);
    
    	//Check if we got a proper type
    	if(type.length() > 0) {
    		//Open file
        	Intent i = new Intent();
        	i.setAction(android.content.Intent.ACTION_VIEW);
        	i.setDataAndType(Uri.fromFile(fileToOpen), type);
        	currentActivity.startActivity(i);
    	} else {
    		//Show unsupported dialog
    		Toast toast = Toast.makeText(currentActivity,
    				R.string.unsupported_filetype_error,
    				UNSUPPORTED_FILETYPE_TOAST_DURATION);
    		toast.show();
    	}
    }

	/**
	 * Getters and setters:
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	
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
	 * A handy async task used to download this content
	 * Used internally by download()
	 * @author Carl
	 *
	 */
	private class DownloadFile extends AsyncTask<Void, Integer, String> {
		private int lessonId;
		private ProgressDialog progressDialog;
		private ContentDownloadCallback callback;
		
		public DownloadFile(int lessonId0, ProgressDialog progress, ContentDownloadCallback call) {
			lessonId = lessonId0;
			progressDialog = progress;
			callback = call;
		}
		
	    @Override
	    protected String doInBackground(Void... sUrl) {
	    	String result = "";
	    	
	        try {
	            URL url = new URL(getURL(lessonId));
	            URLConnection connection = url.openConnection();
	            connection.connect();
	            // this will be useful so that you can show a typical 0-100% progress bar
	            int fileLength = connection.getContentLength();

	            // download the file
	            InputStream input = new BufferedInputStream(url.openStream());
	            
	            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	            String newFileLocation = dir + "/" + getFilename();
	            OutputStream output = new FileOutputStream(newFileLocation);

	            byte data[] = new byte[1024];
	            long total = 0;
	            int count;
	            while ((count = input.read(data)) != -1) {
	                total += count;
	                // publishing the progress....
	                publishProgress((int) (total * 100 / fileLength));
	                output.write(data, 0, count);
	            }

	            output.flush();
	            output.close();
	            input.close();
	            result = newFileLocation;
	        } catch (Exception e) {
	        }
	        return result;
	    }
		
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        progressDialog.show();
	    }

	    @Override
		protected void onPostExecute(String path) {
			super.onPostExecute(path);
			progressDialog.dismiss();

			callback.contentDownloaded(path);
		}

		@Override
	    protected void onProgressUpdate(Integer... progress) {
	        super.onProgressUpdate(progress);
	        progressDialog.setProgress(progress[0]);
	    }
	}
    
    public static class ContentClickListener implements OnClickListener {
    	private Content contentObject;
    	private ProgressDialog progress;
    	private Content.ContentDownloadCallback callback;
    	private int lessonId;
    	
    	public ContentClickListener(Content con0, int less0,
    			ProgressDialog prog0, Content.ContentDownloadCallback call0) {
    		contentObject = con0;
    		lessonId = less0;
    		progress = prog0;
    		callback = call0;
    	}
    	
		public void onClick(View v) {
			contentObject.download(lessonId, progress, callback);
		}
    }
}
