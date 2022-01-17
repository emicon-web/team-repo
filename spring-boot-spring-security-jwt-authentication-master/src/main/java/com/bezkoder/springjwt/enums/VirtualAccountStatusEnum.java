package com.bezkoder.springjwt.enums;

public enum VirtualAccountStatusEnum {
    ACTIVE,
    CLOSED;

    public static VirtualAccountStatusEnum getVirtualAccountStatus(String key) {
        for (VirtualAccountStatusEnum va : values()) {
            if (va.name().equalsIgnoreCase(key)) {
                return va;
            }
        }
        return null;
    }
}