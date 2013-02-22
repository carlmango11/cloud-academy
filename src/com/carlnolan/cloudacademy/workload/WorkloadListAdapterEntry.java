package com.carlnolan.cloudacademy.workload;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.carlnolan.cloudacademy.inclass.Exam;
import com.carlnolan.cloudacademy.inclass.Homework;

public class WorkloadListAdapterEntry implements Comparable {
	private Calendar date;
	private List<Homework> homework;
	private List<Exam> exams;
	
	WorkloadListAdapterEntry(Calendar d) {
		date = d;
		homework = new ArrayList<Homework>();
		exams = new ArrayList<Exam>();
	}

	WorkloadListAdapterEntry(Calendar d, List<Homework> h, List<Exam> e) {
		date = d;
		homework = h;
		exams = e;
	}
	
	public void addHomework(Homework h) {
		homework.add(h);
	}
	
	public void addExam(Exam e) {
		exams.add(e);
	}

	public int compareTo(Object o) {
		WorkloadListAdapterEntry w = (WorkloadListAdapterEntry) o;
		
		return date.compareTo(w.date);
	}

	public List<Homework> getHomework() {
		return homework;
	}

	public List<Exam> getExams() {
		return exams;
	}

	public Calendar getDate() {
		return date;
	}

	/*public Object get(int index) {
		if (homework.size() > index) {
			return homework.get(index);
		} else {
			return exams.get(index - homework.size());
		}
	}*/
}
