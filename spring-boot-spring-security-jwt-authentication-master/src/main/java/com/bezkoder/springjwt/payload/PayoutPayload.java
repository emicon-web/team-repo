package com.bezkoder.springjwt.payload;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayoutPayload {

    public List<String> merchantPayoutId;
	private String merchantid; 
	private String payoutStatus; 
	private String canceledBy;
	private Date cancelDate; 



}
