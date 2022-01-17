import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TransactionalReportComponent } from './transactional-report/transactional-report.component';
import { RoleAccess } from '../../models/role-access';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Transactional-report',
      roles: [RoleAccess.viewReports],
    },
    component: TransactionalReportComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ReportRoutingModule { }
