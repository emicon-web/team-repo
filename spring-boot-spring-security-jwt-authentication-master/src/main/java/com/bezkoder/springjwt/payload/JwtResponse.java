package com.bezkoder.springjwt.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String userName;
    private String userEmail;
    private final Collection<String> roles;
    private String merchantId;
    private String merchantName;
    private String shortCode;
    private String merchantType;
    private String userType;
    private Collection<String> actions;

    public JwtResponse(String accessToken, Long id, String userName, String userEmail, Collection<String> roles,
                       String merchantId, Collection<String> actions, String userType, String merchantName, String shortCode,String merchantType) {
        this.token = accessToken;
        this.id = id;
        this.userName = userName;
        this.userEmail = userEmail;
        this.roles = roles;
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.shortCode = shortCode;
        this.merchantType = merchantType;
        this.actions = actions;
        this.userType = userType;
    }

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	public String getMerchantType() {
		return merchantType;
	}

	public void setMerchantType(String merchantType) {
		this.merchantType = merchantType;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public Collection<String> getActions() {
		return actions;
	}

	public void setActions(Collection<String> actions) {
		this.actions = actions;
	}

	public Collection<String> getRoles() {
		return roles;
	}

  
}
