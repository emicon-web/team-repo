import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { BsModalRef, ModalModule, BsModalService } from 'ngx-bootstrap/modal';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';

import { NotificationService } from '../../../services/notification.service';
import { UserService } from '../../../services/user.service';
import { RoleAccess } from '../../../models/role-access';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { VirtualAccountService } from 'src/app/services/payout-account.service';
import { instaPayoutModel } from 'src/app/models/insta-payout.model';
import { NgxSpinnerService } from 'ngx-spinner';
import { ConfigurationService } from 'src/app/services/configuration.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-insta-payout',
  templateUrl: './insta-payout.component.html',
  styleUrls: ['./insta-payout.component.scss'],
})
export class InstaPayoutComponent implements OnInit {
  bsModalRef: BsModalRef;
  state$: Observable<object>;
  id;
  roleAccess = RoleAccess;
  error: any;
  public data;
  merchantData: any;
  formSubmitted: boolean = false;
  instaPayoutForm: FormGroup;
  paymentInstrumentSelected: string = 'ACCOUNT';
  payoutModeSelected: string = '';
  instaPayoutModel = new instaPayoutModel();
  showOtherInput: boolean = false;
  allPayoutIds: any = [];
  virtualId = '';
  errorAccountId: boolean = false;
  amountError: boolean;
  merchantConfigurationData: any;
  isIMPSAllowed: boolean = false;
  isNEFTAllowed: boolean = false;
  isUPIAllowed: boolean = false;
  isRTGSAllowed: boolean = false;
  modeError: boolean = false;
  payoutError: boolean;
  merchantPayoutId;
  creditCardBank: any;

  constructor(
    public activatedRoute: ActivatedRoute,
    private userService: UserService,
    private notificationService: NotificationService,
    private router: Router,
    private formBuilder: FormBuilder,
    private virtualAccountService: VirtualAccountService,
    private spinner: NgxSpinnerService,
    private congiurationService: ConfigurationService
  ) {}

  ngOnInit(): void {
    this.getmerchantPayoutId();
    this.merchantData = this.userService.userValue;
    this.createForm();
    this.getAllPayoutAccounts();
    this.getConfigurationDetails();
  }

  abc = '';
  ifscCodeLengthcount(e) {
    if (this.instaPayoutForm.value.beneficiaryIFSCCode.length <= 3) {
      this.abc = '';
      this.instaPayoutForm.patchValue({
        bankAccountName: '',
      });
    }
    if (this.instaPayoutForm.value.beneficiaryIFSCCode.length == 4) {
      let data = {
        // prefix: 'HDFC',
        ifscprefix: this.instaPayoutForm.value.beneficiaryIFSCCode,
      };
      this.virtualAccountService.getIfscCode(data).subscribe((data: any) => {
        this.abc = data[0]?.bankName;
        this.instaPayoutForm.value.bankAccountName = data[0]?.bankName;
      });
    }
  }

  ifscGenerator(e) {
    if (this.instaPayoutForm.value.cardHolderBankIFSCCode.length <= 3) {
      this.creditCardBank = '';
      this.instaPayoutForm.patchValue({
        cardHolderBankName: '',
      });
    }
    if (this.instaPayoutForm.value.cardHolderBankIFSCCode.length == 4) {
      let data = {
        // prefix: 'HDFC',
        ifscprefix: this.instaPayoutForm.value.cardHolderBankIFSCCode,
      };
      this.virtualAccountService.getIfscCode(data).subscribe((data: any) => {
        this.creditCardBank = data[0]?.bankName;
        this.instaPayoutForm.value.cardHolderBankName = data[0]?.bankName;
      });
    }
  }

  getAllPayoutAccounts() {
    this.virtualAccountService.getAllVirtualAccounts('ACTIVE').subscribe(
      (data: any) => {
        this.allPayoutIds = data;
      },
      (error) => (this.error = error)
    );
  }

