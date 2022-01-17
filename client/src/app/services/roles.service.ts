import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';

import { throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';

import { NotificationService } from '../services/notification.service';

import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RolesService {

  constructor(private http: HttpClient, private router: Router) { }

  getAllRoles() {
    return this.http.get(`${environment.url}/api/roles/list`);
  }

  getRoleDetails(id) {
    return this.http.get(`${environment.url}/api/roles/${id}`);
  }

  getActionsOnRoles(id) {
    return this.http.get(`${environment.url}/api/roles/action-on-role/${id}`);
  }

  editRole(data) {
    return this.http.post(`${environment.url}/api/roles/${data.id}`, data);
  }

  addRole(data) {
    return this.http.post(`${environment.url}/api/roles/create`, data);
  }

  deleteRole(id) {
    let data = {
      id: id
    }
    return this.http.post(`${environment.url}/api/roles/delete/${id}`, data);
  }
}
