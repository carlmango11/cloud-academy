package com.carlnolan.cloudacademy.inclass;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.carlnolan.cloudacademy.courses.Exercise;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

public class Homework extends Exercise {
	@SerializedName("due")
	private Calendar dueDate;
	
	@SerializedName("completed")
	private boolean isComplete;
	
	@SerializedName("accLessonName")
	private String accompanyingLessonName;
	
	@SerializedName("accLessonId")
	private int accompanyingLessonId;
	
	private int courseId;
	
	@SerializedName("courseName")
	private String courseName;
	
	@SerializedName("teacher")
	private String receivingTeacher;
	
	public static List<Homework> buildHomeworkFromJSON(String json) {
		Gson gson = new GsonBuilder()
    		.registerTypeAdapter(Calendar.class, new DateDeserializer())
			.create();
		json = json.replace("\"completed\":\"0\"", "\"completed\":\"false\"");
		json = json.replace("\"completed\":\"1\"", "\"completed\":\"true\"");
		Log.d("json", "" + json);
		Homework [] homeworkArray = gson.fromJson(json, Homework[].class);
		
		return new ArrayList<Homework>(Arrays.asList(homeworkArray));
	}
	
	private static class DateDeserializer implements JsonDeserializer<Calendar> {
		public Calendar deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String unparsed = json.getAsJsonPrimitive().getAsString();
			
			Date date;
			try {
				date = df.parse(unparsed);
			} catch(ParseException pe) {
				Log.d("cloudacademy", "Could not parse date: " + unparsed);
				date = new Date();
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			
			return cal;
		}
	}
	
	public String getCourseName() {
		return courseName;
	}

	/**
	 * @return the accompanyingLessonName
	 */
	public String getAccompanyingLessonName() {
		return accompanyingLessonName;
	}

	/**
	 * @return the accompanyingLessonId
	 */
	public int getAccompanyingLessonId() {
		return accompanyingLessonId;
	}

	/**
	 * @return the courseId
	 */
	public int getCourseId() {
		return courseId;
	}

	/**
	 * @return the receivingTeacher
	 */
	public String getReceivingTeacher() {
		return receivingTeacher;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public Calendar getDueDate() {
		return dueDate;
	}
}
