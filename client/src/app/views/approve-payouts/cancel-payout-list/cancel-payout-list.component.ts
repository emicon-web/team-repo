import { Component, OnInit, ViewChild } from '@angular/core';
import { ModalDirective } from 'ngx-bootstrap/modal';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { NotificationService } from '../../../services/notification.service';
import { RoleAccess } from '../../../models/role-access';
import Swal from 'sweetalert2';
import { CancelPayoutService } from 'src/app/services/cancel-payout.service';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-cancel-payout-list',
  templateUrl: './cancel-payout-list.component.html',
  styleUrls: ['./cancel-payout-list.component.scss'],
})
export class ApproveCancelPayoutListComponent implements OnInit {
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
  headingText = 'Cancel Payout List';
  systemCollection = [];
  issuerCollection = [];
  p: number = 0;
  itemsPerPage = 25;
  totalItems: any;

  deleteUserId;
  showCol: boolean;
  selectedTxnIds: any;
  merchantData: any;

  constructor(
    private userService: UserService,
    private router: Router,
    private notificationService: NotificationService,
    private cancelPayoutService: CancelPayoutService,
    private spinner: NgxSpinnerService
  ) {}

  ngOnInit(): void {
    this.merchantData = this.userService.userValue;
    this.getAllClosePayoutList();
  }

  false;
  getAllClosePayoutList() {
    this.spinner.show();
    // this.collection = [];
    const offset = this.p === 0 ? this.p : this.p - 1;
    this.totalItems = 0;
    this.cancelPayoutService
      .getAllMarkForCancel(
        { merchantid: this.merchantData.merchantId },
        this.itemsPerPage,
        offset
      )
      .subscribe(
        (data: any) => {
          this.spinner.hide();
          if (data.data) {
            this.collection = data.data.data;
            this.totalItems = data.count;
          }
        },
        (error) => {
          this.spinner.hide();
          this.error = error;
        }
      );
  }

  getPage(page) {
    this.p = page;
    this.getAllClosePayoutList();
  }

  itemOnPage(no) {
    this.p = 0;
    this.itemsPerPage = no;
    this.getAllClosePayoutList();
  }

  approveClosePayout(txnId, status) {
    const dataToSend = {
      merchantPayoutId: txnId,
      canceledBy: this.merchantData.userName,
      cancelDate: new Date().getTime().toString(),
      // action: status,
    };

    const confirmMsg =
      status === 'APPROVE'
        ? 'Are you sure you want to cancel the selected payout?'
        : 'Are you sure you want to reject the cancellation of the selected payout?';

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
        this.cancelPayoutService
          .cancelPayout(
            'approvedPayoutCancellation?action=' + status,
            dataToSend
          )
          .subscribe(
            (data: any) => {
              // this.spinner.hide();
              const successMsg =
                status === 'APPROVE'
                  ? 'Selected payouts are successfully closed'
                  : 'Selected payouts for cancellation are rejected successfully';
              this.getAllClosePayoutList();
              this.notificationService.showSuccess(successMsg, '');
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
