package com.bezkoder.springjwt.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor
public class ProcessCountPayload {
	
	private String merchantid;
	private String status;
	private String payoutType;
	private String payoutStatus;
	

}
