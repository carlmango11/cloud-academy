package com.carlnolan.cloudacademy.coursebrowser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.carlnolan.cloudacademy.MainActivity;
import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.asynctasks.AddNewExercise;
import com.carlnolan.cloudacademy.asynctasks.AssignHomework;
import com.carlnolan.cloudacademy.asynctasks.AttachLessons;
import com.carlnolan.cloudacademy.asynctasks.CheckAttendanceIsTaken;
import com.carlnolan.cloudacademy.asynctasks.DownloadSections;
import com.carlnolan.cloudacademy.courses.Exercise;
import com.carlnolan.cloudacademy.courses.Lesson;
import com.carlnolan.cloudacademy.courses.Section;
import com.carlnolan.cloudacademy.scheduling.Session;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

public class CourseBrowser extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.in_class_layout);
        
        //
    }
}
