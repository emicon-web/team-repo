import { Component, OnInit, ViewChild } from '@angular/core';
import { ModalDirective } from 'ngx-bootstrap/modal';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { NotificationService } from '../../../services/notification.service';
import { RoleAccess } from '../../../models/role-access';
import { VirtualAccountService } from 'src/app/services/payout-account.service';
import Swal from 'sweetalert2';
import { CancelPayoutService } from 'src/app/services/cancel-payout.service';
import { BsDatepickerConfig } from 'ngx-bootstrap/datepicker';
import { FormBuilder, Validators } from '@angular/forms';
import { NgxSpinnerService } from 'ngx-spinner';
import * as XLSX from 'xlsx';

@Component({
  selector: 'app-transactional-report',
  templateUrl: './transactional-report.component.html',
  styleUrls: ['./transactional-report.component.scss'],
})
export class TransactionalReportComponent implements OnInit {
  @ViewChild('dangerModal', { static: false })
  public dangerModal: ModalDirective;
  roleAccess = RoleAccess;
  // api response error or data
  error: any;
  public data;

  collection: any;
  headingText = 'Transactional Report';
  systemCollection = [];
  issuerCollection = [];
  p: number = 0;
  itemsPerPage = 25;
  totalItems: any;
  formSubmitted = false;
  deleteUserId;
  showCol: boolean;
  merchantData: any;
  virtualAccounts: any = [];
  virtualId = '';
  txnType = 'all';
  txnId = '';
  errorTxn: boolean;
  allTxns: any = [];
  selectedTxnIds: any = [];
  minDate: Date;
  maxDate: any;
  bsConfig?: Partial<BsDatepickerConfig>;
  reportFiltersForm: any;
  errorAccountId: boolean = false;
  toMinDate: Date = new Date();
  reportData: any = [];
  payoutStatusOptions = [];
  config = {
    itemsPerPage: 25,
    currentPage: 0,
    totalItems: 0,
  };
  payoutModes: any = [];
  payoutInstruments: any = [];
  isFirstLoad = true;

  constructor(
    private userService: UserService,
    private virtualAccountService: VirtualAccountService,
    private router: Router,
    private notificationService: NotificationService,
    private formBuilder: FormBuilder,
    private spinner: NgxSpinnerService
  ) {
    this.createForm();
    this.getAllVirtualAccount();
    this.getAllStatuses();
    this.bsConfig = Object.assign({}, { containerClass: 'theme-blue' });
  }

  ngOnInit(): void {
    this.minDate = new Date();
    this.maxDate = new Date();
    this.merchantData = this.userService.userValue;
    this.getPayoutInstruments();
    this.getPayoutModees();
    this.getDefaultRecords();
  }

  createForm() {
    this.formSubmitted = false;
    this.reportFiltersForm = this.formBuilder.group({
      payoutAccountId: ['', Validators.required],
      payoutTxnId: [''],
      payphiReferenceId: [''],
      batchReferenceId: [''],
      paymentMode: [''],
      payoutInstrument: [''],
      payoutType: [''],
      payoutStatus: ['SUCCESS'],
      fromDate: ['', Validators.required],
      toDate: ['', Validators.required],
    });

    this.reportFiltersForm.patchValue({
      fromDate: new Date(),
      toDate: new Date(),
    });
  }

  getAllStatuses() {
    this.collection = [];
    this.virtualAccountService.getAllStatuses().subscribe(
      (data: any) => {
        this.payoutStatusOptions = data;
        this.payoutStatusOptions.unshift('ALL');
      },
      (error) => (this.error = error)
    );
  }

  getPayoutInstruments() {
    this.collection = [];
    this.virtualAccountService
      .getPayoutInstruments({ merchantid: this.merchantData.merchantId })
      .subscribe(
        (data: any) => {
          if (data.instruments && data.instruments !== '') {
            this.payoutInstruments = data.instruments
              .split(',')
              .map(function (item) {
                return item.trim();
              });
          }
        },
        (error) => (this.error = error)
      );
  }

  getDefaultRecords() {
    const offset =
      this.config.currentPage === 0
        ? this.config.currentPage
        : this.config.currentPage - 1;

    const dataToSend = {
      payoutStatus: 'SUCCESS',
      fromDate: new Date().getTime().toString(),
      toDate: new Date().getTime().toString(),
    };

    this.virtualAccountService
      .getDefaultRecords(dataToSend, this.config.itemsPerPage, offset)
      .subscribe(
        (data: any) => {
          if (data.data) {
            this.reportData = data.data.transactionData;
            this.config.totalItems = data.count;
          }
        },
        (error) => (this.error = error)
      );
  }

