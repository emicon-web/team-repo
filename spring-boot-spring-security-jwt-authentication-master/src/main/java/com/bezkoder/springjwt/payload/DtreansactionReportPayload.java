package com.bezkoder.springjwt.payload;

import java.util.Date;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtreansactionReportPayload {
	
	
	@NotNull
	private String payoutStatus; 

	@NotNull
    private Date fromDate;

	@NotNull
    private Date toDate;

}
