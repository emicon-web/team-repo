import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class VirtualAccountService {
  private messageSource = new BehaviorSubject('');
  currentMessage = this.messageSource.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  getRoles() {
    return this.http.get(`${environment.url}/api/roles/list`);
  }

  getAllVirtualAccounts(type = '') {
    return this.http.get(
      `${environment.url}/api/virtualaccounts/list?status=` + type
    );
  }

  getPayoutInstruments(data) {
    return this.http.post(
      `${environment.url}/api/merchantConfigurations/merConfigurationPaymentInstrument`,
      data
    );
  }

  getPayoutModees(data) {
    return this.http.post(
      `${environment.url}/api/merchantConfigurations/merConfigurationPaymentModeList`,
      data
    );
  }

  getmerchantPayoutId() {
    return this.http.get(
      `${environment.url}/api/payoutdata/getAutoMerchantpayout`
    );
  }

  getAllClosedPA(data) {
    return this.http.post(
      `${environment.url}/api/virtualaccounts/pendingClouserlist`,
      data
    );
  }

  closePA(data) {
    return this.http.post(
      `${environment.url}/api/virtualaccounts/accountClouser`,
      data
    );
  }

  approvePA(data, param) {
    console.log(param);
    return this.http.post(
      `${environment.url}/api/virtualaccounts/approvedClouser?action=${param}`,
      data
    );
  }

  createPayoutAccount(data) {
    return this.http.post(
      `${environment.url}/api/virtualaccounts/create`,
      data
    );
  }

  createInstaPayout(data) {
    return this.http.post(
      `${environment.url}/api/payoutdata/add-insta-payout`,
      data
    );
  }

  createSelfPayout(data) {
    return this.http.post(
      `${environment.url}/api/payoutdata/add-self-payout`,
      data
    );
  }

  getPADetails(id) {
    return this.http.get(`${environment.url}/api/virtualaccounts/${id}`);
  }

  getPendingCount(data) {
    return this.http.post(
      `${environment.url}/api/payout/getPendingCount`,
      data
    );
  }

  getDashboardBalance(data) {
    return this.http.get(
      `${environment.url}/api/payoutdata/payout-balance-data?merchantId=${data.merchantId}&accountId=${data.accountId}&lastNDays=${data.lastNDays}&type=${data.type}`
    );
  }

  getTransactionalData(data, limit, offset) {
    return this.http.post(
      `${environment.url}/api/payout/transactionReport?limit=${limit}&offset=${offset}`,
      data
    );
  }

  accountStatement(id, day) {
    return this.http.post(
      `${environment.url}/api/virtualaccounts_statements/getVirtualAccountStatement/${id}/${day}`,
      {}
    );
  }

  getIfscCode(data) {
    return this.http.post(`${environment.url}/api/bank/getBankName`, data);
  }

  getAllStatuses() {
    return this.http.get(`${environment.url}/api/payout/getPayoutStatusList`);
  }

  getDefaultRecords(data, limit, offset) {
    return this.http.post(
      `${environment.url}/api/payout/defaultTransactionReport?limit=${limit}&offset=${offset}`,
      data
    );
  }
}
