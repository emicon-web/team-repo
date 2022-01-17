import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import {
  FormGroup,
  FormControl,
  FormBuilder,
  Validators,
  FormArray,
} from '@angular/forms';
import { BsModalService } from 'ngx-bootstrap/modal';

import { UserService } from '../../../services/user.service';
import { NotificationService } from '../../../services/notification.service';

import { Role } from '../../../models/role';
import { ConfigurationService } from 'src/app/services/configuration.service';
import { configurationModel } from 'src/app/models/configuration.model';
import { newArray } from '@angular/compiler/src/util';

@Component({
  selector: 'app-edit-configuration',
  templateUrl: './edit-configuration.component.html',
  styleUrls: ['./edit-configuration.component.scss'],
})
export class EditConfigurationComponent implements OnInit {
  merchantConfiguration = new configurationModel();
  formSubmitted;
  public id: any;
  data;
  editForm: FormGroup;
  paymentMode = [
    {
      label: 'NEFT',
      value: 'NEFT',
    },
    {
      label: 'IMPS',
      value: 'IMPS',
    },
    {
      label: 'UPI',
      value: 'UPI',
    },
    {
      label: 'RTGS',
      value: 'RTGS',
    },
  ];
  instruments = [
    {
      label: 'Bank Account',
      value: 'ACCOUNT',
    },
    {
      label: 'Credit Card',
      value: 'CARD',
    },
    {
      label: 'VPA',
      value: 'VPA',
    },
  ];
  role;
  @Output() passIsUpdated: EventEmitter<any> = new EventEmitter();
  merchantData: any;
  error: any;
  payValue: any;

  constructor(
    private userService: UserService,
    private formBuilder: FormBuilder,
    private modalService: BsModalService,
    private notificationService: NotificationService,
    private configurationService: ConfigurationService
  ) {}

  loaded = false;

  ngOnInit(): void {
    this.merchantData = this.userService.userValue;
    this.createForm();
    this.getConfigurationDetails();
  }

  // Getter for easy access to form fields
  get f() {
    return this.editForm.controls;
  }

  getConfigurationDetails() {
    this.configurationService
      .getDetails({ merchantid: this.merchantData.merchantId })
      .subscribe(
        (data) => {
          if (data) {
            this.data = data;

            // setting amount to 2 decimal points
            this.merchantConfiguration = this.data;
            this.merchantConfiguration.minPayoutAmount =
              this.data.minPayoutAmount.toFixed(2);
            this.merchantConfiguration.maxPayoutAmount =
              this.data.maxPayoutAmount.toFixed(2);
            this.merchantConfiguration.lowBalanceThreshold =
              this.data.lowBalanceThreshold.toFixed(2);

            this.createForm();
            if (this.data?.paymentMode !== '' && this.data != null) {
              const splitValue = this.data.paymentMode.split(',');
              if (splitValue.length) {
                let arr = [];
                splitValue.forEach((el) => {
                  arr.push(el.toUpperCase().trim());
                });
                this.merchantConfiguration.paymentMode = arr;
              }
            }
            if (
              this.data?.instruments !== '' &&
              this.data?.instruments !== 'Empty'
            ) {
              const splitValue = this.data?.instruments.split(',');
              if (splitValue?.length) {
                let arr = [];
                splitValue.forEach((el) => {
                  arr.push(el.toUpperCase().trim());
                });
                this.merchantConfiguration.instruments = arr;
              }
            }
          }
        },
        (error) => (this.error = error)
      );
  }

  createForm() {
    this.editForm = this.formBuilder.group({
      paymentMode: ['', Validators.required],
      instruments: ['', Validators.required],
      minPayoutAmount: [0, Validators.required],
      maxPayoutAmount: [0, Validators.required],
      lowBalanceThreshold: [0, Validators.required],
      enableInstaPayChecker: [this.merchantConfiguration.enableInstaPayChecker],
      enableSelfPayChecker: [this.merchantConfiguration.enableSelfPayChecker],
      enableCancelPayChecker: [
        this.merchantConfiguration.enableCancelPayChecker,
      ],
      enableCloseAcctChecker: [
        this.merchantConfiguration.enableCloseAcctChecker,
      ],
      enableAPIPayChecker: [this.merchantConfiguration.enableAPIPayChecker],
    });
    this.loaded = true;
  }

  onToggle(evt, type) {
    console.log(evt.target.checked, type);
    if (type === 'enableInstaPayChecker') {
      this.merchantConfiguration.enableInstaPayChecker = evt.target.checked;
    } else if (type === 'enableSelfPayChecker') {
      this.merchantConfiguration.enableSelfPayChecker = evt.target.checked;
    } else if (type === 'enableCancelPayChecker') {
      this.merchantConfiguration.enableCancelPayChecker = evt.target.checked;
    } else if (type === 'enableCloseAcctChecker') {
      this.merchantConfiguration.enableCloseAcctChecker = evt.target.checked;
    } else {
      this.merchantConfiguration.enableAPIPayChecker = evt.target.checked;
    }
  }

  editConfiguration() {
    this.formSubmitted = true;
    if (this.editForm.invalid) {
      this.notificationService.showError('All fields are required', '');
      return;
    }

    let payMode = '';
    let instr = '';

    // creating payment mode comman separated string from array
    if (this.merchantConfiguration.paymentMode.length) {
      this.merchantConfiguration.paymentMode.forEach((mode, index) => {
        if (
          mode && // 👈 null and undefined check
          Object.keys(mode).length === 0 &&
          Object.getPrototypeOf(mode) === Object.prototype
        ) {
          if (index > 0) {
            payMode = payMode.toString() + ' , ' + mode.value.toString();
          } else {
            payMode = payMode.toString() + mode.value.toString();
          }
        } else {
          if (index > 0) {
            payMode = payMode.toString() + ' , ' + mode.toString();
          } else {
            payMode = payMode.toString() + mode.toString();
          }
        }
      });
    }

    // creating payment instruments comman separated string from array
    if (
      this.merchantConfiguration.instruments.length &&
      this.merchantConfiguration.instruments !== 'Empty'
    ) {
      this.merchantConfiguration.instruments.forEach((mode, index) => {
        if (
          mode && // 👈 null and undefined check
          Object.keys(mode).length === 0 &&
          Object.getPrototypeOf(mode) === Object.prototype
        ) {
          if (index > 0) {
            instr = instr.toString() + ' , ' + mode.value.toString();
          } else {
            instr = instr.toString() + mode.value.toString();
          }
        } else {
          if (index > 0) {
            instr = instr.toString() + ' , ' + mode.toString();
          } else {
            instr = instr.toString() + mode.toString();
          }
        }
      });
    }

    this.merchantConfiguration.paymentMode = payMode;
    this.merchantConfiguration.instruments = instr;
    this.merchantConfiguration.merchantid = this.merchantData.merchantId;
    this.merchantConfiguration.updateDate = new Date().getTime();
    this.merchantConfiguration.updatedBy = this.merchantData.userName;

    // calling API
    this.configurationService
      .editConfiguration(this.merchantConfiguration)
      .subscribe((data: any) => {
        this.notificationService.showSuccess(
          'Configurations updated sucessfully',
          ''
        );
        this.passIsUpdated.emit();
        this.close();
      });
  }

  close() {
    this.modalService._hideModal(1);
  }

  isNumber(event) {
    const reg = /^-?\d*(\.\d{0,2})?$/;
    let input = event.target.value + String.fromCharCode(event.charCode);

    if (!reg.test(input)) {
      event.preventDefault();
    }
  }
}
