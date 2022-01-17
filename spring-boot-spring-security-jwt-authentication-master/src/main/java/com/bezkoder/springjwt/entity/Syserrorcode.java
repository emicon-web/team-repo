package com.bezkoder.springjwt.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

//@Entity
//@Table(name ="syserrorcode")
public class Syserrorcode {
	
	@Id
	String sysID; 
	
	
	@Column(name = "errorCode")
	String errorCode;
	
	@Column(name = "mappedErrorCode")
	String mappedErrorCode; 
	
	@Column(name = "createdAt")
	Date createdAt; 
	
	@Column(name = "updatedAt")
	Date updatedAt; 
	
	@Column(name = "createdBy")
	String createdBy;
	
	@Column(name = "lastUpdatedBy")
	String lastUpdatedBy;

	public String getSysID() {
		return sysID;
	}

	public void setSysID(String sysID) {
		this.sysID = sysID;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getMappedErrorCode() {
		return mappedErrorCode;
	}

	public void setMappedErrorCode(String mappedErrorCode) {
		this.mappedErrorCode = mappedErrorCode;
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

	public Syserrorcode(String sysID, String errorCode, String mappedErrorCode, Date createdAt, Date updatedAt,
			String createdBy, String lastUpdatedBy) {
		super();
		this.sysID = sysID;
		this.errorCode = errorCode;
		this.mappedErrorCode = mappedErrorCode;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.createdBy = createdBy;
		this.lastUpdatedBy = lastUpdatedBy;
	}
	
	public Syserrorcode()
	{
		
	}

	@Override
	public String toString() {
		return "Syserrorcode [sysID=" + sysID + ", errorCode=" + errorCode + ", mappedErrorCode=" + mappedErrorCode
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", createdBy=" + createdBy
				+ ", lastUpdatedBy=" + lastUpdatedBy + "]";
	}
	
	
	

}
