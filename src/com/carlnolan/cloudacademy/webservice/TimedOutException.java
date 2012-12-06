package com.carlnolan.cloudacademy.webservice;

public class TimedOutException extends Exception {
	private static final long serialVersionUID = 1L;

	public String toString() {
		return "Could not connect to web service";
	}
}
