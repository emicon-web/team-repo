package com.bezkoder.springjwt.payload;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResetPasswordDto {

	@NotNull
	private String userEmail;
	
	@NotNull
	@Column(name = "user_password")
	private String userCred;
	
	@NotNull
	@Column(name = "user_password")
	private String confirmuserCred;
	
	@NotNull	
	@Column(name ="passcode" )
	private Integer tokencred;

	@NotNull
	private String token;

	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getUserCred() {
		return userCred;
	}
	public void setUserCred(String userCred) {
		this.userCred = userCred;
	}
	public String getConfirmuserCred() {
		return confirmuserCred;
	}
	public void setConfirmuserCred(String confirmuserCred) {
		this.confirmuserCred = confirmuserCred;
	}
	
	public Integer getTokencred() {
		return tokencred;
	}
	public void setTokencred(Integer tokencred) {
		this.tokencred = tokencred;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
