import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ApprovePayoutService {
  private messageSource = new BehaviorSubject('');
  currentMessage = this.messageSource.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  getInstaPayouts(type, data) {
    return this.http.post(
      `${environment.url}/api/payoutdata/view-instapayout-list?payoutRoleType=${type}`,
      data
    );
  }

  getSelfPayouts(type, data) {
    return this.http.post(
      `${environment.url}/api/payoutdata/view-selfpayout-list?payoutRoleType=${type}`,
      data
    );
  }

  approveInstaPayout(options) {
    const params = new URLSearchParams();
    for (const key in options) {
      params.set(key, options[key]);
    }
    return this.http.get(
      `${environment.url}/api/payoutdata/approve-instapayout?${params}`
    );
  }

  approveApiPayout(options) {
    const params = new URLSearchParams();
    for (const key in options) {
      params.set(key, options[key]);
    }
    return this.http.get(
      `${environment.url}/api/payoutdata/approve-apipayout?${params}`
    );
  }

  approveSelfPayout(options) {
    const params = new URLSearchParams();
    for (const key in options) {
      params.set(key, options[key]);
    }
    return this.http.get(
      `${environment.url}/api/payoutdata/approve-selfpayout?${params}`
    );
  }

  editConfiguration(data) {
    return this.http.put(
      `${environment.url}/api/merchantConfigurations/updateMerchantConfiguration`,
      data
    );
  }
}
