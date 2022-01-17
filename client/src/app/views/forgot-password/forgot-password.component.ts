import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, Validators, FormArray } from '@angular/forms';
import { Router } from '@angular/router';

import { UserService } from '../../services/user.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit {

  forgotPasswordForm: FormGroup;
  formSubmitted = false;

  showError = false;
  errorTitle = "";
  showSuccess = false;
  successMessage = "";

  constructor(private formBuilder: FormBuilder,
    private userService: UserService,
    private router: Router,
    private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.createForm();
  }

  // Getter for easy access to form fields
  get f() { return this.forgotPasswordForm.controls; }

  createForm() {
    this.forgotPasswordForm = this.formBuilder.group({
      userEmail: ['', [Validators.required, Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$")]],
    });
  }

  forgotPassword() {
    this.showError = false;
    this.formSubmitted = true;

    if (this.forgotPasswordForm.invalid) { return; }
    this.userService.sendOtpToResetPassword(this.forgotPasswordForm.value).subscribe(
      (data: any) => {
        this.notificationService.showSuccess(data.message, "");
        // this.showSuccess = true;
        // this.successMessage = data.message;
        this.router.navigate([`/login`]);
      }, error => {
        this.showError = true;
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
