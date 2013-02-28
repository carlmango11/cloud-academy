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
	private boolean expanded;
	
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

	public int indexOfHomework(Homework updatedHomework) {
		return homework.indexOf(updatedHomework);
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

	public void toggleExpanded() {
		expanded = !expanded;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean b) {
		expanded = true;
	}

	public boolean isSameDay(Calendar selectedDate) {
		return date.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
				date.get(Calendar.DAY_OF_YEAR) == selectedDate.get(Calendar.DAY_OF_YEAR);
	}
}
