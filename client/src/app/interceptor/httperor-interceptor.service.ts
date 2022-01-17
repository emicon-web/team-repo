import { HttpErrorResponse, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { UserService } from '../services/user.service';
import { NotificationService } from '../services/notification.service';

@Injectable({
  providedIn: 'root'
})
export class HttpErrorInterceptorService implements HttpInterceptor {

  constructor(private notificationService: NotificationService,
    private user: UserService) { }
  intercept(request: HttpRequest<any>, next: HttpHandler) {
    return next.handle(request)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          const errorMessage = this.setError(error);
          if (errorMessage != undefined && errorMessage != null) {
            this.notificationService.showError(errorMessage, '');
          }
          if (error.status === 401) {
            this.user.logout();
            location.reload(true);
          }
          return throwError(errorMessage);
        })
      );
  }

  setError(error: HttpErrorResponse): string {
    let errorMessage = 'Unknown error occured';
    if (error.error instanceof ErrorEvent) {
      // Client side error
      errorMessage = error.error.message;
    } else {
      // server side error
      if (error.status !== 0) {
        errorMessage = error.error.message;
      }
    }
    return errorMessage;
  }
}
