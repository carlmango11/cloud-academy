package com.carlnolan.cloudacademy.webservice;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.carlnolan.cloudacademy.MainActivity;
import com.carlnolan.cloudacademy.bo.ExamGrade;
import com.carlnolan.cloudacademy.configuration.AcademyProperties;
import com.carlnolan.cloudacademy.courses.Content;
import com.carlnolan.cloudacademy.courses.Course;
import com.carlnolan.cloudacademy.courses.Exercise;
import com.carlnolan.cloudacademy.courses.LearningMaterial;
import com.carlnolan.cloudacademy.courses.Lesson;
import com.carlnolan.cloudacademy.courses.Section;
import com.carlnolan.cloudacademy.inclass.Exam;
import com.carlnolan.cloudacademy.inclass.Homework;
import com.carlnolan.cloudacademy.progress.RecordGrades;
import com.carlnolan.cloudacademy.scheduling.Session;
import com.carlnolan.cloudacademy.usermanagement.Student;
import com.carlnolan.cloudacademy.usermanagement.Teacher;
import com.carlnolan.cloudacademy.usermanagement.User;

/**
 * This class will handle the connection to the database.
 * @author Carl
 *
 */
public class WebServiceInterface {
	private String url;
	private WebServiceAuthentication authentication;
	
	private static WebServiceInterface instance;
	
	private static final String NO_NEXT_SESSION_ERROR = "-1";
	
	private WebServiceInterface(String connUrl) {
		url = connUrl;
	}

	public int getUserId() {
		return authentication.getId();
	}
	
	public static WebServiceInterface login(String username, String password) {
		WebServiceInterface newInstance =
				new WebServiceInterface(
						AcademyProperties.getInstance().getWebServiceUrl());
		
		String passwordHash = sha1(password);
		System.out.println(passwordHash);
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("username", username));
        nameValuePairs.add(new BasicNameValuePair("password", passwordHash));
        
        String result = newInstance.callService(
        		"authenticateUser",
        		nameValuePairs,
        		false);
        
        if(result.equals("-1")) {
        	instance = null;
        } else {
        	//Separate out the auth_token and the users id
        	String token = result.substring(0, 32);
        	String userId = result.substring(32, result.length());
        	
    		newInstance.setAuthentication(
    				new WebServiceAuthentication(userId, token));
    		
    		instance = newInstance;
        }
        
