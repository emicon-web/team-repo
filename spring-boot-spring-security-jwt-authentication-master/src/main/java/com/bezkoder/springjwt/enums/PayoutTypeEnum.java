package com.bezkoder.springjwt.enums;

public enum PayoutTypeEnum {
    FILEUPLOAD,
    INSTAPAY,
    SELFPAY,
    API;

    public static PayoutTypeEnum getPayoutType(String key) {
        for (PayoutTypeEnum pt : values()) {
            if (pt.name().equalsIgnoreCase(key)) {
                return pt;
            }
        }
        return null;
    }
}