package com.bezkoder.springjwt.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualAccountStatement {
	
	private String merchantid;
	private String accountid;
	private String referenceid; 
	private Double creditamount; 
	private Double debitamount;  
	private Double balance; 
	private Date transDateTime; 
	private String batchID;
	private String description;

}
