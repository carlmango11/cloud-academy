package com.carlnolan.cloudacademy.inclass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.os.AsyncTask;

import com.carlnolan.cloudacademy.courses.Lesson;
import com.carlnolan.cloudacademy.inclass.Homework.DateDeserializer;
import com.carlnolan.cloudacademy.scheduling.Session;
import com.carlnolan.cloudacademy.scheduling.Session.CalendarDeserializer;
import com.carlnolan.cloudacademy.scheduling.Session.DownloadExamsCallback;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Exam {
	private int id;
	private String name;
	private String description;
	private String courseName;
	private String className;
	private int courseId;
	private int sessionId;
	private Calendar time;

	public interface ExamCreatedListener {
		public void examCreated();
	}

	public interface DownloadExamsForRangeListener {
		public void onExamsForRangeDownloaded(List<Exam> exams);
	}

	public static List<Exam> buildExamsFromJSON(String json) {
		Gson gson = new GsonBuilder().registerTypeAdapter(Calendar.class,
				new Homework.DateDeserializer()).create();
		Exam[] examArray = gson.fromJson(json, Exam[].class);

		return new ArrayList<Exam>(Arrays.asList(examArray));
	}

	public static void addNewExam(ExamCreatedListener callback,
			Session thisSession, String newName, String newDesc) {
		new AddNewExam(callback, thisSession, newName, newDesc).execute();
	}
	
	public boolean equals(Object o) {
		Exam e = null;
    	try {
    		e = (Exam) o;
    	} catch(ClassCastException ex) {
    		return false;
    	}
    	
    	if(e == null) {
    		return false;
    	}
    	
    	return this.getId() == e.getId();
	}

	public static AsyncTask downloadExamsForRange(
			DownloadExamsForRangeListener c, Calendar start, Calendar end) {
		return downloadExamsForRange(c, start, end, -1);
	}

	public static AsyncTask downloadExamsForRange(
			DownloadExamsForRangeListener c, Calendar start, Calendar end,
			int course) {
		DownloadExamsForRange t = new DownloadExamsForRange(c, start, end,
				course);
		t.execute();
		return t;
	}

	private static class AddNewExam extends AsyncTask<Void, Void, Void> {
		private ExamCreatedListener callback;
		private String examName;
		private String examDescription;
		private Session session;

		public AddNewExam(ExamCreatedListener callback0, Session session0,
				String name0, String desc0) {
			callback = callback0;
			examName = name0;
			examDescription = desc0;
			session = session0;
		}

		@Override
		protected Void doInBackground(Void... params) {
			WebServiceInterface.getInstance().addNewExam(examName,
					examDescription, session.getId());
			return null;
		}

		@Override
		protected void onPostExecute(Void nothing) {
			super.onPostExecute(nothing);
			callback.examCreated();
		}
	}

	private static class DownloadExamsForRange extends
			AsyncTask<Void, Void, List<Exam>> {
		private DownloadExamsForRangeListener callback;
		private Calendar start;
		private Calendar end;
		private int courseId;

		DownloadExamsForRange(DownloadExamsForRangeListener c, Calendar s,
				Calendar e, int co) {
			callback = c;
			start = s;
			end = e;
			courseId = co;
		}

		@Override
		protected List<Exam> doInBackground(Void... params) {
			List<Exam> ls = WebServiceInterface.getInstance().getExamsForRange(
					start, end, courseId);
			return ls;
		}

		@Override
		protected void onPostExecute(List<Exam> result) {
			super.onPostExecute(result);
			callback.onExamsForRangeDownloaded(result);
		}
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	public String toString() {
		return name;
	}

	public String getClassName() {
		return className;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the courseName
	 */
	public String getCourseName() {
		return courseName;
	}

	/**
	 * @param courseName
	 *            the courseName to set
	 */
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	/**
	 * @return the courseId
	 */
	public int getCourseId() {
		return courseId;
	}

	/**
	 * @param courseId
	 *            the courseId to set
	 */
	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	/**
	 * @return the sessionId
	 */
	public int getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId
	 *            the sessionId to set
	 */
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * @return the date
	 */
	public Calendar getDate() {
		return time;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Calendar date) {
		this.time = date;
	}
}
