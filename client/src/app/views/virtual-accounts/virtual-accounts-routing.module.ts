import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { VirtualAccountComponent } from './virtual-accounts.component';
import { Role } from '../../models/role';
import { RoleAccess } from '../../models/role-access';
import { CreateVirtualAccountComponent } from './create-virtual-account/create-virtual-account.component';
import { VirtualAccountDetailsComponent } from './virtual-account-details/virtual-account-details.component';
import { FileUploadListComponent } from './file-upload-listing/file-upload-list.component';
import { InstaPayoutComponent } from './insta-payout/insta-payout.component';
import { SelfPayoutComponent } from './self-payout/self-payout.component';
import { ClosedVirtualAccountListComponent } from './closed-virtual-accounts/closed-virtual-account-list.component';
import { FileUploadComponent } from './file-upload/file-upload.component';
import { AccountStatementComponent } from './account-statement/account-statement.component';
import { FileUploadHistoryComponent } from './file-history/file-history.component';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Payout Accounts',
    },
    children: [
      {
        path: '',
        redirectTo: 'virtual-accounts',
      },
      {
        path: 'virtual-accounts',
        component: VirtualAccountComponent,
        data: {
          title: 'All-Payout-Accounts',
          roles: [RoleAccess.viewVirtualAccounts],
        },
      },
      {
        path: 'virtual-account-details/:id',
        component: VirtualAccountDetailsComponent,
        data: {
          title: 'payout account details',
          roles: [RoleAccess.viewVirtualAccounts],
        },
      },
      {
        path: 'add-virtual-account',
        component: CreateVirtualAccountComponent,
        data: {
          title: 'add payout account',
          roles: [RoleAccess.addVirtualAccount],
        },
      },
      {
        path: 'files',
        component: FileUploadListComponent,
        data: {
          title: 'view uploaded files',
          roles: [RoleAccess.viewPendinggApprovalFile],
        },
      },
      {
        path: 'add-insta-payout',
        component: InstaPayoutComponent,
        data: {
          title: 'Insta Payout',
          roles: [RoleAccess.addInstaPayout],
        },
      },
      {
        path: 'add-self-payout',
        component: SelfPayoutComponent,
        data: {
          title: 'Self Payout',
          roles: [RoleAccess.addInstaPayout],
        },
      },
      {
        path: 'virtual-account-details/:id/self-payout',
        component: SelfPayoutComponent,
        data: {
          title: 'Self Payout',
          // roles: [RoleAccess.viewVirtualAccounts],
        },
      },
      {
        path: 'closed-virtual-accounts',
        component: ClosedVirtualAccountListComponent,
        data: {
          title: 'Closed-Payout-Accounts',
          roles: [RoleAccess.viewAccountClosure],
        },
      },
      {
        path: 'file-upload',
        component: FileUploadComponent,
        data: {
          title: 'Payout-Account-File-Upload',
          // roles: [RoleAccess.viewVirtualAccounts],
        },
      },
      {
        path: 'statement',
        component: AccountStatementComponent,
        data: {
          title: 'Payout-Account-Statement',
          roles: [RoleAccess.viewAccountStatement],
        },
      },
      {
        path: 'files-history',
        component: FileUploadHistoryComponent,
        data: {
          title: 'Payout-Account-Files-History',
          roles: [RoleAccess.viewPayoutAccountHistory],
        },
      },
      {
        path: 'files-history',
        component: FileUploadHistoryComponent,
        data: {
          title: 'Payout-Account-Files-History',
          // roles: [RoleAccess.viewVirtualAccounts],
        },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class VirtualAccountRoutingModule {}
