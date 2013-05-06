package com.carlnolan.cloudacademy.courses;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.carlnolan.cloudacademy.scheduling.Session;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Lesson implements Parcelable {
	private int id;
	private String name;
	private String description;
	
	Lesson() {
		id = -1;
		name = "";
		description = "";
	}

	public static ArrayList<Lesson> buildLessonsFromJSON(String json) {
		Log.d("carl", ""+json);
		Gson gson = new GsonBuilder()
			.create();
		Lesson [] lessonArray = gson.fromJson(json, Lesson[].class);
		
		return new ArrayList<Lesson>(Arrays.asList(lessonArray));
	}

	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public int getId() {
		return id;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int arg1) {
    	Log.d("cloudacademy", "Parceling Lesson - " + name);
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(description);
	}
	
	public static final Parcelable.Creator<Lesson> CREATOR = new Parcelable.Creator<Lesson>() {
        public Lesson createFromParcel(Parcel in) {
            Lesson thisLesson = new Lesson(in);
            return thisLesson;
        }

        public Lesson[] newArray(int size) {
            return new Lesson[size];
        }
    };
    
    private Lesson(Parcel in) {
    	id = in.readInt();
    	name = in.readString();
    	description = in.readString();
    }
    
    @Override
    public boolean equals(Object o) {
    	Lesson l = null;
    	try {
    		l = (Lesson) o;
    	} catch(ClassCastException e) {
    		return false;
    	}
    	
    	if(l == null) {
    		return false;
    	}
    	
    	return this.getId() == l.getId();
    }
    
    @Override
    public int hashCode() {
        return Integer.valueOf(id).hashCode();
    }
}
