package com.carlnolan.cloudacademy.webservice;

public class WebServiceAuthentication {
	private int userId;
	private String authToken;
	
	public WebServiceAuthentication(String userId1, String token) {
		userId = Integer.parseInt(userId1);
		authToken = token;
	}
	
	public int getId() {
		return userId;
	}

	public String getToken() {
		return authToken;
	}
}
