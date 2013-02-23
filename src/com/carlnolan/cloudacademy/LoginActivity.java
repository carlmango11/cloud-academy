package com.carlnolan.cloudacademy;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.carlnolan.cloudacademy.asynctasks.TestAuthentication;
import com.carlnolan.cloudacademy.configuration.AcademyProperties;
import com.carlnolan.cloudacademy.webservice.WebServiceAuthentication;
import com.carlnolan.cloudacademy.webservice.WebServiceInterface;

public class LoginActivity extends Activity
	implements TestAuthentication.TestAuthenticationResultListener {
	
	private Button loginButton;
	private EditText usernameField;
	private EditText passwordField;
	
	private String authKeyFilename;

	static final int DIALOG_LOGIN_INVALID = 0;
	static final int DIALOG_LOADING = 1;
	static final int DIALOG_RESTORING = 2;
	
	void login(String username, String password) {
		showDialog(DIALOG_LOADING);
		new LogIn().execute(username, password);
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		
		loginButton = (Button) findViewById(R.id.login_button);
        usernameField = (EditText) findViewById(R.id.username_field);
        passwordField = (EditText) findViewById(R.id.password_field);
        
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	login("" + usernameField.getText(),
            		"" + passwordField.getText());
            }
        });
		
        //Unfinished stuff here: check for an existing authentication key
        authKeyFilename =
        		AcademyProperties.getInstance().getAuthKeyFilename();
        
		try {
			DataInputStream in = new DataInputStream(openFileInput(authKeyFilename));
            String id = in.readUTF();
            String key = in.readUTF();
            
            showDialog(DIALOG_RESTORING);
            
            WebServiceAuthentication auth =
            		new WebServiceAuthentication(id, key);
            new TestAuthentication(this).execute(auth);
		} catch (IOException e) {
			Log.d("cloudacademy", "No existing key found");
        }
    }

	public void loginResponseReceived(boolean result) {
		dismissDialog(DIALOG_LOADING);
		
		if(result) {
	        continueToMainScreen();
		} else {
			showDialog(DIALOG_LOGIN_INVALID);
		}
	}
	
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog;
	    switch(id) {
	    case DIALOG_LOGIN_INVALID:
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage("Login Credentials Invalid")
	    	       .setCancelable(false)
	    	       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	           }
	    	       });
	    	dialog = builder.create();
	        break;
	    case DIALOG_LOADING:
	    	dialog = ProgressDialog.show(LoginActivity.this, "", 
                    "Logging In. Please wait...", true);
	    	break;
	    case DIALOG_RESTORING:
	    	dialog = ProgressDialog.show(LoginActivity.this, "", 
                    "Attempting to restore session. Please wait...", true);
	    	break;
	    default:
	        dialog = null;
	    }
	    return dialog;
	}
	
	private void continueToMainScreen() {
		final Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
        finish();
	}
    
	private class LogIn extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			//Try to log in. If this returns null it failed
			//Otherwise it returns an instance of the DB connection
			WebServiceInterface instance =
					WebServiceInterface.login(params[0], params[1]);
			
			return instance != null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			loginResponseReceived(result);
		}
	}

	public void onTestAuthenticationResultReceived(boolean result) {
		dismissDialog(DIALOG_RESTORING);
		
		if(result) {
			//User is already authenticated. We can continue login
			continueToMainScreen();
		}
	}
}
