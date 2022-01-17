package com.bezkoder.springjwt.enums;

public enum UserType {
    SYSTEM;

    public static UserType getUserType(String key) {
        for (UserType ut : values()) {
            if (ut.name().equalsIgnoreCase(key)) {
                return ut;
            }
        }
        return null;
    }
}