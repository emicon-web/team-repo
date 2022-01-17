package com.bezkoder.springjwt.enums;

public enum ModeOfPaymentEnum {
    IMPS,
    NEFT,
    UPI,
	RTGS;
	
	public static ModeOfPaymentEnum getModeOfPayment(String key) {
		for (ModeOfPaymentEnum mop : values()) {
			if (mop.name().equalsIgnoreCase(key)) {
				return mop;
			}
		}
		return null;
	}
}