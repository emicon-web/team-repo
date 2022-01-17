package com.bezkoder.springjwt.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payout {
	
		private Integer payoutid;
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
		private String payoutPurpose;
		private String payoutStatus; 
		private Date payoutDateTime; 
		private double amount; 
		private String beneficiaryMobileNumber; 
		private String beneficiaryEmailId;
		private String payoutType; 
		private String batchId;
		private String responseDescription; 
		private String acquirerResponseCode;
		private String batchReferenceId;
		private String approvedBy;
		private String canceledBy;
		private Date cancelDate; 
		private Date approveDate;
		private Date createdDate;

}
