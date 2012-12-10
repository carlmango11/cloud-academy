package com.carlnolan.cloudacademy.configuration;

import com.carlnolan.cloudacademy.R;
import com.carlnolan.cloudacademy.usermanagement.User;

public class AcademyProperties {
	private static AcademyProperties instance;
	
	/**
	 * Web Service properties
	 */
	private String coreAddress;
	private String webServiceUrl;
	private String fileProviderUrl;
	private String userPhotoProviderUrl;
	
	/** App preference key/value keys
	 */
	private String authKeyFilename;
	
	/**
	 * Failure strings
	 */
	private String authenticationFailure;
	
	/**
	 * The current user
	 */
	private User user;
	
	private AcademyProperties() {
		readValues();
	}
	
	public static AcademyProperties getInstance() {
		if(instance == null) {
			instance = new AcademyProperties();
		}
		return instance;
	}
	
	void readValues() {
		coreAddress = "http://192.168.2.7/cloudacademy";
		//coreAddress = "http://cloudacademy.carlnolan.com";
		webServiceUrl = coreAddress + "/webservice/";
		fileProviderUrl = coreAddress + "/fileprovider/getFile.php?";
		userPhotoProviderUrl = coreAddress + "/fileprovider/getUserPhoto.php?";
		
		authenticationFailure = "null";
		
		authKeyFilename = "existing_auth.key";
		
		user = null;
	}

	/**
	 * @return the databaseUrl
	 */
	public String getWebServiceUrl() {
		return webServiceUrl;
	}

	public String getFileProviderUrl() {
		return fileProviderUrl;
	}

	public String getUserPhotoProviderUrl() {
		return userPhotoProviderUrl;
	}

	public Object getAuthenticationFailureString() {
		return authenticationFailure;
	}
	
	public String getAuthKeyFilename() {
		return authKeyFilename;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
}
