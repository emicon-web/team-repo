package com.bezkoder.springjwt.models;

import com.bezkoder.springjwt.enums.PartnerTypeEnum;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "userName"),
                @UniqueConstraint(columnNames = "userEmail")
        })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 33)
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
    private String  userCred;


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

    @Column(name = "merchantid")
    private String merchantId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PartnerTypeEnum userType;

    private Date userLastLogoutTime;

    public User() {
        super();
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
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
