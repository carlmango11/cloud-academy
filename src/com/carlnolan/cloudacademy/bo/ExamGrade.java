package com.carlnolan.cloudacademy.bo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.os.AsyncTask;

import com.carlnolan.cloudacademy.inclass.Exam;
import com.carlnolan.cloudacademy.inclass.Homework;
import com.carlnolan.cloudacademy.inclass.Exam.DownloadExamsForRangeListener;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ExamGrade {
	private int id;
	private String name;
	private String course;
	private String classname;
	private int grade;
	private Calendar date;
	
	public static List<ExamGrade> buildExamGradesFromJSON(String json) {
		Gson gson = new GsonBuilder()
    		.registerTypeAdapter(Calendar.class, new Homework.DateDeserializer())
			.create();
		System.out.println(json);
		ExamGrade [] gradeArray = gson.fromJson(json, ExamGrade[].class);
		
		return new ArrayList<ExamGrade>(Arrays.asList(gradeArray));
	}
	
	public String toString() {
		return name;
	}

	public int getGrade() {
		return grade;
	}
	
	public Calendar getDate() {
		return date;
	}

	public String getCourseName() {
		return course;
	}
}
