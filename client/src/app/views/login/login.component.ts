import { Component, OnInit } from '@angular/core';
import {
  FormGroup,
  FormControl,
  FormBuilder,
  Validators,
  FormArray,
} from '@angular/forms';
import { Router } from '@angular/router';
import * as shajs from 'sha.js';

import { UserService } from '../../services/user.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styles: [],
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  formSubmitted = false;
  showError = false;
  errorTitle;

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private router: Router,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    if (this.userService.userValue != null) {
      this.userService.logout();
      // this.router.navigate([`/dashboard`]);
    }
    this.createForm();
  }

  // Getter for easy access to form fields
  get f() {
    return this.loginForm.controls;
  }

  createForm() {
    this.loginForm = this.formBuilder.group({
      userName: ['', Validators.required],
      userCred: ['', [Validators.required]],
    });
  }

  loginUser() {
    this.formSubmitted = true;
    if (this.loginForm.invalid) {
      return;
    }
    this.loginForm.value.userCred = shajs('sha256')
      .update(this.loginForm.value.userCred)
      .digest('hex');
    this.userService.userLogin(this.loginForm.value).subscribe(
      (data: any) => {
        this.notificationService.showSuccess('Login Successfully', '');
        localStorage.setItem('user', JSON.stringify(data));
        this.userService.changeToken(data);
        this.router.navigate([`/dashboard`]);
      },
      (error) => {
        // this.showError = true;
        if (error.error instanceof ErrorEvent) {
          // Client side error
          this.errorTitle = error.error.message;
        } else {
          // server side error
          if (error.status !== 0) {
            this.errorTitle = error.error.message;
          }
        }
      }
    );
  }
}
