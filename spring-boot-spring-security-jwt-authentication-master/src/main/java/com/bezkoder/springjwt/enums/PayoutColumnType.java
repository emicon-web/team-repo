package com.bezkoder.springjwt.enums;

public enum PayoutColumnType {
    PAYOUT_STATUS,
    PAYOUT_PAYMENT_MODE;

    public static PayoutColumnType getPayoutColumnType(String key) {
        for (PayoutColumnType pct : values()) {
            if (pct.name().equalsIgnoreCase(key)) {
                return pct;
            }
        }
        return null;
    }
}