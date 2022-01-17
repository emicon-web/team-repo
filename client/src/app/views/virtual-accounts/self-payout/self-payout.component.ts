import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { BsModalRef, ModalModule, BsModalService } from 'ngx-bootstrap/modal';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';

import { NotificationService } from '../../../services/notification.service';
import { UserService } from '../../../services/user.service';
import { RoleAccess } from '../../../models/role-access';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { VirtualAccountService } from 'src/app/services/payout-account.service';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-self-payout',
  templateUrl: './self-payout.component.html',
  styleUrls: ['./self-payout.component.scss'],
})
export class SelfPayoutComponent implements OnInit {
  bsModalRef: BsModalRef;
  state$: Observable<object>;
  id;
  roleAccess = RoleAccess;
  error: any;
  public data;
  merchantData: any;
  formSubmitted: boolean = false;
  selfPayoutForm: FormGroup;
  paymentInstrumentSelected: string = 'bank';
  payoutModeSelected: string = 'neft';
  virtualId: string = '';
  errorAccountId: boolean = false;
  allPayoutIds: any = [];
  merchantPayoutId: any;
  showOtherInput: boolean;
  amountError: boolean;
  payoutError: boolean;

  constructor(
    public activatedRoute: ActivatedRoute,
    private userService: UserService,
    private notificationService: NotificationService,
    private router: Router,
    private formBuilder: FormBuilder,
    private virtualAccountService: VirtualAccountService,
    private spinner: NgxSpinnerService
  ) {}

  ngOnInit(): void {
    this.merchantData = this.userService.userValue;
    this.createForm();
    this.getmerchantPayoutId();
    this.getAllPayoutAccounts();
  }

  // Getter for easy access to form fields
  get f() {
    return this.selfPayoutForm.controls;
  }

  createForm() {
    this.selfPayoutForm = this.formBuilder.group({
      beneficiaryName: ['', [Validators.required]],
      beneficiaryPhone: ['', [Validators.required]],
      beneficiaryEmail: ['', [Validators.required]],
      paymentAmount: ['', [Validators.required]],
      payoutPurpose: ['', [Validators.required]],
      payoutPurposeOther: [''],
    });
  }

  getAllPayoutAccounts() {
    this.virtualAccountService.getAllVirtualAccounts('ACTIVE').subscribe(
      (data: any) => {
        this.allPayoutIds = data;
      },
      (error) => (this.error = error)
    );
  }

  getVirtualAccountDetails(id) {
    this.virtualAccountService.getPADetails(id).subscribe(
      (data) => {
        this.data = data;
      },
      (error) => (this.error = error)
    );
  }

  getDataForPayoudId(evt) {
    this.errorAccountId = false;
    this.virtualId = evt.target.value;
    if (this.virtualId === '') {
      this.errorAccountId = true;
      this.data = {};
      return;
    }
    this.getVirtualAccountDetails(this.virtualId);
  }

  getmerchantPayoutId() {
    this.virtualAccountService.getmerchantPayoutId().subscribe((data: any) => {
      this.merchantPayoutId = data;
    });
  }

  onPurposeChange(value) {
    if (value === 'other') {
      this.showOtherInput = true;
      this.selfPayoutForm.controls['payoutPurposeOther'].setValidators([
        Validators.required,
      ]);
    } else {
      this.showOtherInput = false;
      this.selfPayoutForm.controls['payoutPurposeOther'].setValidators([]);
      this.selfPayoutForm.controls[
        'payoutPurposeOther'
      ].updateValueAndValidity();
    }
  }

  goBack() {
    this.router.navigate([
      `/virtual-accounts/virtual-account-details/${this.id}`,
    ]);
  }

  allowNumberWithDecimal(evt) {
    const reg = /^-?\d*(\.\d{0,2})?$/;
    let input = evt.target.value + String.fromCharCode(evt.charCode);

    if (!reg.test(input)) {
      evt.preventDefault();
    }
  }

  isNumber(evt) {
    evt = evt ? evt : window.event;
    var charCode = evt.which ? evt.which : evt.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
      return false;
    }
    return true;
  }

  letterOnly(event) {
    var charCode = event.keyCode;

    if (
      (charCode > 64 && charCode < 91) ||
      (charCode > 96 && charCode < 123) ||
      charCode == 8 ||
      charCode == 32
    ) {
      return true;
    }
    return false;
  }

  saveSelfPayout() {
    this.spinner.show();
    this.errorAccountId = false;
    this.formSubmitted = true;
    this.amountError = false;
    this.payoutError = false;

    if (this.virtualId === '') {
      this.spinner.hide();
      this.errorAccountId = true;
      return;
    }

    if (this.selfPayoutForm.value.payoutPurpose === '') {
      this.payoutError = true;
      this.spinner.hide();
      return;
    }

    if (this.selfPayoutForm.invalid) {
      this.spinner.hide();
      return;
    }

    if (this.selfPayoutForm.value.amount === '0') {
      this.amountError = true;
      this.spinner.hide();
      return;
    }

    const data = {
      merchantid: this.merchantData.merchantId,
      merchantPayoutId: this.merchantPayoutId,
      accountId: this.data.virtualAccountID,
      beneficiaryName: this.selfPayoutForm.value.beneficiaryName,
      beneficiaryMobileNumber: this.selfPayoutForm.value.beneficiaryPhone,
      beneficiaryEmailId: this.selfPayoutForm.value.beneficiaryEmail,
      payoutPurpose:
        this.selfPayoutForm.value.payoutPurpose === 'other'
          ? this.selfPayoutForm.value.payoutPurposeOther
          : this.selfPayoutForm.value.payoutPurpose,
      amount: this.selfPayoutForm.value.paymentAmount,
    };

    this.virtualAccountService.createSelfPayout(data).subscribe(
      (data: any) => {
        this.formSubmitted = false;
        this.spinner.hide();
        this.notificationService.showSuccess(
          'Self Payout created successfully',
          ''
        );

        this.virtualId = '';
        this.data = {};
        this.merchantPayoutId = '';
        this.getmerchantPayoutId();

        this.selfPayoutForm.patchValue({
          beneficiaryName: '',
          beneficiaryPhone: '',
          beneficiaryEmail: '',
          paymentAmount: '',
          payoutPurpose: '',
          payoutPurposeOther: '',
        });
        // this.router.navigate(['/virtual-accounts/add-insta-payout']);
      },
      (error) => {
        console.log(error);
        const allErrors = [];
        this.spinner.hide();
        // this.error = error;
        if (error.error.length) {
          error.error.forEach((el, index) => {
            this.notificationService.showError(el, '');
            allErrors.push(index + 1 + ' ' + el + '. ');
          });
        }
      }
    );
  }
}
