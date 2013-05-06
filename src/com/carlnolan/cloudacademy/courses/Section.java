package com.carlnolan.cloudacademy.courses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.carlnolan.cloudacademy.scheduling.Session;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Section implements Parcelable {
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
	
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
	}
	
	public static final Parcelable.Creator<Section> CREATOR = new Parcelable.Creator<Section>() {
        public Section createFromParcel(Parcel in) {
            Section section = new Section(in);
            return section;
        }

        public Section[] newArray(int size) {
            return new Section[size];
        }
    };

    private Section(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }
    
    @Override
    public boolean equals(Object o) {
    	Section s = (Section) o;
    	return this.getId() == s.getId();
    }
    
    @Override
    public int hashCode() {
        return Integer.valueOf(id).hashCode();
    }
}
