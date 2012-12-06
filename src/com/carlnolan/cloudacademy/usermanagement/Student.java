package com.carlnolan.cloudacademy.usermanagement;

import java.util.Arrays;
import java.util.List;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Student extends User {
	private int classId;
	
	public Student() {
		super();
		classId = -1;
	}

	public static List<Student> buildStudentsFromJSON(String json) {
		Gson gson = new GsonBuilder()
			.create();
		
		Student [] studentArray = gson.fromJson(json, Student[].class);
		List<Student> ls = Arrays.asList(studentArray);
	
		return ls;
		//TODO: See if deserializing an empty result == an empty list or null
	}
	
	public static Student buildStudentFromJson(String json) {
		Gson gson = new GsonBuilder()
			.create();
		
		if(json.equals("[]")) {
			return null;
		}

		return gson.fromJson(json, Student[].class)[0];
	}

	/**
	 * @return the classId
	 */
	public int getClassId() {
		return classId;
	}

	/**
	 * @param classId the classId to set
	 */
	public void setClassId(int classId) {
		this.classId = classId;
	}
	
	public boolean isTeacher() {
		return false;
	}
}
