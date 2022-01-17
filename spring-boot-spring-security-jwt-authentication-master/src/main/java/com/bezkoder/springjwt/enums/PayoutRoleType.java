package com.bezkoder.springjwt.enums;

public enum PayoutRoleType {
    MAKER,
    CHECKER;

    public static PayoutRoleType getPayoutRoleType(String key) {
        for (PayoutRoleType prt : values()) {
            if (prt.name().equalsIgnoreCase(key)) {
                return prt;
            }
        }
        return null;
    }
}