import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

// Import Containers
import { DefaultLayoutComponent } from './containers';
import { AuthGuard } from './guards/auth.guard';

import { P404Component } from './views/error/p404.component';
import { P500Component } from './views/error/p500.component';
import { LoginComponent } from './views/login/login.component';
import { ForgotPasswordComponent } from './views/forgot-password/forgot-password.component';
import { GeneratePasswordComponent } from './views/generate-password/generate-password.component';
import { Role } from './models';
import { RoleAccess } from './models';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  },
  {
    path: '404',
    component: P404Component,
    data: {
      title: 'Page 404',
    },
  },
  {
    path: '500',
    component: P500Component,
    data: {
      title: 'Page 500',
    },
  },
  {
    path: 'login',
    component: LoginComponent,
    data: {
      title: 'Login Page',
    },
  },
  {
    path: 'forgot-password',
    component: ForgotPasswordComponent,
    data: {
      title: 'Forgot Password Page',
    },
  },
  {
    path: 'generate-password/:token',
    component: GeneratePasswordComponent,
    data: {
      title: 'Generate Password Page',
    },
  },
  {
    path: '',
    component: DefaultLayoutComponent,
    canActivate: [AuthGuard],
    // canActivateChild: [AuthGuard],
    children: [
      {
        path: 'dashboard',
        loadChildren: () =>
          import('./views/dashboard/dashboard.module').then(
            (m) => m.DashboardModule
          ),
        // data: { roles: [RoleAccess.viewUsers, RoleAccess.viewRoles] },
      },
      {
        path: 'users',
        loadChildren: () =>
          import('./views/users/users.module').then((m) => m.UsersModule),
        data: { roles: [RoleAccess.viewUsers] },
      },
      {
        path: 'roles',
        loadChildren: () =>
          import('./views/roles/roles.module').then((m) => m.RolesModule),
        data: { roles: [RoleAccess.viewRoles] },
      },
      {
        path: 'virtual-accounts',
        loadChildren: () =>
          import('./views/virtual-accounts/virtual-accounts.module').then(
            (m) => m.VirtualAccountModule
          ),
        data: { roles: [RoleAccess.viewVirtualAccounts] },
      },
      {
        path: 'approve',
        loadChildren: () =>
          import('./views/approve-payouts/approve-payout.module').then(
            (m) => m.ApprovePayoutModule
          ),
        data: { roles: [RoleAccess.viewVirtualAccounts] },
      },
      {
        path: 'configuration',
        loadChildren: () =>
          import(
            './views/merchant-configurations/merchant-configurations.module'
          ).then((m) => m.MerchantConfigurationModule),
        data: { roles: [RoleAccess.viewVirtualAccounts] },
      },
      {
        path: 'cancel-payouts',
        loadChildren: () =>
          import('./views/cancel-payout/cancel-payout.module').then(
            (m) => m.CancelPayoutModule
          ),
        data: { roles: [RoleAccess.viewVirtualAccounts] },
      },
      {
        path: 'reports',
        loadChildren: () =>
          import('./views/reports/report.module').then((m) => m.ReportModule),
        data: { roles: [RoleAccess.viewVirtualAccounts] },
      },
    ],
  },
  { path: '**', component: P404Component },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule { }
