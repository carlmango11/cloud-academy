package com.carlnolan.cloudacademy.courses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Exercise extends Content {
	@Override
	public boolean isExercise() {
		return true;
	}

	public static List<Exercise> buildExercisesFromJSON(String json) {
		Gson gson = new GsonBuilder()
			.create();
		Exercise [] exerciseArray = gson.fromJson(json, Exercise[].class);
		
		return new ArrayList<Exercise>(Arrays.asList(exerciseArray));
	}
}
