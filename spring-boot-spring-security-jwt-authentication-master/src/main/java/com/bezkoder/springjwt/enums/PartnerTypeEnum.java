package com.bezkoder.springjwt.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PartnerTypeEnum {

    MERCHANT("10", "Merchant"),
    AGGREGATOR("20", "Aggregator"),
    SYSTEM("30", "System");

    private final String id;
    private final String description;

    PartnerTypeEnum(String id, String description) {
        this.id = id;
        this.description = description;
    }

//    @JsonValue
    public String getId() {
        return id;
    }

    @JsonValue
    public String getName() {
        return this.name();
    }

    public String getDescription() {
        return description;
    }

    public static PartnerTypeEnum getById(String id) {
        for (PartnerTypeEnum userType : values()) {
            if (userType.getId().equals(id)) {
                return userType;
            }
        }
        return null;
    }

    public static PartnerTypeEnum getByDescription(String description) {
        for (PartnerTypeEnum userType : PartnerTypeEnum.values()) {
            if (userType.getDescription().equals(description)) {
                return userType;
            }
        }
        return null;
    }
}
