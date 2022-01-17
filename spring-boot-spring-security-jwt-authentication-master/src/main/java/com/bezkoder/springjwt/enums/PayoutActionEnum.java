package com.bezkoder.springjwt.enums;

public enum PayoutActionEnum {
    APPROVE,
    REJECT;

    public static PayoutActionEnum getPayoutAction(String key) {
        for (PayoutActionEnum pa : values()) {
            if (pa.name().equalsIgnoreCase(key)) {
                return pa;
            }
        }
        return null;
    }
}