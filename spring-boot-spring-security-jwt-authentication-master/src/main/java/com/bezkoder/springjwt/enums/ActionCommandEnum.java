package com.bezkoder.springjwt.enums;

public enum ActionCommandEnum {
    Create,
    Read,
    Update,
    Delete;
	
	public static ActionCommandEnum getActionCommandByValue(String key) {
		for (ActionCommandEnum ace : values()) {
			if (ace.name().equalsIgnoreCase(key)) {
				return ace;
			}
		}
		return null;
	}
}