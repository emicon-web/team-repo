package com.bezkoder.springjwt.payload;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayoutFileUploadDto {
    private Long payoutFileUploadId;
    private String merchantid;
    private String accountid;
    private Date uploadDateTime;
    private String status;
    private String createdBy;
    private Date createdDate;
    private String approvedBy;
    private Date approvedDate;
    private BigDecimal successTotalAmount;
    private Integer successRecordCount;
    private BigDecimal failureTotalAmount;
    private Integer failureRecordCount;
    private BigDecimal duplicateTotalAmount;
    private Integer duplicateRecordCount;
    private String fileName;
    private String filePath;
    private String checksum;

    public Long getPayoutFileUploadId() {
        return payoutFileUploadId;
    }

    public void setPayoutFileUploadId(Long payoutFileUploadId) {
        this.payoutFileUploadId = payoutFileUploadId;
    }

    public String getMerchantid() {
        return merchantid;
    }

    public void setMerchantid(String merchantid) {
        this.merchantid = merchantid;
    }

    public String getAccountid() {
        return accountid;
    }

    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }

    public Date getUploadDateTime() {
        return uploadDateTime;
    }

    public void setUploadDateTime(Date uploadDateTime) {
        this.uploadDateTime = uploadDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Date getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }

    public BigDecimal getSuccessTotalAmount() {
        return successTotalAmount;
    }

    public void setSuccessTotalAmount(BigDecimal successTotalAmount) {
        this.successTotalAmount = successTotalAmount;
    }

    public Integer getSuccessRecordCount() {
        return successRecordCount;
    }

    public void setSuccessRecordCount(Integer successRecordCount) {
        this.successRecordCount = successRecordCount;
    }

    public BigDecimal getFailureTotalAmount() {
        return failureTotalAmount;
    }

    public void setFailureTotalAmount(BigDecimal failureTotalAmount) {
        this.failureTotalAmount = failureTotalAmount;
    }

    public Integer getFailureRecordCount() {
        return failureRecordCount;
    }

    public void setFailureRecordCount(Integer failureRecordCount) {
        this.failureRecordCount = failureRecordCount;
    }

    public BigDecimal getDuplicateTotalAmount() {
        return duplicateTotalAmount;
    }

    public void setDuplicateTotalAmount(BigDecimal duplicateTotalAmount) {
        this.duplicateTotalAmount = duplicateTotalAmount;
    }

    public Integer getDuplicateRecordCount() {
        return duplicateRecordCount;
    }

    public void setDuplicateRecordCount(Integer duplicateRecordCount) {
        this.duplicateRecordCount = duplicateRecordCount;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

}
