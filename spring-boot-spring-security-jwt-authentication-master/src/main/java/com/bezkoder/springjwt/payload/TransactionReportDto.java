package com.bezkoder.springjwt.payload;

import java.util.Date;
import java.util.List;

import com.bezkoder.springjwt.entity.Payout;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionReportDto {
	
	    
	    //private Long transactionDataCount;
	    
	    List<Payout> transactionData;

}
