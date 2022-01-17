import { Component, OnInit, ViewChild } from '@angular/core';
import { ModalDirective } from 'ngx-bootstrap/modal';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { NotificationService } from '../../../services/notification.service';
import { RoleAccess } from '../../../models/role-access';
import { VirtualAccountService } from 'src/app/services/payout-account.service';
import Swal from 'sweetalert2';
import { BsDatepickerConfig } from 'ngx-bootstrap/datepicker';
import { FormBuilder, Validators } from '@angular/forms';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-account-statement',
  templateUrl: './account-statement.component.html',
  styleUrls: ['./account-statement.component.scss'],
})
export class AccountStatementComponent implements OnInit {
  @ViewChild('dangerModal', { static: false })
  public dangerModal: ModalDirective;
  roleAccess = RoleAccess;
  // api response error or data
  error: any;
  public data;
  errorAccountId: boolean = false;

  collection: any;
  headingText = 'Account Statement';
  systemCollection = [];
  issuerCollection = [];
  p: number;
  itemsPerPage = 25;
  totalItems: any;
  showCol: boolean;
  merchantData: any;
  virtualAccounts: any = [];
  virtualId = '';
  selectedType = 'today';
  idError: boolean = false;
  statementData: any = [];

  constructor(
    private userService: UserService,
    private virtualAccountService: VirtualAccountService,
    private router: Router,
    private notificationService: NotificationService,
    private spinner: NgxSpinnerService
  ) {
    this.getAllVirtualAccount();
  }

  ngOnInit(): void { }

  getAllVirtualAccount() {
    this.spinner.show();
    this.collection = [];
    this.virtualAccountService.getAllVirtualAccounts().subscribe(
      (data: any) => {
        this.spinner.hide();
        this.virtualAccounts = data;
      },
      (error) => {
        this.spinner.hide();
        this.error = error;
      }
    );
  }

  getPage(page) { }

  itemOnPage(no) {
    this.p = 0;
    this.itemsPerPage = no;
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
  onTypeChange(evt) {
    this.selectedType = evt.target.value;
  }

  fetchRecords() {
    this.statementData = [];
    this.spinner.show();
    this.idError = false;
    if (this.virtualId === '') {
      this.spinner.hide();
      this.idError = true;
      return;
    }

    const day =
      this.selectedType === 'today' ? 1 : this.selectedType === 'week' ? 7 : 30;
    this.virtualAccountService.accountStatement(this.virtualId, day).subscribe(
      (data: any) => {
        this.spinner.hide();
        this.statementData = data;
      },
      (error) => {
        this.spinner.hide();
        this.error = error;
        this.statementData = [];
      }
    );
  }
}
