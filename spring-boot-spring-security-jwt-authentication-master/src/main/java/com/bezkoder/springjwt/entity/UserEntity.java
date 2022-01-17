package com.bezkoder.springjwt.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.bezkoder.springjwt.models.Role;

public class UserEntity {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @NotBlank
	    @Size(max = 20)
	    private String userName;

	    @NotBlank
	    @Size(max = 20)
	    private String userFirstName;

	    @NotBlank
	    @Size(max = 20)
	    private String userLastName;

	    @NotBlank
	    @Size(max = 50)
	    @Email
	    private String userEmail;


	    private String userPhone;

	    @Size(max = 120)
	    @Column(name = "user_password")
	    private String userCred;


	    private Boolean userIsActive;


	    private Boolean userIsLocked;


	    private Date userLastSuccessLogin;


	    private Date userLastFailedLogin;

	    @Column(columnDefinition = "integer default 0", name = "user_no_failed_password")
	    private Integer userNoFailedAttempts;

	    private Date userBlockReleaseTime;

	    @NotNull
	    private String userCreatedBy;

	    @NotNull
	    @CreationTimestamp
	    private Date userCreatedDate;

	    private String userUpdatedBy;

	    @UpdateTimestamp
	    private Date userUpdatedDate;

	    @Column(columnDefinition = "boolean default false")
	    private Boolean isDeleted = false;

	    @ManyToMany(fetch = FetchType.LAZY)
	    @JoinTable(name = "user_roles",
	            joinColumns = @JoinColumn(name = "user_id"),
	            inverseJoinColumns = @JoinColumn(name = "role_id"))
	    private Set<Role> roles = new HashSet<>();

	    @Column(name = "issuerID", nullable = true)
	    private String issuerId;

	    @NotNull
	    @Enumerated(EnumType.STRING)
	    private String userType;

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

		public String getUserFirstName() {
			return userFirstName;
		}

		public void setUserFirstName(String userFirstName) {
			this.userFirstName = userFirstName;
		}

		public String getUserLastName() {
			return userLastName;
		}

		public void setUserLastName(String userLastName) {
			this.userLastName = userLastName;
		}

		public String getUserEmail() {
			return userEmail;
		}

		public void setUserEmail(String userEmail) {
			this.userEmail = userEmail;
		}

		public String getUserPhone() {
			return userPhone;
		}

		public void setUserPhone(String userPhone) {
			this.userPhone = userPhone;
		}

		public String getUserCred() {
			return userCred;
		}

		public void setUserCred(String userCred) {
			this.userCred = userCred;
		}

		public Boolean getUserIsActive() {
			return userIsActive;
		}

		public void setUserIsActive(Boolean userIsActive) {
			this.userIsActive = userIsActive;
		}

		public Boolean getUserIsLocked() {
			return userIsLocked;
		}

		public void setUserIsLocked(Boolean userIsLocked) {
			this.userIsLocked = userIsLocked;
		}

		public Date getUserLastSuccessLogin() {
			return userLastSuccessLogin;
		}

		public void setUserLastSuccessLogin(Date userLastSuccessLogin) {
			this.userLastSuccessLogin = userLastSuccessLogin;
		}

		public Date getUserLastFailedLogin() {
			return userLastFailedLogin;
		}

		public void setUserLastFailedLogin(Date userLastFailedLogin) {
			this.userLastFailedLogin = userLastFailedLogin;
		}

		public Integer getUserNoFailedAttempts() {
			return userNoFailedAttempts;
		}

		public void setUserNoFailedAttempts(Integer userNoFailedAttempts) {
			this.userNoFailedAttempts = userNoFailedAttempts;
		}

		public Date getUserBlockReleaseTime() {
			return userBlockReleaseTime;
		}

		public void setUserBlockReleaseTime(Date userBlockReleaseTime) {
			this.userBlockReleaseTime = userBlockReleaseTime;
		}

		public String getUserCreatedBy() {
			return userCreatedBy;
		}

		public void setUserCreatedBy(String userCreatedBy) {
			this.userCreatedBy = userCreatedBy;
		}

