package com.carlnolan.cloudacademy.usermanagement;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.carlnolan.cloudacademy.configuration.AcademyProperties;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class User {
	private int id;
	private String firstname;
	private String surname;
	private Bitmap photo;
	
	public abstract boolean isTeacher();

	/**
	 * Get the URL used to retrieve user photo from server
	 * @return
	 */
	public String getURL() {
		String url = "";
			
		url = AcademyProperties.getInstance().getUserPhotoProviderUrl();
		url += "user=" + id;
		
		return url;
	}
	
	public void downloadPhoto() {
		try {
            URL url = new URL(getURL());
            Log.d("cloudacademy", "Download user photo from: " + url);
            URLConnection connection = url.openConnection();
            connection.connect();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream());
            photo = BitmapFactory.decodeStream(input);
            Log.d("photosize", ""+photo.getByteCount());
            Log.d("photosize", ""+photo.getHeight());

            /*byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }

            output.flush();
            output.close();*/
            input.close();
        } catch (Exception e) {
        	Log.d("carl", ""+e.getMessage());
        }
	}

	public Bitmap getPhoto() {
		return photo;
	}

	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @param firstname the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * @return the surname
	 */
	public String getSurname() {
		return surname;
	}

	/**
	 * @param surname the surname to set
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}

	public int getId() {
		return id;
	}
	
	public static class GetCurrentUser extends AsyncTask<Integer, Void, User> {
		@Override
		protected User doInBackground(Integer... params) {
			User u =
				WebServiceInterface.getInstance().getUserFromId(params[0]);
			return u;
		}
		
		@Override
		protected void onPostExecute(User result) {
			super.onPostExecute(result);
			AcademyProperties.getInstance().setUser(result);			
		}
	}
}
