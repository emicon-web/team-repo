export class configurationModel {
  'merchantid': string;
  'paymentMode': any;
  'instruments': any;
  'acquirerBankName': string;
  'acquirerBankIFSC': string;
  'minPayoutAmount': number;
  'maxPayoutAmount': number;
  'lowBalanceThreshold': number;
  'updateDate': number;
  'updatedBy': string;
  'enableInstaPayChecker': boolean;
  'enableSelfPayChecker': boolean;
  'enableCancelPayChecker': boolean;
  'enableCloseAcctChecker': boolean;
  'enableAPIPayChecker': boolean;
}
