package com.bezkoder.springjwt.enums;

public enum PaymentInstrumentEnum {
    CARD,
    ACCOUNT,
    VPA;

    public static PaymentInstrumentEnum getPaymentInstrument(String key) {
        for (PaymentInstrumentEnum pi : values()) {
            if (pi.name().equalsIgnoreCase(key)) {
                return pi;
            }
        }
        return null;
    }
}