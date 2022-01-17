import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, Validators, FormArray } from '@angular/forms';
import { Router } from '@angular/router';

import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styles: [
  ]
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  formSubmitted = false;
  showPassword: boolean = false;


  constructor(private formBuilder: FormBuilder,
    private userService: UserService,
    private router: Router) { }

  ngOnInit(): void {
    this.createForm();
  }

  // Getter for easy access to form fields
  get f() { return this.registerForm.controls; }

  createForm() {
    this.registerForm = this.formBuilder.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$")]],
      password: ['', [Validators.required, Validators.pattern('(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[$@$!%*?&])[A-Za-z\d$@$!%*?&].{8,}')]],
      confirmPassword: ['', [Validators.required]],
      role: [["ROLE_ADMIN"]]
    }, {
      validator: this.MustMatch('password', 'confirmPassword')
    });
  }

  MustMatch(password1: string, password2: string) {
    return (formGroup: FormGroup) => {
      const pass = formGroup.controls[password1];
      const confirmPass = formGroup.controls[password2];
      if (confirmPass.errors && !confirmPass.errors.mustMatch) {
        return;
      }

      // set error on confirmPass if validation fails
      if (pass.value !== confirmPass.value) {
        confirmPass.setErrors({ mustMatch: true });
      } else {
        confirmPass.setErrors(null);
      }
    }
  }

  registerUser() {
    this.formSubmitted = true;

    if (this.registerForm.invalid) { return; }
    this.userService.userRegister(this.registerForm.value).subscribe(
      (data: any) => {
        // console.log(data);
        this.router.navigate([`/login`]);
      }, err => {
        // console.log(err);
      }
    );
  }
}
