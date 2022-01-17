package com.bezkoder.springjwt.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

public class Sysparams {
	
	@Id
	@Column
	String sysType;
	
	@Column
	String sysKey; 
	
	@Column
	String sysValue; 
	
	@Column
	String description; 
	
	@Column
	String addProp;
	
	@Column
	String createdBy; 
	
	@Column
	String lastUpdatedBy; 
	
	@Column
	Date createdAt;
	
	@Column
	Date updatedAt;
	

	public String getSysType() {
		return sysType;
	}

	public void setSysType(String sysType) {
		this.sysType = sysType;
	}

	public String getSysKey() {
		return sysKey;
	}

	public void setSysKey(String sysKey) {
		this.sysKey = sysKey;
	}

	public String getSysValue() {
		return sysValue;
	}

	public void setSysValue(String sysValue) {
		this.sysValue = sysValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAddProp() {
		return addProp;
	}

	public void setAddProp(String addProp) {
		this.addProp = addProp;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Sysparams(String sysType, String sysKey, String sysValue, String description, String addProp,
			String createdBy, String lastUpdatedBy, Date createdAt, Date updatedAt) {
		super();
		this.sysType = sysType;
		this.sysKey = sysKey;
		this.sysValue = sysValue;
		this.description = description;
		this.addProp = addProp;
		this.createdBy = createdBy;
		this.lastUpdatedBy = lastUpdatedBy;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
	public Sysparams()
	{
		
	}

	@Override
	public String toString() {
		return "Sysparams [sysType=" + sysType + ", sysKey=" + sysKey + ", sysValue=" + sysValue + ", description="
				+ description + ", addProp=" + addProp + ", createdBy=" + createdBy + ", lastUpdatedBy=" + lastUpdatedBy
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
	

}
