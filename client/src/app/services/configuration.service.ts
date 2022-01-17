import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ConfigurationService {
  private messageSource = new BehaviorSubject('');
  currentMessage = this.messageSource.asObservable();

  constructor(private http: HttpClient, private router: Router) {}
  getDetails(id) {
    return this.http.post(
      `${environment.url}/api/merchantConfigurations/merConfigurationlist`,
      id
    );
  }

  editConfiguration(data) {
    return this.http.put(
      `${environment.url}/api/merchantConfigurations/updateMerchantConfiguration`,
      data
    );
  }
}
