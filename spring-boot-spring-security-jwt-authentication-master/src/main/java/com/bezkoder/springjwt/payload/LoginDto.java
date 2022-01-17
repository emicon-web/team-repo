package com.bezkoder.springjwt.payload;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginDto {

	@NotBlank
	private String userName;

	@NotBlank
    @Column(name = "user_password")
	private String userCred;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserCred() {
		return userCred;
	}

	public void setUserCred(String userCred) {
		this.userCred = userCred;
	}

	
}
