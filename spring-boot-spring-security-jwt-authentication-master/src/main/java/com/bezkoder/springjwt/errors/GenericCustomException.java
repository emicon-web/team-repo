package com.bezkoder.springjwt.errors;

import java.util.Date;

public class GenericCustomException extends RuntimeException {
	
	String message;
	Date timestamp;

	public GenericCustomException(String message) {
		this.message = message;
	}
	
	public GenericCustomException(String message, Date timestamp) {
		super();
		this.message = message;
		this.timestamp = timestamp;
	}
	
	public GenericCustomException() {
		// TODO Auto-generated constructor stub
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "GenericCustomException [message=" + message + ", timestamp=" + timestamp + "]";
	}
	
	
	
	
	
	

}
