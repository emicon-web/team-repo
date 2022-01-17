import { Component, OnInit, ViewChild } from '@angular/core';
import { ModalDirective } from 'ngx-bootstrap/modal';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { NotificationService } from '../../../services/notification.service';
import { RoleAccess } from '../../../models/role-access';
import { VirtualAccountService } from 'src/app/services/payout-account.service';
import Swal from 'sweetalert2';
import { CancelPayoutService } from 'src/app/services/cancel-payout.service';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-cancel-payout',
  templateUrl: './cancel-payout.component.html',
  styleUrls: ['./cancel-payout.component.scss'],
})
export class CancelPayoutComponent implements OnInit {
  @ViewChild('dangerModal', { static: false })
  public dangerModal: ModalDirective;
  roleAccess = RoleAccess;
  sAll: Boolean = false;
  // api response error or data
  error: any;
  public data;

  // for search
  public filterQuery = '';
  public filterQueryEmail = '';
  public filterQueryIssuer = '';

  collection: any;
  headingText = 'Cancel Payout';
  systemCollection = [];
  issuerCollection = [];
  p: number = 0;
  errorAccountId: boolean = false;
  itemsPerPage = 25;
  totalItems: any;

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
  mId;
  errorVirtualId: boolean;

  constructor(
    private userService: UserService,
    private virtualAccountService: VirtualAccountService,
    private router: Router,
    private notificationService: NotificationService,
    private cancelPayoutService: CancelPayoutService,
    private spinner: NgxSpinnerService
  ) {
    this.getAllVirtualAccount();
  }

  ngOnInit(): void {
    this.merchantData = this.userService.userValue;
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
    this.p = page;
    this.searchDataForTxn();
  }

  itemOnPage(no) {
    this.p = 0;
    this.itemsPerPage = no;
    this.searchDataForTxn();
  }

  mercahntTxnIdChange(evt) {
    this.errorTxn = false;
    if (evt.target.value === 'all') {
      this.txnId = '';
    }
    this.txnType = evt.target.value;
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

        this.data = data;
      },
      (error) => (this.error = error)
    );
  }

  searchDataForTxn(type = '') {
    this.selectedTxnIds = [];
    this.checkAll();
    if (type === 'initial') {
      this.p = 0;
      this.itemsPerPage = 25;
    }

    this.spinner.show();
    this.errorVirtualId = false;
    this.errorTxn = false;

    if (this.virtualId == '') {
      this.spinner.hide();
      this.errorVirtualId = true;
      return;
    }

    if (this.txnType === 'single' && this.txnId === '') {
      this.spinner.hide();
      this.errorTxn = true;
      return;
    }

    let path = '';
    let dataToSend;

    const offset = this.p === 0 ? this.p : this.p - 1;

    if (this.txnType === 'all') {
      this.cancelPayoutService
        .getAllReadyTxns(
          { accountId: this.virtualId },
          this.itemsPerPage,
          offset
        )
        .subscribe(
          (data: any) => {
            this.totalItems = 0;
            this.spinner.hide();
            if (data.data) {
              this.allTxns = data.data.data;
              this.totalItems = data.count;
            }
          },
          (error) => {
            this.totalItems = 0;
            this.spinner.hide();
            this.allTxns = [];
            this.error = error;
          }
        );
    } else {
      path = '/getAllPayoutsById';
      dataToSend = {
        merchantPayoutId: this.txnId,
        accountId: this.virtualId,
      };
      this.cancelPayoutService.getTxns(path, dataToSend).subscribe(
        (data: any) => {
          this.spinner.hide();
          this.allTxns = data;
          this.totalItems = 1;
        },
        (error) => {
          this.spinner.hide();
          this.error = error;
        }
      );
    }
  }

  checkAll() {
    if (this.selectedTxnIds.length != this.allTxns.length) {
      return false;
    } else {
      return true;
    }
  }

  selectTxnAc(value, txnId) {
    var index = this.selectedTxnIds.indexOf(txnId);
    if (value && !(index > -1)) {
      this.selectedTxnIds.push(txnId);
    } else {
      if (index > -1) {
        this.selectedTxnIds.splice(index, 1);
      }
    }
    this.checkAll();
  }

  resetData() {
    this.txnId = '';
    this.allTxns = [];
  }

  closeAccounts() {
    const dataToSend = {
      merchantPayoutId: this.selectedTxnIds,
      merchantid: this.userService.userValue.merchantId,
      canceledBy: this.merchantData.userName,
      cancelDate: new Date().getTime().toString(),
    };

    Swal.fire({
      title: 'Are you sure?',
      text: 'you want to cancel the selected payouts?',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Yes',
    }).then((result) => {
      if (result.isConfirmed) {
        this.spinner.show();
        this.cancelPayoutService
          .cancelPayout('canclePayout', dataToSend)
          .subscribe(
            (data: any) => {
              this.spinner.hide();
              this.notificationService.showSuccess(data.message, '');
              this.selectedTxnIds = [];
              if (this.txnType === 'all') {
                this.searchDataForTxn();
              } else {
                this.resetData();
              }
            },
            (error) => {
              this.spinner.hide();
              this.selectedTxnIds = [];
              if (this.txnType === 'all') {
                this.searchDataForTxn();
              } else {
                this.resetData();
              }
              this.error = error;
            }
          );
      }
    });
  }
  selectAll(event) {
    if (event == false) {
      this.selectedTxnIds = [];
    } else if (event == true) {
      for (let i = 0; i < this.allTxns.length; i++) {
        this.selectedTxnIds.push(this.allTxns[i].merchantPayoutId)
      }
    }
    this.checkAll();
  }

  contains(id) {
    this.checkAll();
    return this.selectedTxnIds.includes(id);
  }
}
