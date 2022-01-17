package com.bezkoder.springjwt.payload;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayoutStagingDto {

    private Long payoutid;
    private String merchantid;
    private String payoutFileUploadId;
    private String merchantPayoutId;
    private String accountId;
    private String beneficiaryName;
    private String payoutPaymentInstrument;
    private String payoutPaymentMode;
    private String bankAccountName;
    private String beneficiaryAccountNumber;
	private String beneficiaryIFSCCode; 
    private String beneficiaryCardNumber;
    private String cardHolderName;
    private String cardHolderBankName;
    private String cardHolderBankIFSCCode;
    private String beneficiaryVPA;
    private String payoutStatus;
    private String payoutStatusFailureReason;
    private String payoutPurpose;
    private Date payoutDateTime;
    private double amount;
    private String payoutDateTimeStr;
    private String amountStr;
    private String beneficiaryMobileNumber;
    private String beneficiaryEmailId;
    private String payoutType;
    private String createdBy;
    private Date createdDate;
    private String approvedBy;
    private Date approvedDate;
    private String canceledBy;
    private Date cancelDate;

   
}
