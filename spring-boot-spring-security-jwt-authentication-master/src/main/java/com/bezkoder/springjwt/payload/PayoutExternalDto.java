package com.bezkoder.springjwt.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayoutExternalDto {

    private Long payoutId;
    private String merchantId;
    private String payoutFileUploadId;
    private String merchantPayoutId;
    private String virtualAccNumber;
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
    private String payoutPurpose;
    private Date payoutDateTime;
    private String amount;
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
