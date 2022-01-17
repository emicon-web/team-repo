import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CancelPayoutComponent } from './cancel-payout/cancel-payout.component';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Cancel Payout',
    },
    component: CancelPayoutComponent,
    // children: [
    //   {
    //     path: '',
    //     redirectTo: 'cancel-payouts',
    //   },
    //   {
    //     path: 'cancel',
    //     data: {
    //       title: 'Payout Cancel',
    //       // roles: [RoleAccess.viewVirtualAccounts],
    //     },
    //   },
    // ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CancelPayoutRoutingModule {}
