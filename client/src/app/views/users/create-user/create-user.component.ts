import { Component, OnInit } from '@angular/core';
import {
  FormGroup,
  FormControl,
  FormBuilder,
  Validators,
  FormArray,
} from '@angular/forms';
import { Router } from '@angular/router';

import { UserService } from '../../../services/user.service';
import { NotificationService } from '../../../services/notification.service';

import { Role } from '../../../models/role';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-create-user',
  templateUrl: './create-user.component.html',
  styleUrls: ['./create-user.component.scss'],
})
export class CreateUserComponent implements OnInit {
  registerForm: FormGroup;
  formSubmitted = false;
  showPassword: boolean = false;
  role = [];
  categories = [];
  selectedRoles = [];

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private router: Router,
    private notificationService: NotificationService,
    private spinner: NgxSpinnerService
  ) { }
  loaded = false;
  ngOnInit(): void {
    this.selectRoles();
  }

  // roles;
  selectRoles() {
    this.userService.getRoles().subscribe((data: any) => {
      this.role = data;
      for (let i = 0; i < this.role.length; i++) {
        this.categories.push({ id: this.role[i].id, name: this.role[i].name });
        if (i == this.role.length - 1) {
          this.createForm();
          this.loaded = true;
        }
      }
    });
  }
  shortCode;

  // Getter for easy access to form fields
  get f() {
    return this.registerForm.controls;
  }

  createForm() {
    this.shortCode = this.userService.userValue.shortCode;
    this.registerForm = this.formBuilder.group(
      {
        userName: [
          '',
          [
            Validators.required,
            Validators.minLength(3),
            Validators.maxLength(27),
          ],
        ],
        userFirstName: [
          '',
          [
            Validators.required,
            Validators.minLength(3),
            Validators.maxLength(20),
          ],
        ],
        userLastName: [
          '',
          [
            Validators.required,
            Validators.minLength(3),
            Validators.maxLength(20),
          ],
        ],
        userEmail: [
          '',
          [
            Validators.required,
            Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$'),
          ],
        ],
        userPhone: [''],
        userIsActive: [true, Validators.required],
        userIsLocked: [false, Validators.required],
        userCreatedBy: [this.userService.userValue.id, Validators.required],
        userType: ['MERCHANT'],
        merchantId: [this.userService.userValue.merchantId],
        // userUpdatedBy: ['rakshit', Validators.required],
        // userCreatedDate: ['1625034783'],
        // userUpdatedDate: ['1625034783'],
        // userLastSuccessLogin: ["1625034783"],
        // userLastFailedLogin: ["1625034783"],
        // userNoFailedAttempts: 0,
        // userBlockReleaseTime: ["1625034783"],
        // userResetPassCode: ["hfg54654"],
        // userCred: ['', [Validators.required, Validators.pattern('(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[$@$!%*?&])[A-Za-z\d$@$!%*?&].{8,}')]],
        // confirmPassword: ['', [Validators.required]],
        roles: [[], [Validators.required]],
      }
      // , {
      //   validator: this.MustMatch('userCred', 'confirmPassword')
      // }
    );
  }

  // MustMatch(password1: string, password2: string) {
  //   return (formGroup: FormGroup) => {
  //     const pass = formGroup.controls[password1];
  //     const confirmPass = formGroup.controls[password2];
  //     if (confirmPass.errors && !confirmPass.errors.mustMatch) {
  //       return;
  //     }

  //     if (pass.value !== confirmPass.value) {
  //       confirmPass.setErrors({ mustMatch: true });
  //     } else {
  //       confirmPass.setErrors(null);
  //     }
  //   }
  // }

  registerUser() {
    this.spinner.show();
    let temp = [];
    // if (this.registerForm.value.userPhone != '') {
    //   this.registerForm.value.userPhone =
    //     this.countryCode + this.registerForm.value.userPhone;
    // }
    // this.registerForm.value.userPhone = this.countryCode + this.registerForm.value.userPhone;
    this.formSubmitted = true;
    if (this.registerForm.invalid) {
      this.spinner.hide();
      return;
    }
    this.registerForm.value.userName = this.registerForm.value.userName + '@' + this.userService.userValue.shortCode;
    this.userService.userRegister(this.registerForm.value).subscribe(
      (data: any) => {
        this.spinner.hide();
        this.formSubmitted = false;
        this.createForm();
        this.notificationService.showSuccess(data.message, '');
        this.router.navigate([`/users`]);
      },
      (error) => {
        this.spinner.hide();
      }
    );
  }

  countryCode = '91';
  onCountryChange(event) {
    this.countryCode = event.dialCode;
  }

  changeRole(e) {
    this.registerForm.get('role').setValue(e.target.value);
  }

  selectActions(event, val) {
    if (event) {
      this.selectedRoles.push(val);
    } else {
      const itemIndex = this.selectedRoles.indexOf(val);
      if (itemIndex != -1) this.selectedRoles.splice(itemIndex, 1);
    }
    this.registerForm.patchValue({ roles: this.selectedRoles });
  }

  goBack() {
    this.router.navigate(['/users']);
  }

  isNumber(evt) {
    evt = evt ? evt : window.event;
    var charCode = evt.which ? evt.which : evt.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
      return false;
    }
    return true;
  }
}