		public Date getUserCreatedDate() {
			return userCreatedDate;
		}

		public void setUserCreatedDate(Date userCreatedDate) {
			this.userCreatedDate = userCreatedDate;
		}

		public String getUserUpdatedBy() {
			return userUpdatedBy;
		}

		public void setUserUpdatedBy(String userUpdatedBy) {
			this.userUpdatedBy = userUpdatedBy;
		}

		public Date getUserUpdatedDate() {
			return userUpdatedDate;
		}

		public void setUserUpdatedDate(Date userUpdatedDate) {
			this.userUpdatedDate = userUpdatedDate;
		}

		public Boolean getIsDeleted() {
			return isDeleted;
		}

		public void setIsDeleted(Boolean isDeleted) {
			this.isDeleted = isDeleted;
		}

		public Set<Role> getRoles() {
			return roles;
		}

		public void setRoles(Set<Role> roles) {
			this.roles = roles;
		}

		public String getIssuerId() {
			return issuerId;
		}

		public void setIssuerId(String issuerId) {
			this.issuerId = issuerId;
		}

		public String getUserType() {
			return userType;
		}

		public void setUserType(String userType) {
			this.userType = userType;
		}

		public UserEntity(Long id, @NotBlank @Size(max = 20) String userName,
				@NotBlank @Size(max = 20) String userFirstName, @NotBlank @Size(max = 20) String userLastName,
				@NotBlank @Size(max = 50) @Email String userEmail, @NotBlank @Size(max = 20) String userPhone,
				@Size(max = 120) String userCred, Boolean userIsActive, Boolean userIsLocked,
				Date userLastSuccessLogin, Date userLastFailedLogin, Integer userNoFailedAttempts,
				Date userBlockReleaseTime, @NotNull String userCreatedBy, @NotNull Date userCreatedDate,
				String userUpdatedBy, Date userUpdatedDate, Boolean isDeleted, Set<Role> roles, String issuerId,
				@NotNull String userType) {
			super();
			this.id = id;
			this.userName = userName;
			this.userFirstName = userFirstName;
			this.userLastName = userLastName;
			this.userEmail = userEmail;
			this.userPhone = userPhone;
			this.userCred = userCred;
			this.userIsActive = userIsActive;
			this.userIsLocked = userIsLocked;
			this.userLastSuccessLogin = userLastSuccessLogin;
			this.userLastFailedLogin = userLastFailedLogin;
			this.userNoFailedAttempts = userNoFailedAttempts;
			this.userBlockReleaseTime = userBlockReleaseTime;
			this.userCreatedBy = userCreatedBy;
			this.userCreatedDate = userCreatedDate;
			this.userUpdatedBy = userUpdatedBy;
			this.userUpdatedDate = userUpdatedDate;
			this.isDeleted = isDeleted;
			this.roles = roles;
			this.issuerId = issuerId;
			this.userType = userType;
		}
	    
		public UserEntity()
		{
			
		}

		@Override
		public String toString() {
			return "UserEntity [id=" + id + ", userName=" + userName + ", userFirstName=" + userFirstName
					+ ", userLastName=" + userLastName + ", userEmail=" + userEmail + ", userPhone=" + userPhone
					+ ", userPassword=" + userCred + ", userIsActive=" + userIsActive + ", userIsLocked="
					+ userIsLocked + ", userLastSuccessLogin=" + userLastSuccessLogin + ", userLastFailedLogin="
					+ userLastFailedLogin + ", userNoFailedPassword=" + userNoFailedAttempts + ", userBlockReleaseTime="
					+ userBlockReleaseTime + ", userCreatedBy=" + userCreatedBy + ", userCreatedDate=" + userCreatedDate
					+ ", userUpdatedBy=" + userUpdatedBy + ", userUpdatedDate=" + userUpdatedDate + ", isDeleted="
					+ isDeleted + ", roles=" + roles + ", issuerId=" + issuerId + ", userType=" + userType + "]";
		}
		
		

}