  getPayoutModees() {
    this.collection = [];
    this.virtualAccountService
      .getPayoutModees({ merchantid: this.merchantData.merchantId })
      .subscribe(
        (data: any) => {
          if (data.paymentMode && data.paymentMode !== '') {
            this.payoutModes = data.paymentMode.split(',').map(function (item) {
              return item.trim();
            });
          }
        },
        (error) => (this.error = error)
      );
  }

  getAllVirtualAccount() {
    this.collection = [];
    this.virtualAccountService.getAllVirtualAccounts().subscribe(
      (data: any) => {
        this.virtualAccounts = data;
      },
      (error) => (this.error = error)
    );
  }

  getPage(page) {
    this.config.currentPage = page;
    if (this.isFirstLoad) {
      this.getDefaultRecords();
    } else {
      this.getFilteredRecords();
    }
  }

  itemOnPage(no) {
    this.config.currentPage = 0;
    this.config.itemsPerPage = no;
    if (this.isFirstLoad) {
      this.getDefaultRecords();
    } else {
      this.getFilteredRecords();
    }
  }

  setMinToDate(date) {
    console.log(date);
    if (!date) return;
    this.reportFiltersForm.patchValue({
      toDate: '',
    });
    this.toMinDate = new Date(date);
    var dateSelected = new Date(date);
    var newDate = dateSelected;
    newDate.setDate(newDate.getDate() + 7);
    this.maxDate = newDate;
  }

  changePayoutAcId(evt) {
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
        console.log(data);

        this.data = data;
      },
      (error) => (this.error = error)
    );
  }

  get f() {
    return this.reportFiltersForm.controls;
  }

  getFilteredRecords(type = '') {
    this.isFirstLoad = false;
    this.config.totalItems = 0;
    if (type === 'searched') {
      this.config.currentPage = 0;
      this.config.itemsPerPage = 25;
    }

    this.formSubmitted = true;
    this.reportData = [];

    if (
      this.reportFiltersForm.value.toDate <
      this.reportFiltersForm.value.fromDate
    ) {
      this.notificationService.showError('', 'Please Enter Valid Date Range');
      return;
    }
    if (this.reportFiltersForm.invalid) {
      return;
    }

    this.spinner.show();

    const dataToSend = {
      accountId: this.reportFiltersForm.value.payoutAccountId,
      merchantPayoutId: this.reportFiltersForm.value.payoutTxnId || '',
      payoutStatus:
        this.reportFiltersForm.value.payoutStatus === 'ALL'
          ? ''
          : this.reportFiltersForm.value.payoutStatus.toUpperCase(),
      payoutPaymentInstrument:
        this.reportFiltersForm.value.payoutInstrument || '',
      payoutPaymentMode: this.reportFiltersForm.value.paymentMode || '',
      batchId:
        this.reportFiltersForm.value.batchReferenceId === ''
          ? ''
          : parseInt(this.reportFiltersForm.value.batchReferenceId),
      payoutid:
        this.reportFiltersForm.value.payphiReferenceId === ''
          ? ''
          : parseInt(this.reportFiltersForm.value.payphiReferenceId),
      fromDate: new Date(this.reportFiltersForm.value.fromDate)
        .getTime()
        .toString(),
      toDate: new Date(this.reportFiltersForm.value.toDate)
        .getTime()
        .toString(),
    };

    const offset =
      this.config.currentPage === 0
        ? this.config.currentPage
        : this.config.currentPage - 1;

    this.virtualAccountService
      .getTransactionalData(dataToSend, this.config.itemsPerPage, offset)
      .subscribe(
        (data: any) => {
          this.spinner.hide();
          if (data.data) {
            this.reportData = data.data.transactionData;
            this.config.totalItems = data.count;
          }
          this.formSubmitted = false;
          console.log(this.reportData, this.config.totalItems);
        },
        (error) => {
          this.spinner.hide();
          this.reportData = [];
          this.error = error;
        }
      );
  }

  exportexcel(): void {
    /* table id is passed over here */
    let element = document.getElementById('excel-table');
    const ws: XLSX.WorkSheet = XLSX.utils.table_to_sheet(element);

    /* generate workbook and add the worksheet */
    const wb: XLSX.WorkBook = XLSX.utils.book_new();
    // var ws = XLSX.utils.json_to_sheet(this.reportData);
    XLSX.utils.book_append_sheet(wb, ws, 'Sheet1');

    /* save to file */
    XLSX.writeFile(wb, 'report_' + new Date().getTime() + '.xlsx');
  }
}