		return instance;
	}
	
	public static WebServiceInterface getInstance() {
		return instance;
	}
	
	public User getUserFromId(int id) {
		// Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("id", "" + id));

        String json = callService(
        		"getUserFromId",
        		nameValuePairs,
        		true);
        
        char typeIndicator = json.charAt(0);
        String object = json.substring(1, json.length());
        
        User user;
        if(typeIndicator == '0') {
        	user = Student.buildStudentFromJson(object);
        } else {
        	user = Teacher.buildTeacherFromJson(object);
        }
        Log.d("carl when build", user.getFirstname() + " " + user.getId());
        
        return user;
	}

	public boolean registerGCMId(String regId) {
		int userId = AcademyProperties.getInstance().getUser().getId();
		
		// Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("user", "" + userId));
        nameValuePairs.add(new BasicNameValuePair("regid", "" + regId));

        String result = callService(
        		"registerGCM",
        		nameValuePairs,
        		true);
        
        return result.equals("1");
	}

	public ArrayList<Session> getSessionsForDate(Date date) {
		Format formatter = new SimpleDateFormat("yyyy-MM-dd");
		
        // Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
        nameValuePairs.add(new BasicNameValuePair("date", formatter.format(date.getTime())));
        nameValuePairs.add(new BasicNameValuePair("user", "" + authentication.getId()));
        addIsTeacherParameter(nameValuePairs);

        String json = callService(
        		"getSessionsForDate",
        		nameValuePairs,
        		true);
        
        ArrayList<Session> sessions = Session.buildSessionsFromJSON(json);
        return sessions;
	}

	//I think this is useless
	public String getCourseName(int course) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("id", "" + course));

        return callService(
        		"getCourseName",
        		nameValuePairs,
        		true);
	}

	public ArrayList<Course> getCourses(boolean isTeacher) {
		// Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("user", "" + authentication.getId()));
        nameValuePairs.add(new BasicNameValuePair("isteacher", isTeacher ? "1" : "0"));

        String json = callService(
        		"getCourses",
        		nameValuePairs,
        		true);
        
        ArrayList<Course> courses = Course.buildCoursesFromJSON(json);
        return courses;
	}

	public ArrayList<Section> getSections(Integer integer) {
		// Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("course", "" + integer));

        String json = callService(
        		"getSections",
        		nameValuePairs,
        		true);
        
        ArrayList<Section> sections = Section.buildSectionsFromJSON(json);
        return sections;
	}

	public ArrayList<Lesson> getLessons(Integer integer) {
		// Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("section", "" + integer));

        String json = callService(
        		"getLessons",
        		nameValuePairs,
        		true);
        
        ArrayList<Lesson> lessons = Lesson.buildLessonsFromJSON(json);
        return lessons;
	}

	public List<Exercise> getExercises(Integer integer) {
		// Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("lesson", "" + integer));
        nameValuePairs.add(new BasicNameValuePair("is_exercise", "1"));
       
        String json = callService(
        		"getContent",
        		nameValuePairs,
        		true);
        
        List<Exercise> content = Exercise.buildExercisesFromJSON(json);
        return content;
	}

	/**
	 * Gets a list of exercises assigned as homework
	 * @param courseId Course Id to fetch homework for
	 * @param date The due date of homework to check
	 * @param classId 
	 * @return
	 */
	public List<Homework> getHomeworkDue(int courseId, String date, int classId) {
		boolean isStudent = !AcademyProperties.getInstance().getUser().isTeacher();
		int userId = AcademyProperties.getInstance().getUser().getId();
		
		// Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
        nameValuePairs.add(new BasicNameValuePair("course", "" + courseId));
        nameValuePairs.add(new BasicNameValuePair("date", "" + date));
        nameValuePairs.add(new BasicNameValuePair("class", "" + classId));
        nameValuePairs.add(new BasicNameValuePair("user", "" + userId));
        nameValuePairs.add(new BasicNameValuePair("is_student", isStudent ? "1" : "0"));

        String json = callService(
        		"getHomeworkDue",
        		nameValuePairs,
        		true);

        List<Homework> homework = Homework.buildHomeworkFromJSON(json);
        return homework;
	}

	/**
	 * Takes an exam and returns the id of the class who took that exam
	 * @param examId
	 * @return
	 */
	public int getClassIdFromExamId(int examId) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("exam", "" + examId));
       
        String result = callService(
        		"getClassIdFromExamId",
        		nameValuePairs,
        		true);
        return Integer.parseInt(result);
	}

	public List<Homework> getHomeworkDueForRange(Calendar start, Calendar end) {
		int userId = AcademyProperties.getInstance().getUser().getId();
		
		//Turn calendars into sql strings
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String startString = formatter.format(start.getTime());
		String endString = formatter.format(end.getTime());
		
		// Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
        nameValuePairs.add(new BasicNameValuePair("userid", "" + userId));
        nameValuePairs.add(new BasicNameValuePair("start", startString));
        nameValuePairs.add(new BasicNameValuePair("end", endString));

        String json = callService(
        		"getHomeworkDueForRange",
        		nameValuePairs,
        		true);
        
        List<Homework> homework = Homework.buildHomeworkFromJSON(json);
        return homework;
	}

	public List<Exam> getExamsForRange(Calendar start, Calendar end, int courseId) {
		User user = AcademyProperties.getInstance().getUser();
		int userId = user.getId();
		int isTeacher = user.isTeacher() ? 1 : 0;
		
		//Turn calendars into sql strings
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String startString = formatter.format(start.getTime());
		String endString = formatter.format(end.getTime());
		
		// Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
        nameValuePairs.add(new BasicNameValuePair("userid", "" + userId));
        nameValuePairs.add(new BasicNameValuePair("start", startString));
        nameValuePairs.add(new BasicNameValuePair("end", endString));
        nameValuePairs.add(new BasicNameValuePair("isteacher", "" + isTeacher));
        nameValuePairs.add(new BasicNameValuePair("course", "" + courseId));

        String json = callService(
        		"getExamsForRange",
        		nameValuePairs,
        		true);
        
        List<Exam> exams = Exam.buildExamsFromJSON(json);
        return exams;
	}

	public List<Exam> getExamsForSession(int sessionId) {
		// Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("session", "" + sessionId));
       
        String json = callService(
        		"getExamsForSession",
        		nameValuePairs,
        		true);
        
        List<Exam> content = Exam.buildExamsFromJSON(json);
        return content;
	}

	public List<LearningMaterial> getLearningMaterial(Integer integer) {
		// Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("lesson", "" + integer));
        nameValuePairs.add(new BasicNameValuePair("is_exercise", "0"));
       
        String json = callService(
        		"getContent",
        		nameValuePairs,
        		true);
        
        List<LearningMaterial> content = LearningMaterial.buildLearningMaterialFromJSON(json);
        return content;
	}

	public void updateHomeworkCompletion(int id, boolean isComplete) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("id", "" + id));
        nameValuePairs.add(new BasicNameValuePair("value", isComplete ? "1" : "0"));
       
        callService(
        		"updateHomeworkCompletion",
        		nameValuePairs,
        		true);
	}

	/**
	 * Give it a course ID and it will return a list of all grades ever gotten
	 * for exams in that course
	 * @param courseId
	 * @return
	 */
	public List<ExamGrade> getGradesForCourse(int courseId) {
		int userId = AcademyProperties.getInstance().getUser().getId();
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
        nameValuePairs.add(new BasicNameValuePair("course", "" + courseId));
        nameValuePairs.add(new BasicNameValuePair("user", "" + userId));
        addIsTeacherParameter(nameValuePairs);
        
        String json = callService(
        		"getGradesForCourse",
        		nameValuePairs,
        		true);
        
        List<ExamGrade> grades = ExamGrade.buildExamGradesFromJSON(json);
        return grades;
	}

	public Map<Integer, Integer> getExistingGrades(int examId) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("exam", "" + examId));
        
        String json = callService(
        		"getExistingGrades",
        		nameValuePairs,
        		true);
        
        return RecordGrades.GradesHolder.buildMapFromGrades(json);
	}

	public void saveGrade(int examId, int id, int g) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
        nameValuePairs.add(new BasicNameValuePair("student", "" + id));
        nameValuePairs.add(new BasicNameValuePair("exam", "" + examId));
        nameValuePairs.add(new BasicNameValuePair("grade", "" + g));
        
        callService(
        		"saveGrade",
        		nameValuePairs,
        		true);
	}

	public Exercise addNewExercise(String name, String desc) {
		// Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("name", name));
        nameValuePairs.add(new BasicNameValuePair("description", desc));
       
        String json = callService(
        		"createExercise",
        		nameValuePairs,
        		true);
        
        List<Exercise> ls =
        		Exercise.buildExercisesFromJSON(json);
        return ls.get(0);
	}

	public void addNewExam(String name, String desc, int id) {
		// Add data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("name", name));
        nameValuePairs.add(new BasicNameValuePair("description", desc));
        nameValuePairs.add(new BasicNameValuePair("session_id", "" + id));
       
        callService(
        		"createExam",
        		nameValuePairs,
        		true);
	}

	public ArrayList<Lesson> getLessonsForSession(int integer) {
		// Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("session", "" + integer));
       
        String json = callService(
        		"getLessonsForSession",
        		nameValuePairs,
        		true);
        
        ArrayList<Lesson> lessons = Lesson.buildLessonsFromJSON(json);
        return lessons;
	}

	public boolean checkAttendanceTaken(int sessionId) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("session", "" + sessionId));
        
        String resultString = callService(
        		"checkAttendanceTaken",
        		nameValuePairs,
        		true);
        
        return resultString.equals("1") ? true : false;
	}

	public void attachLessonToSession(int sessionId, int lessonId) {
		// Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("session", "" + sessionId));
        nameValuePairs.add(new BasicNameValuePair("lesson", "" + lessonId));
       
        callService("attachLessonToSession",
        		nameValuePairs,
        		true);
	}

	public void resetSessionAttendance(int sessionId) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("session", "" + sessionId));
        
        callService("resetSessionAttendance",
        		nameValuePairs,
        		true);
	}

	/**
	 * Marks student as attended a given session
	 * @param sessionId Session attended
	 * @param id Student's ID
	 */
	public void recordStudentAsAttended(int sessionId, int id) {
		// Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("session", "" + sessionId));
        nameValuePairs.add(new BasicNameValuePair("student", "" + id));
       
        callService("markStudentAttended",
        		nameValuePairs,
        		true);
	}

	public void setSessionAttendanceTaken(int sessionId) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("session", "" + sessionId));
        
        callService("setSessionAttendanceTaken",
        		nameValuePairs,
        		true);
	}

	public List<Integer> getAttendanceList(int id) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("session", "" + id));
        
        String json = callService("getAttendanceList",
        		nameValuePairs,
        		true);
        
        List<Integer> ls;
        
        if(json.length() > 0) {
	        String [] idStrings = json.split(",");
	        Integer [] idInts = new Integer[idStrings.length];
	        
	        for(int i=0;i<idInts.length;i++) {
	        	idInts[i] = Integer.parseInt(idStrings[i]);
	        }
	        
	        ls = Arrays.asList(idInts);
        } else {
        	ls = new ArrayList<Integer>();
        }
        
        return ls;
	}

	/**
	 * Get all students in a class
	 * @param id Id of class to fetch list for
	 * @return List 
	 */
	public List<Student> getClassList(int id) {
		// Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("class", "" + id));
        
        String json = callService(
        		"getClassList",
        		nameValuePairs,
        		true);
        
        List<Student> students = Student.buildStudentsFromJSON(json);
        
        //Get photos
        for(Student s:students) {
	    	s.downloadPhoto();
        }
        
        return students;
	}

	/**
	 * Takes a WebServiceAuthenticaion and tests if its valid
	 * @param webServiceAuthentication
	 * @return
	 */
	public boolean testAuthentication(
			WebServiceAuthentication webServiceAuthentication) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		setAuthentication(webServiceAuthentication);
        
        String result = callService(
        		"checkAuthentication",
        		nameValuePairs,
        		true);
        
        return result.equals("1") ? true : false;
	}

	/**
	 * This method will assign the exercise passed as homework due on the
	 * passed date.
	 * @param e Exercise to assign
	 * @param date Date of month when due
	 * @param month Month due
	 * @param year Year due
	 */
	public boolean assignHomework(Exercise e, int classId, int courseId, String dueDate) {
		// Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("exercise", "" + e.getId()));
        nameValuePairs.add(new BasicNameValuePair("due", dueDate));
        nameValuePairs.add(new BasicNameValuePair("course", "" + courseId));
        nameValuePairs.add(new BasicNameValuePair("class", "" + classId));
        
        //Result isn't used yet but prob in future to check for failure
        String result = callService(
        		"assignHomework",
        		nameValuePairs,
        		true);
        
        if(result.equals(NO_NEXT_SESSION_ERROR)) {
        	//No "next session" to assign to:
        	return false;
        }
        
        return true;
	}
	
	public String getAuthPostParameters() {
		return "auth_id=" + authentication.getId() + "&auth_token=" + authentication.getToken();
	}
	
	private String callService(String name, List<NameValuePair> params, boolean withAuthentication)
		//throws TimedOutException
	{
		if(withAuthentication) {
			//Add the security parameters:
			params.add(new BasicNameValuePair(
					"auth_id", "" + authentication.getId()));
			params.add(new BasicNameValuePair(
					"auth_token", "" + authentication.getToken()));
		}
		
		String address = url + name + ".php";
		Log.d("cloudacademy", "Attempting to reach: " + address);
		
		HttpPost httpPost = new HttpPost(address);
		HttpClient httpClient = new DefaultHttpClient();
		
        try {
        	httpPost.setEntity(new UrlEncodedFormEntity(params));

	        // Execute HTTP Post Request
	        HttpResponse response = httpClient.execute(httpPost);
	        InputStream is = response.getEntity().getContent();
	        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

	        String line = "";
	        StringBuilder total = new StringBuilder();

	        // Read response until the end
	        while ((line = rd.readLine()) != null) {
	            total.append(line);
	        }
	        
	        // Return full string
	        return total.toString();
		} catch (UnsupportedEncodingException e) {
			Log.e("cloudacademy", "Cannot encode URL");
		} catch (ClientProtocolException cpe) {
			Log.e("cloudacademy", "Client protocol exception or something else: "
					+ cpe.getMessage());
		} catch (IOException cpe2) {
			Log.e("cloudacademy", "22Client protocol exception or something else: "
					+ cpe2.getMessage());
			//throw new TimedOutException();
		}
        
        return null;
	}

	/**
	 * Adds on the "isTeacher" parameter to the supplied List of params using
	 * value supplied by the User object in AcademyProperties
	 * @param nameValuePairs The List to add param to
	 */
	private static void addIsTeacherParameter(List<NameValuePair> nameValuePairs) {
		boolean isTeacherBool = AcademyProperties.getInstance().getUser().isTeacher();
		String isTeacher = isTeacherBool ? "1" : "0";
		
        nameValuePairs.add(new BasicNameValuePair("isTeacher", isTeacher));
	}
	
	public static String sha1(String string) {
	    byte [] hash = {};

	    try {
	        hash = MessageDigest.getInstance("SHA-1").digest(string.getBytes("UTF-8"));
	    } catch (NoSuchAlgorithmException e) {
	        Log.d("cloudacademy", "SHA-1 not supported. Cannot authenticate user");
	    } catch (UnsupportedEncodingException e) {
	        Log.d("cloudacademy", "UTF-8 not supported");
	    }

	    StringBuilder hex = new StringBuilder(hash.length * 2);

	    for (byte b : hash) {
	        int i = (b & 0xFF);
	        if (i < 0x10) hex.append('0');
	        hex.append(Integer.toHexString(i));
	    }

	    return hex.toString();
	}
	
	private void setAuthentication(
			WebServiceAuthentication webServiceAuthentication) {
		authentication = webServiceAuthentication;		
	}
}
