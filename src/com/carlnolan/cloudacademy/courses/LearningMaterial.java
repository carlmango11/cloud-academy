package com.carlnolan.cloudacademy.courses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LearningMaterial extends Content {
	@Override
	public boolean isExercise() {
		return false;
	}

	public static List<LearningMaterial> buildLearningMaterialFromJSON(String json) {
		Gson gson = new GsonBuilder()
			.create();
		LearningMaterial [] lmArray = gson.fromJson(json, LearningMaterial[].class);
		
		return new ArrayList<LearningMaterial>(Arrays.asList(lmArray));
	}
}
