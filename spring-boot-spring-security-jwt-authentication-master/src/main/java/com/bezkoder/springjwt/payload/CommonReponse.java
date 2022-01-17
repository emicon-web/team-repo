package com.bezkoder.springjwt.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonReponse {
	private String message;

	public CommonReponse(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "CommonReponse{" +
				"message='" + message + '\'' +
				'}';
	}
}
