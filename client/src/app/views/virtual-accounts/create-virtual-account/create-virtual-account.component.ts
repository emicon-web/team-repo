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
import { VirtualAccountService } from 'src/app/services/payout-account.service';
import { NgxSpinnerService } from 'ngx-spinner';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-create-virtual-account',
  templateUrl: './create-virtual-account.component.html',
  styleUrls: ['./create-virtual-account.component.scss'],
})
export class CreateVirtualAccountComponent implements OnInit {
  registerForm: FormGroup;
  formSubmitted = false;
  showPassword: boolean = false;
  role = [];
  categories = [];
  selectedRoles = [];
  merchantData: any;
  preFixId: string;
  error: any;

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private router: Router,
    private notificationService: NotificationService,
    private virtualAccountService: VirtualAccountService,
    private spinner: NgxSpinnerService
  ) {}
  loaded = false;

  ngOnInit(): void {
    this.createForm();
    this.merchantData = this.userService.userValue;
    this.preFixId = 'PHIP' + this.merchantData.merchantId.slice(-5);
  }

  // Getter for easy access to form fields
  get f() {
    return this.registerForm.controls;
  }

  createForm() {
    this.registerForm = this.formBuilder.group({
      virtualAccountId: ['', [Validators.required]],
      description: ['', [Validators.required]],
    });
  }

  registerVirtualAccount() {
    this.spinner.show();
    this.formSubmitted = true;
    if (this.registerForm.invalid) {
      this.spinner.hide();
      return;
    }

    const data = {
      virtualAccountID:
        this.preFixId + this.registerForm.value.virtualAccountId,
      merchantid: this.merchantData.merchantId,
      description: this.registerForm.value.description,
      createdBy: this.merchantData.userName,
      balance: 0,
    };

    this.virtualAccountService.createPayoutAccount(data).subscribe(
      (data: any) => {
        this.spinner.hide();
        this.formSubmitted = false;
        this.createForm();

        Swal.fire('Payout Account created successfully');
        // this.notificationService.showSuccess(
        //   'Payout Account created successfully',
        //   ''
        // );
        this.router.navigate([`/virtual-accounts`]);
      },
      (error) => {
        this.spinner.hide();
        this.error = error;
      }
    );
  }

  goBack() {
    this.router.navigate(['/virtual-accounts']);
  }
}
