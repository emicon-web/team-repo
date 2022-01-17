package com.bezkoder.springjwt.enums;

public enum ActionEnum {

	ADD_USER(1, "Add User"),
	UPDATE_USER(2, "Update Moderator"),
	READ_USER(3, "Read User"),
	DELETE_USER(4, "Delete Moderator"),
	USER_DETAIL(5, "User Detail"),
	ADD_ROLE(6,"Add Role"),
	ROLE_DETAIL(7,"Role Detail"),
	UPDATE_ROLE(8,"Update Role"),
	DELETE_ROLE(9,"Delete Role"),
	READ_ROLE(10,"Read Role"),
	GET_VIRTUAL_ACCOUNTS(11,"Get Virtual Account"),
	VIEW_VIRTUAL_ACCOUNTS(12,"View Virtual Account"),
	ADD_VIRTUAL_ACCOUNT(13,"Add virtual Account"),
	EDIT_VIRTUAL_ACCOUNT(14,"Edit Virtual Account"),
	VIEW_MERCHANT_CONFIGURATIONS(15,"View Merchant Configurations"),
	EDIT_MERCHANT_CONFIGURATIONS(16,"Edit Merchant Configurations"),
	VIEW_FILE_UPLOAD(17,"View File Upload"),
	VIEW_CANCEL_PAYOUTS(18,"View Cancel Payouts"),
	PENDING_APPROVAL(19,"PENDING_APPROVAL"),
	PENDING_APPROVAL_FILE(20,"PENDING_APPROVAL_FILE"),
	INSTA_PAYOUTS(21,"INSTA_PAYOUTS"),
	ACCOUNT_CLOSURE(22,"ACCOUNT_CLOSURE"),
	CANCEL_PAYOUT_BUTTON(23,"CANCEL_PAYOUT_BUTTON"),
	VIEW_PAYOUT_ACCOUNT_HISTORY(24,"VIEW_PAYOUT_ACCOUNT_HISTORY"),
	CANCEL_SELECTED_PAYOUT(25,"CANCEL_SELECTED_PAYOUT"),
	VIEW_ACCOUNT_STATEMENT(26,"VIEW_ACCOUNT_STATEMENT"),
	ADD_INSTA_PAYOUT(27,"ADD_INSTA_PAYOUT"),
	VIEW_REPORTS(28,"VIEW_REPORTS"),
	ACTION_PENDING_APPROVAL_FILE(29,"ACTION_PENDING_APPROVAL_FILE"),
	ACTION_INSTA_PAYOUTS(30,"ACTION_INSTA_PAYOUTS"),
	API_PAYOUT(31,"API_PAYOUT"),
	ACTION_API_PAYOUT(32,"ACTION_API_PAYOUT"),
	ACTION_ACCOUNT_CLOSURE(33,"ACTION_ACCOUNT_CLOSURE"),
	ACTION_CANCEL_PAYOUT(34,"ACTION_CANCEL_PAYOUT");


	private int id;
	private String description;

	private ActionEnum(int id, String description) {
		this.id = id;
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public static ActionEnum getById(int id) {
		for(ActionEnum actionEnum : values()) {
			if(actionEnum.getId() == id) {
				return actionEnum;
			}
		}
		return null;
	}

	public static ActionEnum getByDescription(String description) {
		for(ActionEnum actionEnum : ActionEnum.values()) {
			if(actionEnum.getDescription().equals(description)) {
				return actionEnum;
			}
		}
		return null;
	}
}
