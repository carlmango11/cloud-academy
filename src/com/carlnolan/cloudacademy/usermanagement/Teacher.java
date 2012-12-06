package com.carlnolan.cloudacademy.usermanagement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Teacher extends User {
	
	public static Teacher buildTeacherFromJson(String json) {
		Gson gson = new GsonBuilder()
			.create();
		
		if(json.equals("[]")) {
			return null;
		}

		return gson.fromJson(json, Teacher[].class)[0];
	}
	
	public boolean isTeacher() {
		return true;
	}
}
