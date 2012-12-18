package com.carlnolan.cloudacademy.inclass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.carlnolan.cloudacademy.scheduling.Session.CalendarDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Exam {
	private int id;
	private String name;
	private String description;
	private String courseName;
	private int courseId;
	private int sessionId;
	private Calendar date;
	
	public static List<Exam> buildExamsFromJSON(String json) {
		Gson gson = new GsonBuilder()
			.registerTypeAdapter(Calendar.class, new CalendarDeserializer())
			.create();
		
		Exam [] examArray = gson.fromJson(json, Exam[].class);
		
		return new ArrayList<Exam>(Arrays.asList(examArray));
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

	/**
	 * @param id the id to set
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
	 * @param name the name to set
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
	 * @param description the description to set
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
	 * @param courseName the courseName to set
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
	 * @param courseId the courseId to set
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
	 * @param sessionId the sessionId to set
	 */
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * @return the date
	 */
	public Calendar getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Calendar date) {
		this.date = date;
	}
}
