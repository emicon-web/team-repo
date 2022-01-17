import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { ViewConfigurationDetailsComponent } from './view-configuration-details/view-configuration-details.component';
import { Role } from '../../models/role';
import { RoleAccess } from '../../models/role-access';
import { EditConfigurationComponent } from './edit-configuration/edit-configuration.component';

const routes: Routes = [
  {
    path: '',
    component: ViewConfigurationDetailsComponent,
    data: {
      title: 'Configurations',
    },
    children: [
      {
        path: 'view',
        component: ViewConfigurationDetailsComponent,
        data: {
          title: 'View Configuration',
          roles: [RoleAccess.viewUsers],
        },
      },
      {
        path: 'edit',
        component: EditConfigurationComponent,
        data: {
          title: 'Edit Configuration',
          roles: [RoleAccess.viewUsers],
        },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class MerchantConfigurationRoutingModule {}
