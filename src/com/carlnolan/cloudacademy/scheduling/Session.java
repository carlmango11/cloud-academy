package com.carlnolan.cloudacademy.scheduling;

import java.lang.reflect.Type;
import java.sql.Time;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.carlnolan.cloudacademy.configuration.AcademyProperties;
import com.carlnolan.cloudacademy.courses.Exercise;
import com.carlnolan.cloudacademy.inclass.Exam;
import com.carlnolan.cloudacademy.inclass.Homework;
import com.carlnolan.cloudacademy.usermanagement.User;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

public class Session implements Parcelable {
	private int id;
	private Calendar starts;
	private Calendar ends;
	private int classId;
	private int lead;
	private int course;
	private String room;
	
	//Wanted these to be transient but GSON won't let me
	@SerializedName("firstname")
	private String leadFirstName;
	@SerializedName("surname")
	private String leadSurname;
	@SerializedName("coursename")
	private String courseName;
	@SerializedName("name")
	private String className;
	
	public interface DownloadExamsCallback {
		public void examsDownloaded(List<Exam> exams);
	}
	
	public String toString() {
		return "" + id;
	}

	public static ArrayList<Session> buildSessionsFromJSON(String json) {
		Gson gson = new GsonBuilder()
        	.registerTypeAdapter(Calendar.class, new CalendarDeserializer())
			.create();

		Session [] sessionArray = gson.fromJson(json, Session[].class);
		return new ArrayList<Session>(Arrays.asList(sessionArray));
	}

	public void downloadExams(DownloadExamsCallback callback) {
		new DownloadExams(callback, id).execute();
	}
	
	private static class DownloadExams extends AsyncTask<Void, Void, List<Exam>> {
		private DownloadExamsCallback callback;
		private int sessionId;
		
		DownloadExams(DownloadExamsCallback c, int s) {
			callback = c;
			sessionId = s;
		}
		
		@Override
		protected List<Exam> doInBackground(Void... params) {
			List<Exam> ls = WebServiceInterface.getInstance()
					.getExamsForSession(sessionId);
			return ls;
		}

		@Override
		protected void onPostExecute(List<Exam> result) {
			super.onPostExecute(result);
			callback.examsDownloaded(result);
		}
	}
	
	public static class CalendarDeserializer implements JsonDeserializer<Calendar> {
		public Calendar deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String unparsed = json.getAsJsonPrimitive().getAsString();
			
			Date date;
			try {
				date = df.parse(unparsed);
			} catch(ParseException pe) {
				Log.d("cloudacademy", "Could not parse date: " + unparsed);
				date = new Date();
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			
			return cal;
		}
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		Log.d("cloudacademy", "Parcelling session: " + toString());
		dest.writeInt(id);
		dest.writeLong(starts.getTimeInMillis());
		dest.writeLong(ends.getTimeInMillis());
		dest.writeInt(classId);
		dest.writeInt(lead);
		dest.writeInt(course);
		dest.writeString(room);
		
		dest.writeString(leadFirstName);
		dest.writeString(leadSurname);
		dest.writeString(courseName);
		dest.writeString(className);
	}
	
	public static final Parcelable.Creator<Session> CREATOR = new Parcelable.Creator<Session>() {
        public Session createFromParcel(Parcel in) {
            Session thisSession = new Session(in);
            return thisSession;
        }

        public Session[] newArray(int size) {
            return new Session[size];
        }
    };

    private Session(Parcel in) {
		Log.d("cloudacademy", "Rebuilding session from parcel");
        id = in.readInt();
        
        starts = Calendar.getInstance();
        starts.setTimeInMillis(in.readLong());
        ends = Calendar.getInstance();
        ends.setTimeInMillis(in.readLong());
        
        classId = in.readInt();
        lead = in.readInt();
        course = in.readInt();
        room = in.readString();
        
        leadFirstName = in.readString();
        leadSurname = in.readString();
        courseName = in.readString();
        className = in.readString();
    }

	public int getLeadId() {
		return lead;
	}

	public boolean isLedByCurrentUser() {
		return getLeadId() == AcademyProperties.getInstance().getUser().getId();
	}

	public String getStartFinishTimes() {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		
		return formatter.format(starts.getTime()) + "\n" +
				formatter.format(ends.getTime());
	}

	public String getClassName() {
		return className;
	}

	public int getCourseId() {
		return course;
	}

	public int getClassId() {
		return classId;
	}

	public String getStartDateSQL() {
		Format formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(starts.getTime());
	}
	
	public int getId() {
		return id;
	}

	public String getCourseName() {
		return courseName;
	}
	
	public String getLeadName() {
		return leadFirstName + " " + leadSurname;
	}
	
	public String getStartsNice() {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		return formatter.format(starts.getTime());
	}
	
	public String getRoom() {
		return room;
	}
}
