package com.bezkoder.springjwt.payload;

import com.bezkoder.springjwt.enums.PartnerTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {

    private Long id;

    @NotBlank
    @Size(min = 3, max = 33)
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

    @Column(name = "user_password")
    private String userCred;

    private Boolean userIsActive;

    private Boolean userIsLocked;

    private Date userLastSuccessLogin;

    private Date userLastFailedLogin;

    @Column(name = "user_no_failed_password")
    private Integer userNoFailedAttempts;

    private Date userBlockReleaseTime;

    private String userResetPassCode;

    private String userCreatedBy;

    private Date userCreatedDate;

    private String userUpdatedBy;

    private Date userUpdatedDate;

    private Collection<RoleDto> roles;

    private String merchantId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PartnerTypeEnum userType;

    private Date userLastLogoutTime;

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

    public String getUserResetPassCode() {
        return userResetPassCode;
    }

    public void setUserResetPassCode(String userResetPassCode) {
        this.userResetPassCode = userResetPassCode;
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

    public Collection<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(Collection<RoleDto> roles) {
        this.roles = roles;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public PartnerTypeEnum getUserType() {
        return userType;
    }

    public void setUserType(PartnerTypeEnum userType) {
        this.userType = userType;
    }

    public Date getUserLastLogoutTime() {
        return userLastLogoutTime;
    }

    public void setUserLastLogoutTime(Date userLastLogoutTime) {
        this.userLastLogoutTime = userLastLogoutTime;
    }
}
