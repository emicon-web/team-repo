package com.bezkoder.springjwt.payload;

import java.util.Collection;
import java.util.Date;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleDto {

	private Integer id;

	@NotBlank
	private String name;
	private String description;
	private Collection<ActionDto> actions;
	private String roleCreatedBy;
	private Date roleCreatedTime;
	private String roleUpdatedBy;
	private Date roleUpdatedTime;
	private Boolean isDeleted;
	private String merchantId;

	public RoleDto() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<ActionDto> getActions() {
		return actions;
	}

	public void setActions(Collection<ActionDto> actions) {
		this.actions = actions;
	}

	public String getRoleCreatedBy() {
		return roleCreatedBy;
	}

	public void setRoleCreatedBy(String roleCreatedBy) {
		this.roleCreatedBy = roleCreatedBy;
	}

	public Date getRoleCreatedTime() {
		return roleCreatedTime;
	}

	public void setRoleCreatedTime(Date roleCreatedTime) {
		this.roleCreatedTime = roleCreatedTime;
	}

	public String getRoleUpdatedBy() {
		return roleUpdatedBy;
	}

	public void setRoleUpdatedBy(String roleUpdatedBy) {
		this.roleUpdatedBy = roleUpdatedBy;
	}

	public Date getRoleUpdatedTime() {
		return roleUpdatedTime;
	}

	public void setRoleUpdatedTime(Date roleUpdatedTime) {
		this.roleUpdatedTime = roleUpdatedTime;
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
}
