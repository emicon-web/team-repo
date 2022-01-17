package com.bezkoder.springjwt.errors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ValidationException extends RuntimeException {

	String message;
	Object value;
	Date timestamp;
}
