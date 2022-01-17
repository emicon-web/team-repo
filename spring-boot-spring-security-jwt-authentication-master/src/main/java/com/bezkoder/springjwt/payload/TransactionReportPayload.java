package com.bezkoder.springjwt.payload;

import java.util.Date;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionReportPayload {
	
	private Integer payoutid;

	private String merchantPayoutId;
	
	@NotNull
	private String accountId; 
	
	private String payoutStatus; 

	private String payoutPaymentInstrument;
	
	private String payoutPaymentMode;
	
	private String batchId;

	@NotNull
    private Date fromDate;

	@NotNull
    private Date toDate;
	

}
