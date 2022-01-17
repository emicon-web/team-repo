package com.bezkoder.springjwt.enums;

public enum PayoutFileUploadStatusEnum {
    PENDING,
    APPROVED,
    REJECTED,
    CKSUMFAILED,
    UPLOADING,
    BLANKFILE;

    public static PayoutFileUploadStatusEnum getPayoutFileUploadStatus(String key) {
        for (PayoutFileUploadStatusEnum pfus : values()) {
            if (pfus.name().equalsIgnoreCase(key)) {
                return pfus;
            }
        }
        return null;
    }
}