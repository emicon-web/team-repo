package com.bezkoder.springjwt.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NegativeOrZero;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayoutFileUpload {
	
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
    private Integer totalCount;
    private BigDecimal totalAmount;
    private String fileName;
    private String filePath;
    private String checksum;

}
