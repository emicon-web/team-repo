package com.bezkoder.springjwt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Merchant {
	private String merchantid;
	private String name;
	private String displayName;

}
