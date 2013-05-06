package com.carlnolan.cloudacademy.courses;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import com.carlnolan.cloudacademy.scheduling.Session;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Course {
	private int id;
	private String name;
	private String ownerFirstname;
	private String ownerSurname;
	
	public static ArrayList<Course> buildCoursesFromJSON(String json) {
		Gson gson = new GsonBuilder()
			.create();
		Course [] courseArray = gson.fromJson(json, Course[].class);
		
		return new ArrayList<Course>(Arrays.asList(courseArray));
	}
	
	public String toString() {
		return name;
	}

	public int getId() {
		return id;
	}

	public String getOwner() {
		return ownerFirstname + " " + ownerSurname;
	}
}
