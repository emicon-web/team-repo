import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs';

import { NotificationService } from '../services/notification.service';
import { PreviousRouteService } from '../services/previous-route.service';

import { RoleAccess } from '../models/role-access';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private messageSource = new BehaviorSubject("");
  currentMessage = this.messageSource.asObservable();

  public userSubject: BehaviorSubject<any>;
  public user: Observable<any>;

  constructor(private http: HttpClient,
    private router: Router) {
    this.userSubject = new BehaviorSubject<any>(JSON.parse(localStorage.getItem('user')));
    this.user = this.userSubject.asObservable();
  }

  // register api
  userRegister(data) {
    return this.http.post(`${environment.url}/api/auth/signup`, data);
  }

  // login api
  userLogin(data) {
    return this.http.post(`${environment.url}/api/auth/signin`, data);
  }

  // logout api
  logout() {
    let name = this.userValue.userName;
    return this.http.get(`${environment.url}/api/auth/signout/${name}`).subscribe((data) => {
      localStorage.removeItem('user');
      this.userSubject.next(null);
      this.router.navigate(['/login']);
    })
    // location.reload(true);
  }

  getRoles() {
    return this.http.get(`${environment.url}/api/roles/list`);
  }

  // get all users
  getAllUsers() {
    return this.http.get(`${environment.url}/api/users/list`);
    // return this.http.get(`${environment.url}/api/users/list?page=${page}&size=${itemsPerPage}`);
  }

  // get specific user details
  getUserDetails(id) {
    return this.http.get(`${environment.url}/api/users/${id}`);
  }

  // delete user
  deleteUser(id) {
    return this.http.post(`${environment.url}/api/users/delete/${id}`, id);
  }

  // edit user
  editUser(id, data) {
    return this.http.post(`${environment.url}/api/users/${id}`, data);
  }

  sendOtpToResetPassword(data) {
    return this.http.post(`${environment.url}/api/auth/forgot-password/?email=${data.userEmail}`, data);
  }

  createNewPassword(data) {
    return this.http.post(`${environment.url}/api/auth/reset-password`, data);
  }

  addRole(data) {
    return this.http.post(`${environment.url}/api/roles/create`, data);
  }

  // Checking Permission
  // check user login with this role have permission or not
  havePermission(access) {
    let token = this.userValue;
    if (token.actions.includes(access)) {
      return true;
    } else {
      return false;
    }
  }

  // private handleError(error: HttpErrorResponse) {
  //   console.log(error);
  //   if (error.error instanceof ErrorEvent) {
  //     // A client-side or network error occurred. Handle it accordingly.
  //     console.error('An error occurred:', error.error.message);
  //   } else {
  //     // The backend returned an unsuccessful response code.
  //     // The response body may contain clues as to what went wrong,
  //     console.error(
  //       `Backend returned code ${error.status}, ` +
  //       `body was: ${error.error}`);
  //   }
  //   // return an observable with a user-facing error message
  //   return throwError(
  //     'Something bad happened; please try again later.');
  // }

  getMessage() {
    return this.http.get(`${environment.url}/api/test/user`);
  }


  public get userValue(): any {
    return this.userSubject.value;
  }
  changeMessage(message: string) {
    this.messageSource.next(message);
  }
  changeToken(token: any) {
    this.userSubject.next(token);
  }

}
