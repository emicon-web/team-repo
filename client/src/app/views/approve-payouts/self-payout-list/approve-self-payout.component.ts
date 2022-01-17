import { Component, OnInit, ViewChild } from '@angular/core';
import { ModalDirective } from 'ngx-bootstrap/modal';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { NotificationService } from '../../../services/notification.service';
import { RoleAccess } from '../../../models/role-access';
import { NgxSpinnerService } from 'ngx-spinner';
import { ApprovePayoutService } from 'src/app/services/approve-payouts.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-approve-self-payout',
  templateUrl: './approve-self-payout.component.html',
  styleUrls: ['./approve-self-payout.component.scss'],
})
export class ApproveSelfPayoutListComponent implements OnInit {
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
    private router: Router,
    private notificationService: NotificationService,
    private spinner: NgxSpinnerService,
    private approvePayoutService: ApprovePayoutService
  ) {
    this.getAllSelfPayoutList();
  }

  ngOnInit(): void {
    this.merchantData = this.userService.userValue;
  }

  getAllSelfPayoutList() {
    this.spinner.show();
    this.approvePayoutService
      .getSelfPayouts('CHECKER', { payoutType: 'SELFPAY' })
      .subscribe(
        (data: any) => {
          this.spinner.hide();
          this.collection = data;
          this.totalItems = this.collection.length;
        },
        (err) => {
          this.spinner.hide();
          console.log(err);
        }
      );
  }

  getPage(page) {}

  itemOnPage(no) {
    this.p = 0;
    this.itemsPerPage = no;
  }

  approveSelfPayout(id, status) {
    let options = {
      payoutRoleType: 'CHECKER',
      merchantId: this.merchantData.merchantId,
      payoutid: id,
      action: status,
    };

    Swal.fire({
      // title: 'Are you sure?',
      text: `Are you sure you want to ${status.toLowerCase()} this Self Payout?`,
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Yes',
    }).then((result) => {
      if (result.isConfirmed) {
        this.approvePayoutService.approveSelfPayout(options).subscribe(
          (data: any) => {
            const msg = status === 'APPROVE' ? 'approved' : 'rejected';
            this.notificationService.showSuccess(
              `Payout ${msg} successfully`,
              ''
            );
            this.getAllSelfPayoutList();
          },
          (err) => {
            const errorMsg = status === 'APPROVE' ? 'approving' : 'rejecting';
            this.notificationService.showError(
              `Error in ${errorMsg} self payout`,
              ''
            );
            console.log(err);
          }
        );
      }
    });
  }
}
