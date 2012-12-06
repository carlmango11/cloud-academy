package com.carlnolan.cloudacademy.courses;

import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Section {
	private int id;
	private String name;
	
	public static ArrayList<Section> buildSectionsFromJSON(String json) {
		Gson gson = new GsonBuilder()
			.create();
		Section [] sectionArray = gson.fromJson(json, Section[].class);

		return new ArrayList<Section>(Arrays.asList(sectionArray));
	}
	
	public String toString() {
		return name;
	}

	public int getId() {
		return id;
	}
}
