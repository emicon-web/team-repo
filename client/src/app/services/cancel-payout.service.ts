import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CancelPayoutService {
  private messageSource = new BehaviorSubject('');
  currentMessage = this.messageSource.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  getAllReadyTxns(id, limit, offset) {
    return this.http.post(
      `${environment.url}/api/payout/readyForProcessList?limit=${limit}&offset=${offset}`,
      id
    );
  }

  getTxns(path, data) {
    return this.http.post(`${environment.url}/api/payout` + path, data);
  }

  cancelPayout(path, data) {
    return this.http.post(`${environment.url}/api/payout/` + path, data);
  }

  getAllMarkForCancel(data, itemPerPage, offset) {
    return this.http.post(
      `${environment.url}/api/payout/markForCancleList?limit=${itemPerPage}&offset=${offset}`,
      data
    );
  }
}
