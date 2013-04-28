package com.carlnolan.cloudacademy.bo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.carlnolan.cloudacademy.inclass.Homework;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ExamGrade {
	private int id;
	private String name;
	private String course;
	private int grade;
	private Calendar date;
	
	public static List<ExamGrade> buildExamGradesFromJSON(String json) {
		Gson gson = new GsonBuilder()
    		.registerTypeAdapter(Calendar.class, new Homework.DateDeserializer())
			.create();
		
		ExamGrade [] gradeArray = gson.fromJson(json, ExamGrade[].class);
		
		return new ArrayList<ExamGrade>(Arrays.asList(gradeArray));
	}
}
