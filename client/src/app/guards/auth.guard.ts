import { Injectable } from '@angular/core';
import {
  CanActivate,
  CanActivateChild,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  UrlTree,
  Router,
} from '@angular/router';
import { Observable } from 'rxjs';
import { JwtHelperService } from '@auth0/angular-jwt';
import { UserService } from '../services/user.service';
import { RoleAccess } from '../models/role-access';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate, CanActivateChild {
  constructor(private router: Router, private userService: UserService) { }
  helper = new JwtHelperService();
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    let token = this.userService.userValue;
    let validity = this.helper.isTokenExpired(token?.token);
    if (token != null && validity == false) {
      return true;
    } else {
      localStorage.removeItem('user');
      this.userService.userSubject.next(null);
      this.router.navigate(['/login']);
      return false;
    }
  }

  canActivateChild(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    let token = this.userService.userValue;
    const decodedToken = this.helper.decodeToken(token.token);
    let validity = this.helper.isTokenExpired(token.token);
    if (token != null && validity == false) {
      if (
        (route.data.roles && token.actions.includes(route.data.roles[0])) ||
        token.actions.includes(route.data.roles[1])
      ) {
        return true;
      } else {
        this.router.navigate(['/dashboard']);
        return false;
      }
    } else {
      localStorage.removeItem('user');
      this.userService.userSubject.next(null);
      this.router.navigate(['/login']);
      return false;
    }
  }
}
