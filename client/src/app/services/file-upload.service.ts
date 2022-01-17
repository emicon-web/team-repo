import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class FileUploadService {
  private messageSource = new BehaviorSubject('');
  currentMessage = this.messageSource.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  uploadFile(data) {
    return this.http.post(
      `${environment.url}/api/payoutdata/upload-payout-file`,
      data
    );
  }

  viewPayoutFiles(type) {
    return this.http.get(
      `${environment.url}/api/payoutdata/view-payout-files?payoutRoleType=${type}`
    );
  }

  getFileUrl(options) {
    const params = new URLSearchParams();
    for (const key in options) {
      params.set(key, options[key]);
    }
    return this.http.get(
      `${environment.url}/api/payoutdata/download-payoutfile?${params}`,
      { responseType: 'blob' }
    );
  }

  approveFile(data) {
    return this.http.post<any>(
      `${environment.url}/api/payoutdata/update-payout-files-status`,
      data
    );
  }

  editConfiguration(data) {
    return this.http.put(
      `${environment.url}/api/merchantConfigurations/updateMerchantConfiguration`,
      data
    );
  }

  getFileHistory(data,type,days) {
    return this.http.post(
      `${environment.url}/api/payoutdata/history-payout-files?payoutRoleType=${type}&days=${days}`,
      data
    );
  }
}
