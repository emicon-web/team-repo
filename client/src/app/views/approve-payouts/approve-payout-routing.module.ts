import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { Role } from '../../models/role';
import { RoleAccess } from '../../models/role-access';
import { DashboardComponent } from '../dashboard/dashboard.component';
import { ApproveApiPayoutListComponent } from './api-payout-list/approve-api-payout.component';
import { ApproveCancelPayoutListComponent } from './cancel-payout-list/cancel-payout-list.component';
import { ApproveInstaPayoutListComponent } from './insta-payout-list/approve-insta-payout.component';
import { ApproveSelfPayoutListComponent } from './self-payout-list/approve-self-payout.component';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Approve Payout',
    },
    children: [
      {
        path: '',
        component: DashboardComponent,
        data: {
          title: 'Dashboard',
          roles: [RoleAccess.viewUsers],
        },
      },
      {
        path: 'insta-payout',
        component: ApproveInstaPayoutListComponent,
        data: {
          title: 'All-Insta-Payout',
          roles: [RoleAccess.viewInstaPayout],
        },
      },
      {
        path: 'api-payout',
        component: ApproveApiPayoutListComponent,
        data: {
          title: 'All-API-Payout',
          roles: [RoleAccess.viewApiPayout],
        },
      },
      {
        path: 'self-payout',
        component: ApproveSelfPayoutListComponent,
        data: {
          title: 'All-Self-Payout',
          roles: [RoleAccess.viewUsers],
        },
      },
      {
        path: 'cancel-payout',
        component: ApproveCancelPayoutListComponent,
        data: {
          title: 'All-Cancel-Payout',
          roles: [RoleAccess.viewCancelPayoutButton],
        },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ApprovePayoutRoutingModule { }
