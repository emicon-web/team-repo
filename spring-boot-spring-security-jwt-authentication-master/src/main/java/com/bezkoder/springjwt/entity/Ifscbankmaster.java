package com.bezkoder.springjwt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ifscbankmaster {
	
	private String ifsccode; 
	private String bankid; 
	private String bankname; 
	private String ifscprefix;

}