  getConfigurationDetails() {
    this.spinner.show();
    this.congiurationService
      .getDetails({ merchantid: this.merchantData.merchantId })
      .subscribe(
        (data) => {
          this.spinner.hide();
          this.merchantConfigurationData = data;
          if (
            this.merchantConfigurationData?.paymentMode !== '' &&
            this.merchantConfigurationData != null
          ) {
            this.merchantConfigurationData.paymentMode =
              this.merchantConfigurationData?.paymentMode
                .split(',')
                .map(function (item) {
                  return item.trim();
                });
          }
          if (this.merchantConfigurationData.paymentMode.includes('UPI')) {
            this.isUPIAllowed = true;
            this.instaPayoutForm.patchValue({ payoutPaymentMode: 'UPI' });
            this.payoutModeSelected = 'UPI';
          }
          if (this.merchantConfigurationData.paymentMode.includes('RTGS')) {
            this.isRTGSAllowed = true;
            this.instaPayoutForm.patchValue({ payoutPaymentMode: 'RTGS' });
            this.payoutModeSelected = 'RTGS';
          }
          if (this.merchantConfigurationData.paymentMode.includes('IMPS')) {
            this.isIMPSAllowed = true;
            this.instaPayoutForm.patchValue({ payoutPaymentMode: 'IMPS' });
            this.payoutModeSelected = 'IMPS';
          }
          if (this.merchantConfigurationData.paymentMode.includes('NEFT')) {
            this.isNEFTAllowed = true;
            this.instaPayoutForm.patchValue({ payoutPaymentMode: 'NEFT' });
            this.payoutModeSelected = 'NEFT';
          }
        },
        (error) => {
          this.spinner.hide();
          this.error = error;
        }
      );
  }

  // Getter for easy access to form fields
  get f() {
    return this.instaPayoutForm.controls;
  }

