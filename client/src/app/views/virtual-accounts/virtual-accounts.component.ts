import { Component, OnInit, ViewChild } from '@angular/core';
import { ModalDirective } from 'ngx-bootstrap/modal';
import { Router } from '@angular/router';
import { NotificationService } from '../../services/notification.service';
import { RoleAccess } from '../../models/role-access';
import { VirtualAccountService } from 'src/app/services/payout-account.service';
import { UserService } from 'src/app/services/user.service';
import Swal from 'sweetalert2';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-virtual-accounts',
  templateUrl: './virtual-accounts.component.html',
  styleUrls: ['./virtual-accounts.component.scss'],
})
export class VirtualAccountComponent implements OnInit {
  @ViewChild('dangerModal', { static: false })
  public dangerModal: ModalDirective;
  roleAccess = RoleAccess;
  // api response error or data
  error: any;
  public data;

  // for search
  public filterQuery = '';
  public filterQueryEmail = '';
  public filterQueryIssuer = '';

  collection: any;
  btnText = 'View Issuer Users';
  headingText = 'Payout Accounts';
  systemCollection = [];
  issuerCollection = [];
  p: number;
  itemsPerPage = 10;
  totalItems: any;

  deleteUserId;
  showCol: boolean;
  closeVAId: any;
  mId;
  closureReason: any = '';
  modalError: boolean = false;
  merchantData: any;

  constructor(
    private virtualAccountService: VirtualAccountService,
    private router: Router,
    private userService: UserService,
    private notificationService: NotificationService,
    private spinner: NgxSpinnerService
  ) {
    this.getAllVirtualAccount();
  }

  ngOnInit(): void {
    this.merchantData = this.userService.userValue;
  }

  false;
  getAllVirtualAccount() {
    this.spinner.show();
    this.collection = [];
    this.totalItems = 0;
    this.virtualAccountService.getAllVirtualAccounts('ACTIVE').subscribe(
      (data: any) => {
        this.spinner.hide();
        this.collection = data;
        this.totalItems = this.collection.length;
      },
      (error) => {
        this.spinner.hide();
        this.error = error;
      }
    );
  }

  distribute(data) {
    this.systemCollection = [];
    for (let i = 0; i < data.length; i++) {
      if (data[i].userType == 'ISSUER') {
        this.issuerCollection.push(data[i]);
      } else {
        this.systemCollection.push(data[i]);
      }
    }
  }

  getPage(page) {
    console.log(page);
    // this.userService.getAllUsers()
    //   .subscribe(
    //     (data: any) => {
    //       this.data = data;
    //       this.collection = data;
    //       this.totalItems = data.length;
    //     },
    //     error => this.error = error
    //   );
  }

  itemOnPage(no) {
    this.p = 0;
    this.itemsPerPage = no;
    // this.userService.getAllUsers()
    //   .subscribe(
    //     (data: any) => {
    //       this.data = data;
    //       this.collection = data;
    //       this.totalItems = data.length;
    //     },
    //     error => this.error = error
    //   );
  }

  confirmCloseVA(id) {
    this.closeVAId = id;
    this.dangerModal.show();
  }

  hideCloseVA() {
    this.modalError = false;
    this.closureReason = '';
    this.dangerModal.hide();
  }

  closeVirtualAccount() {
    this.modalError = false;
    if (this.closureReason === '') {
      this.modalError = true;
      return;
    }
    const data = {
      virtualAccountID: this.closeVAId,
      // status: 'CLOSED',
      merchantid: this.userService.userValue.merchantId,
      closedDate: new Date().getTime().toString(),
      closedBy: this.merchantData.userName,
      closeReason: this.closureReason,
    };

    Swal.fire({
      // title: 'Are you sure?',
      text: 'Are you sure you want to close this payout account?',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Yes',
    }).then((result) => {
      if (result.isConfirmed) {
        this.virtualAccountService.closePA(data).subscribe(
          (data: any) => {
            this.closureReason = '';
            this.getAllVirtualAccount();
            this.notificationService.showSuccess(data.message, '');
            this.dangerModal.hide();
          },
          (error) => {
            this.closureReason = '';
            this.error = error;
          }
        );
      }
    });
  }

  viewDetails(id) {
    this.router.navigate(['/virtual-accounts/virtual-account-details/' + id]);
    // if (
    //   this.virtualAccountService.havePermission(
    //     this.roleAccess.viewVirtualAccounts
    //   ) == true
    // ) {
    //   this.router.navigate(['/virtual-accounts/virtual-account-details/' + id]);
    // } else {
    //   this.router.navigate(['/virtual-accounts']);
    // }
  }

  public toInt(num: string) {
    return +num;
  }

  public sortByWordLength = (a: any) => {
    return a.name.length;
  };

  public getDate(regDate: string) {
    const date = new Date(regDate);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: '2-digit',
    });
  }
}
