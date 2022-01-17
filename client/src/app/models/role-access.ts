export enum RoleAccess {
  // USER MANAGEMENT STARTS
  navDashboard = 'MAIN_DASHBOARD',
  navUsers = 'NAVBAR_USER',
  navRoles = 'NAVBAR_ROLES',
  viewUsers = 'READ_USER',
  editUser = 'UPDATE_USER',
  addUser = 'ADD_USER',
  viewUserDetails = 'USER_DETAIL',
  viewRoleDetails = 'ROLE_DETAIL',
  deleteUser = 'DELETE_USER',
  viewRoles = 'READ_ROLE',
  addRoles = 'ADD_ROLE',
  editRoles = 'UPDATE_ROLE',
  deleteRoles = 'DELETE_ROLE',
  // USER MANAGEMENT ENDS
  // ACCOUNT MANAGEMENT ACTIONS START
  getVirtualAccounts = 'GET_VIRTUAL_ACCOUNTS',
  viewVirtualAccounts = 'VIEW_VIRTUAL_ACCOUNTS',
  addVirtualAccount = 'ADD_VIRTUAL_ACCOUNT',
  editVirtualAccount = 'EDIT_VIRTUAL_ACCOUNT',
  // ACCOUNT MANAGEMENT ACTIONS END
  // MERCHANT CONFIGURATION START
  viewMerchantConfigurations = 'VIEW_MERCHANT_CONFIGURATIONS',
  editMerchantConfigurations = 'EDIT_MERCHANT_CONFIGURATIONS',
  // EDIT MERCHANT CONFIGURATION END
  // ACCOUNT OPERATION STARTS
  // FILE UPLOAD START
  viewFileUpload = 'VIEW_FILE_UPLOAD',
  viewPayoutAccountHistory = 'VIEW_PAYOUT_ACCOUNT_HISTORY',
  // FILE UPLOAD END
  // CANCEL PAYOUT STARTS
  viewCancelPayouts = 'VIEW_CANCEL_PAYOUTS',
  cancelSelcetedPayouts = 'CANCEL_SELECTED_PAYOUT',
  // CANCEL PAYOUT ENDS
  // ACCOUNT STATEMENT STARTS
  viewAccountStatement = 'VIEW_ACCOUNT_STATEMENT',
  // ACCOUNT STATEMENT ENDS
  // ADD INSTA PAYOUT STARTS
  addInstaPayout = 'ADD_INSTA_PAYOUT',
  // ADD INSTA PAYOUT ENDS
  // ACCOUNT OPERATION ENDS
  // REPORT START
  viewReports = 'VIEW_REPORTS',
  // REPORT ENDS
  // DASHBOARD RIGHT HAND SIDE BUTTON STARTS
  viewPendingApproval = 'PENDING_APPROVAL',
  viewPendinggApprovalFile = 'PENDING_APPROVAL_FILE',
  actionPendinggApprovalFile = 'ACTION_PENDING_APPROVAL_FILE',
  viewInstaPayout = 'INSTA_PAYOUTS',
  actionInstaPayout = 'ACTION_INSTA_PAYOUTS',
  viewApiPayout = 'API_PAYOUT',
  actionApiPayout = 'ACTION_API_PAYOUT',
  viewAccountClosure = 'ACCOUNT_CLOSURE',
  actionAccountClosure = 'ACTION_ACCOUNT_CLOSURE',
  viewCancelPayoutButton = 'CANCEL_PAYOUT_BUTTON',
  actionCancelPayout = 'ACTION_CANCEL_PAYOUT'
  // DASHBOARD RIGHT HAND SIDE BUTTON ENDS
}