  createForm() {
    this.instaPayoutForm = this.formBuilder.group({
      beneficiaryName: ['', [Validators.required]],
      beneficiaryMobileNumber: ['', [Validators.required]],
      beneficiaryEmailId: ['', [Validators.required, Validators.email]],
      amount: ['', [Validators.required]],
      payoutPaymentInstrument: ['ACCOUNT', [Validators.required]],
      bankAccountName: [''],
      beneficiaryAccountNumber: [''],
      beneficiaryIFSCCode: [''],
      beneficiaryCardNumber: [''],
      cardHolderName: [''],
      cardHolderBankName: [''],
      cardHolderBankIFSCCode: [''],
      beneficiaryVPA: [''],
      payoutPaymentMode: ['', [Validators.required]],
      payoutPurpose: ['', [Validators.required]],
      payoutPurposeOther: [''],
    });
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

  getVirtualAccountDetails(id) {
    this.virtualAccountService.getPADetails(id).subscribe(
      (data) => {
        this.data = data;
      },
      (error) => (this.error = error)
    );
  }

  editVirtualAccount() {
    const initialState = {
      id: this.id,
    };
    // this.bsModalRef = this.modalService.show(EditUserComponent, {
    //   initialState,
    //   class: 'modal-lg',
    // });
    // this.bsModalRef.content.id = this.id;
    // this.bsModalRef.content.passIsUpdated.subscribe((receivedEntry) => {
    //   this.getUserDetails(this.id);
    // });

    // this.bsModalRef.content.onClose.subscribe(result => {
    //   console.log('results', result);
    // })
  }

  goBack() {
    this.router.navigate([
      `/virtual-accounts/virtual-account-details/${this.id}`,
    ]);
  }

  payoutModeChange(evt) {
    this.paymentInstrumentSelected = 'ACCOUNT';
    this.instaPayoutForm.patchValue({ payoutPaymentInstrument: 'ACCOUNT' });
    this.setBankValidators();
    if (evt.target.value === 'NEFT') {
      this.payoutModeSelected = 'NEFT';
    } else if (evt.target.value === 'IMPS') {
      this.payoutModeSelected = 'IMPS';
    } else {
      this.payoutModeSelected = 'UPI';
    }
  }

  setBankValidators() {
    this.instaPayoutForm.controls['bankAccountName'].setValidators([
      Validators.required,
    ]);
    this.instaPayoutForm.controls['beneficiaryAccountNumber'].setValidators([
      Validators.required,
    ]);
    this.instaPayoutForm.controls['beneficiaryIFSCCode'].setValidators([
      Validators.required,
    ]);
    this.instaPayoutForm.patchValue({
      beneficiaryCardNumber: '',
      cardHolderName: '',
      cardHolderBankName: '',
      cardHolderBankIFSCCode: '',
      beneficiaryVPA: '',
    });
    this.instaPayoutForm.controls['beneficiaryCardNumber'].setValidators([]);
    this.instaPayoutForm.controls[
      'beneficiaryCardNumber'
    ].updateValueAndValidity();
    this.instaPayoutForm.controls['cardHolderName'].setValidators([]);
    this.instaPayoutForm.controls['cardHolderName'].updateValueAndValidity();
    this.instaPayoutForm.controls['cardHolderBankName'].setValidators([]);
    this.instaPayoutForm.controls[
      'cardHolderBankName'
    ].updateValueAndValidity();
    this.instaPayoutForm.controls['cardHolderBankIFSCCode'].setValidators([]);
    this.instaPayoutForm.controls[
      'cardHolderBankIFSCCode'
    ].updateValueAndValidity();
    this.instaPayoutForm.controls['beneficiaryVPA'].setValidators([]);
    this.instaPayoutForm.controls['beneficiaryVPA'].updateValueAndValidity();
  }

  paymentInstrumentChange(evt) {
    if (evt.target.value === 'ACCOUNT') {
      this.paymentInstrumentSelected = 'ACCOUNT';
      this.setBankValidators();
    } else if (evt.target.value === 'CARD') {
      this.paymentInstrumentSelected = 'CARD';
      this.instaPayoutForm.controls['beneficiaryCardNumber'].setValidators([
        Validators.required,
      ]);
      this.instaPayoutForm.controls['cardHolderName'].setValidators([
        Validators.required,
      ]);
      this.instaPayoutForm.controls['cardHolderBankName'].setValidators([
        Validators.required,
      ]);
      this.instaPayoutForm.controls['cardHolderBankIFSCCode'].setValidators([
        Validators.required,
      ]);
      this.instaPayoutForm.patchValue({
        beneficiaryCardNumber: '',
        cardHolderName: '',
        cardHolderBankName: '',
        cardHolderBankIFSCCode: '',
        bankAccountName: '',
        beneficiaryAccountNumber: '',
        beneficiaryIFSCCode: '',
      });
      this.instaPayoutForm.controls['bankAccountName'].setValidators([]);
      this.instaPayoutForm.controls['bankAccountName'].updateValueAndValidity();
      this.instaPayoutForm.controls['beneficiaryAccountNumber'].setValidators(
        []
      );
      this.instaPayoutForm.controls[
        'beneficiaryAccountNumber'
      ].updateValueAndValidity();
      this.instaPayoutForm.controls['beneficiaryIFSCCode'].setValidators([]);
      this.instaPayoutForm.controls[
        'beneficiaryIFSCCode'
      ].updateValueAndValidity();
      this.instaPayoutForm.controls['beneficiaryVPA'].setValidators([]);
      this.instaPayoutForm.controls['beneficiaryVPA'].updateValueAndValidity();
      this.instaPayoutForm.updateValueAndValidity();
    } else {
      this.paymentInstrumentSelected = 'VPA';
      this.instaPayoutForm.patchValue({
        bankAccountName: '',
        beneficiaryAccountNumber: '',
        beneficiaryIFSCCode: '',
        beneficiaryVPA: '',
      });
      this.instaPayoutForm.controls['beneficiaryVPA'].setValidators([
        Validators.required,
      ]);
      this.instaPayoutForm.controls['beneficiaryCardNumber'].setValidators([]);
      this.instaPayoutForm.controls[
        'beneficiaryCardNumber'
      ].updateValueAndValidity();
      this.instaPayoutForm.controls['cardHolderName'].setValidators([]);
      this.instaPayoutForm.controls['cardHolderName'].updateValueAndValidity();
      this.instaPayoutForm.controls['cardHolderBankName'].setValidators([]);
      this.instaPayoutForm.controls[
        'cardHolderBankName'
      ].updateValueAndValidity();
      this.instaPayoutForm.controls['cardHolderBankIFSCCode'].setValidators([]);
      this.instaPayoutForm.controls[
        'cardHolderBankIFSCCode'
      ].updateValueAndValidity();
      this.instaPayoutForm.controls['bankAccountName'].setValidators([]);
      this.instaPayoutForm.controls['bankAccountName'].updateValueAndValidity();
      this.instaPayoutForm.controls['beneficiaryAccountNumber'].setValidators(
        []
      );
      this.instaPayoutForm.controls[
        'beneficiaryAccountNumber'
      ].updateValueAndValidity();
      this.instaPayoutForm.controls['beneficiaryIFSCCode'].setValidators([]);
      this.instaPayoutForm.controls[
        'beneficiaryIFSCCode'
      ].updateValueAndValidity();
      this.instaPayoutForm.updateValueAndValidity();
    }
  }

  onPurposeChange(value) {
    if (value === 'other') {
      this.showOtherInput = true;
      this.instaPayoutForm.controls['payoutPurposeOther'].setValidators([
        Validators.required,
      ]);
    } else {
      this.showOtherInput = false;
      this.instaPayoutForm.controls['payoutPurposeOther'].setValidators([]);
      this.instaPayoutForm.controls[
        'payoutPurposeOther'
      ].updateValueAndValidity();
    }
  }

  allowNumberWithDecimal(evt) {
    console.log(evt);
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

  getmerchantPayoutId() {
    this.virtualAccountService.getmerchantPayoutId().subscribe((data: any) => {
      this.merchantPayoutId = data;
    });
  }

  saveInstaPayout() {
    this.errorAccountId = false;
    this.formSubmitted = true;
    this.amountError = false;
    this.modeError = false;
    this.payoutError = false;

    if (this.virtualId === '') {
      this.spinner.hide();
      this.errorAccountId = true;
      return;
    }

    if (this.instaPayoutForm.value.payoutPaymentMode === '') {
      this.modeError = true;
      this.spinner.hide();
      return;
    }

    if (this.instaPayoutForm.value.payoutPurpose === '') {
      this.payoutError = true;
      this.spinner.hide();
      return;
    }

    if (this.instaPayoutForm.invalid) {
      this.spinner.hide();
      return;
    }

    if (this.instaPayoutForm.value.amount === '0') {
      this.amountError = true;
      this.spinner.hide();
      return;
    }

    const data = {
      merchantid: this.merchantData.merchantId,
      merchantPayoutId: this.merchantPayoutId,
      accountId: this.data.virtualAccountID,
      beneficiaryName: this.instaPayoutForm.value.beneficiaryName,
      payoutPaymentInstrument:
        this.instaPayoutForm.value.payoutPaymentInstrument,
      payoutPaymentMode: this.instaPayoutForm.value.payoutPaymentMode,
      bankAccountName: this.instaPayoutForm.value.bankAccountName,
      beneficiaryAccountNumber:
        this.instaPayoutForm.value.beneficiaryAccountNumber,
      beneficiaryIFSCCode: this.instaPayoutForm.value.beneficiaryIFSCCode,
      beneficiaryCardNumber: this.instaPayoutForm.value.beneficiaryCardNumber,
      cardHolderName: this.instaPayoutForm.value.cardHolderName,
      cardHolderBankName: this.instaPayoutForm.value.cardHolderBankName,
      cardHolderBankIFSCCode: this.instaPayoutForm.value.cardHolderBankIFSCCode,
      beneficiaryVPA: this.instaPayoutForm.value.beneficiaryVPA,
      payoutPurpose:
        this.instaPayoutForm.value.payoutPurpose === 'other'
          ? this.instaPayoutForm.value.payoutPurposeOther
          : this.instaPayoutForm.value.payoutPurpose,
      amount: this.instaPayoutForm.value.amount,
      payoutDateTime: new Date().getTime(),
      beneficiaryMobileNumber:
        this.instaPayoutForm.value.beneficiaryMobileNumber,
      beneficiaryEmailId: this.instaPayoutForm.value.beneficiaryEmailId,
    };

    if (
      parseFloat(this.data.balance) <
      parseFloat(this.instaPayoutForm.value.amount)
    ) {
      Swal.fire({
        // title: 'Are you sure?',
        text: `You are initiating payout for an amount more than current account balance. Are you sure you want to procced with the payout initiation?`,
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes',
      }).then((result) => {
        if (result.isConfirmed) {
          this.spinner.show();
          this.virtualAccountService.createInstaPayout(data).subscribe(
            (data: any) => {
              this.formSubmitted = false;
              this.spinner.hide();
              this.notificationService.showSuccess(
                'Insta Payout created successfully',
                ''
              );

              this.virtualId = '';
              this.data = {};
              this.merchantPayoutId = '';
              this.getmerchantPayoutId();
              this.instaPayoutForm.patchValue({
                beneficiaryName: '',
                beneficiaryMobileNumber: '',
                beneficiaryEmailId: '',
                amount: '',
                bankAccountName: '',
                beneficiaryAccountNumber: '',
                beneficiaryIFSCCode: '',
                beneficiaryCardNumber: '',
                cardHolderName: '',
                cardHolderBankName: '',
                cardHolderBankIFSCCode: '',
                beneficiaryVPA: '',
                payoutPurpose: '',
              });
              // this.router.navigate(['/virtual-accounts/add-insta-payout']);
            },
            (error) => {
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
      });
    } else {
      this.spinner.show();
      this.virtualAccountService.createInstaPayout(data).subscribe(
        (data: any) => {
          this.formSubmitted = false;
          this.spinner.hide();
          this.notificationService.showSuccess(
            'Insta Payout created successfully',
            ''
          );

          this.virtualId = '';
          this.data = {};
          this.merchantPayoutId = '';
          this.getmerchantPayoutId();
          this.instaPayoutForm.patchValue({
            beneficiaryName: '',
            beneficiaryMobileNumber: '',
            beneficiaryEmailId: '',
            amount: '',
            bankAccountName: '',
            beneficiaryAccountNumber: '',
            beneficiaryIFSCCode: '',
            beneficiaryCardNumber: '',
            cardHolderName: '',
            cardHolderBankName: '',
            cardHolderBankIFSCCode: '',
            beneficiaryVPA: '',
            payoutPurpose: '',
          });
          // this.router.navigate(['/virtual-accounts/add-insta-payout']);
        },
        (error) => {
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
}
