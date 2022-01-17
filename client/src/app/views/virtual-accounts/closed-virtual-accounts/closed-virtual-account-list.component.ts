import { Component, OnInit, ViewChild } from '@angular/core';
import { ModalDirective } from 'ngx-bootstrap/modal';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { NotificationService } from '../../../services/notification.service';
import { RoleAccess } from '../../../models/role-access';
import { VirtualAccountService } from 'src/app/services/payout-account.service';
import Swal from 'sweetalert2';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-closed-virtual-account-list',
  templateUrl: './closed-virtual-account-list.component.html',
  styleUrls: ['./closed-virtual-account-list.component.scss'],
})
export class ClosedVirtualAccountListComponent implements OnInit {
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

  collection: any = [];
  headingText = 'Approval List';
  systemCollection = [];
  issuerCollection = [];
  p: number;
  itemsPerPage = 10;
  totalItems: any;

  deleteUserId;
  showCol: boolean;
  merchantData: any;

  constructor(
    private userService: UserService,
    private virtualAccountService: VirtualAccountService,
    private router: Router,
    private notificationService: NotificationService,
    private spinner: NgxSpinnerService
  ) {
    this.getAllCloseVA();
  }

  ngOnInit(): void {
    this.merchantData = this.userService.userValue;
  }

  false;
  getAllCloseVA() {
    this.spinner.show();
    // this.collection = [];
    this.totalItems = 0;
    let data = {
      merchantid: this.userService.userValue.merchantId,
    };
    this.virtualAccountService.getAllClosedPA(data).subscribe(
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

  getPage(page) {}

  itemOnPage(no) {
    this.p = 0;
    this.itemsPerPage = no;
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

  approveClosePA(id, status) {
    const data = {
      virtualAccountID: id,
      approvedBy: this.merchantData.userName,
    };

    const confirmMsg =
      status === 'APPROVE'
        ? 'Are you sure you want to close this payout account?'
        : 'Are you sure you want to reject the closing of the selected payout account?';

    Swal.fire({
      // title: 'Are you sure?',
      text: confirmMsg,
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Yes',
    }).then((result) => {
      if (result.isConfirmed) {
        // this.spinner.show();
        this.virtualAccountService.approvePA(data, status).subscribe(
          (data: any) => {
            const successMsg =
              status === 'APPROVE'
                ? 'Selected payout account marked as closed successfully'
                : 'Selected payout account for closing is rejected successfully';

            this.notificationService.showSuccess(successMsg, '');
            // this.spinner.hide();
            this.getAllCloseVA();
          },
          (error) => {
            // this.spinner.hide();
            this.error = error;
          }
        );
      }
    });
  }
}
