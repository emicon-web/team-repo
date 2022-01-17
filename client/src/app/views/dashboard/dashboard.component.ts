import { Component, OnInit, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { BsDropdownConfig } from 'ngx-bootstrap/dropdown';
import { VirtualAccountService } from 'src/app/services/payout-account.service';
import { UserService } from '../../services/user.service';
import { ChartType, ChartOptions } from 'chart.js';
import {
  SingleDataSet,
  Label,
  monkeyPatchChartJsLegend,
  monkeyPatchChartJsTooltip,
} from 'ng2-charts';
import { RoleAccess } from '../../models/role-access';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  providers: [
    {
      provide: BsDropdownConfig,
      useValue: { isAnimated: true, autoClose: true },
    },
  ],
})
export class DashboardComponent implements OnInit {
  roleAccess = RoleAccess;
  userResponse;
  collection: any[];
  error: any;
  virtualId = '';
  pendingCountData: any;
  totalCount: any = 0;
  merchantData: any;
  formatSelected = 'week';
  accountData: any = [];
  screenHeight: number;
  screenWidth: number;

  // options
  view: any[];
  gradient: boolean = true;
  showLegend: boolean = true;
  legendPosition: string = 'top';
  payoutData: any;
  legendTitlePayout = "Payout By Mode";
  labels: boolean = false;
  legendTitleStatus = "Payout By Status";
  payoutTypeData = [];

  constructor(
    private userService: UserService,
    private router: Router,
    private virtualAccountService: VirtualAccountService,
    private spinner: NgxSpinnerService
  ) {
    this.onResizee();
  }

  ngOnInit(): void {
    this.merchantData = this.userService.userValue;
    if (this.userService.havePermission(this.roleAccess.getVirtualAccounts) == true) {
      this.getAllVirtualAccount();
    }
    this.getPendingCount();
  }

  @HostListener('window:resize', ['$event'])
  onResize(event?) {
    this.screenHeight = window.innerHeight;
    this.screenWidth = window.innerWidth;
    if (this.screenWidth <= 425) {
      this.view = [250, 300];
      this.labels = false;
    } else {
      this.labels = false;
      this.view = [this.screenWidth / 4.1, 300];
    }
  }

  onResizee() {
    this.screenHeight = window.innerHeight;
    this.screenWidth = window.innerWidth;
    if (this.screenWidth <= 425) {
      this.labels = false;
      this.view = [250, 300];
    } else {
      this.view = [this.screenWidth / 4.1, 300];
    }
  }

  navigate(route) {
    this.router.navigate(['/approve/' + route]);
  }

  navigateToCVA(route) {
    this.router.navigate(['/virtual-accounts/' + route]);
  }

  getAllVirtualAccount() {
    this.spinner.show();
    this.collection = [];
    this.virtualAccountService.getAllVirtualAccounts().subscribe(
      (data: any) => {
        this.spinner.hide();
        this.collection = data;
        if (this.collection.length) {
          this.virtualId = this.collection[0].virtualAccountID;
          this.getBalanceData(this.collection[0].virtualAccountID, 'radio');
        }
      },
      (error) => {
        this.spinner.hide();
        this.error = error;
      }
    );
  }

  getPendingCount() {
    this.virtualAccountService
      .getPendingCount({ merchantid: this.merchantData.merchantId })
      .subscribe(
        (data: any) => {
          this.pendingCountData = data;
          this.totalCount =
            this.pendingCountData.accountCloseCount +
            this.pendingCountData.cancelPayoutCount +
            this.pendingCountData.instaPayoutsCount +
            this.pendingCountData.uploadfileapprovalCount;
        },
        (error) => (this.error = error)
      );
  }

  onTypeChange(evt) {
    this.formatSelected = evt.target.value;
    this.getBalanceData(this.virtualId, 'radio');
  }

  getBalanceData(evt, type = '') {
    this.spinner.show();
    this.error = false;

    if (this.virtualId === '') {
      this.spinner.hide();
      this.error = true;
      return;
    }

    this.virtualId = type === 'radio' ? evt : evt.target.value;

    const dataForStatusChart = {
      merchantId: this.merchantData.merchantId,
      accountId: this.virtualId,
      lastNDays:
        this.formatSelected === 'today'
          ? 1
          : this.formatSelected === 'week'
            ? 7
            : 30,
      type: 'PAYOUT_STATUS',
    };

    const dataForPayoutTypeChart = {
      merchantId: this.merchantData.merchantId,
      accountId: this.virtualId,
      lastNDays:
        this.formatSelected === 'today'
          ? 1
          : this.formatSelected === 'week'
            ? 7
            : 30,
      type: 'PAYOUT_PAYMENT_MODE',
    };

    this.getDataForStatusChart(dataForStatusChart);
    this.getDataForPayoutTypeChart(dataForPayoutTypeChart);
  }

  getDataForStatusChart(data) {
    this.virtualAccountService.getDashboardBalance(data).subscribe(
      (res: any) => {
        this.accountData = [];
        this.spinner.hide();
        this.payoutData = res;
        if (res.data.length) {
          for (const account of res.data) {
            this.accountData.push({
              "name": account.payoutStatus + ' - ' + account.percentage,
              "value": account.recordCount,
              "extra": {
                "amount": account.totalAmount
              }
            });
          }
        }
      },
      (error) => {
        this.spinner.hide();
        this.error = error;
      }
    );
  }

  getDataForPayoutTypeChart(data) {
    this.virtualAccountService.getDashboardBalance(data).subscribe(
      (res: any) => {
        this.payoutTypeData = [];
        this.spinner.hide();
        this.payoutData = res;
        if (res.data.length) {
          for (const account of res.data) {
            if (account.payoutPaymentMode != null) {
              this.payoutTypeData.push({
                "name": account.payoutPaymentMode + ' - ' + account.percentage,
                "value": account.recordCount,
                "extra": {
                  "amount": account.totalAmount
                }
              });
            }
          }
        }
      },
      (error) => {
        this.spinner.hide();
        this.error = error;
      }
    );
  }

  pieChartLabel(series: any[], name: string): string {
    const item = series.filter((data) => data.name === name);
    if (item.length > 0) {
      return item[0].label;
    }
    return name;
  }

  onPieSliceSelect(evt) {
    console.log(evt);
  }
}
