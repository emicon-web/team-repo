import { Component, OnInit, ViewChild } from '@angular/core';
import { ModalDirective } from 'ngx-bootstrap/modal';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { NotificationService } from '../../../services/notification.service';
import { RoleAccess } from '../../../models/role-access';
import { ApprovePayoutService } from 'src/app/services/approve-payouts.service';
import Swal from 'sweetalert2';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-approve-api-payout',
  templateUrl: './approve-api-payout.component.html',
  styleUrls: ['./approve-api-payout.component.scss'],
})
export class ApproveApiPayoutListComponent implements OnInit {
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
    private approvePayoutService: ApprovePayoutService,
    private spinner: NgxSpinnerService
  ) {}

  ngOnInit(): void {
    this.merchantData = this.userService.userValue;
    this.getAllApiPayoutList();
  }

  getAllApiPayoutList() {
    this.spinner.show();
    this.approvePayoutService
      .getInstaPayouts('CHECKER', { payoutType: 'API' })
      .subscribe(
        (data: any) => {
          this.spinner.hide();
          this.collection = data;
          this.totalItems = 0;
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

  approveInstaPayout(id, status) {
    let options = {
      payoutRoleType: 'CHECKER',
      merchantId: this.merchantData.merchantId,
      payoutid: id,
      action: status,
    };

    Swal.fire({
      // title: 'Are you sure?',
      text: `Are you sure you want to ${status.toLowerCase()} the selected API Payout?`,
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Yes',
    }).then((result) => {
      if (result.isConfirmed) {
        // this.spinner.show();
        this.approvePayoutService.approveApiPayout(options).subscribe(
          (data: any) => {
            // this.spinner.hide();
            const msg = status === 'APPROVE' ? 'approved' : 'rejected';
            this.notificationService.showSuccess(
              `API payout ${msg} successfully`,
              ''
            );
            this.getAllApiPayoutList();
          },
          (err) => {
            // this.spinner.hide();
            const errorMsg = status === 'APPROVE' ? 'approving' : 'rejecting';
            this.notificationService.showError(
              `Error in ${errorMsg} api payout`,
              ''
            );
          }
        );
      }
    });
  }
}
