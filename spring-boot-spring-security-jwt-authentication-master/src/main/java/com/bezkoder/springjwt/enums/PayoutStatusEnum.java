package com.bezkoder.springjwt.enums;

public enum PayoutStatusEnum {
    READY,
    SUCCESS,
    FAILURE,
    REJECTED,
    PROCESSED,
    PENDING,
    REVERSED,
    CANCELLED,
    READYFORPROCESSING,
    DUPLICATE;

    public static PayoutStatusEnum getPayoutStatus(String key) {
        for (PayoutStatusEnum ps : values()) {
            if (ps.name().equalsIgnoreCase(key)) {
                return ps;
            }
        }
        return null;
    }
}