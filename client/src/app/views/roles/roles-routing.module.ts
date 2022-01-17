import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { RolesComponent } from './roles.component';
import { CreateRoleComponent } from './create-role/create-role.component';
import { RoleDetailsComponent } from './role-details/role-details.component';

import { RoleAccess } from '../../models/role-access';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Roles'
    },
    children: [
      {
        path: '',
        redirectTo: 'roles',
      },
      {
        path: 'roles',
        component: RolesComponent,
        data: {
          title: 'All-Roles',
          roles: [RoleAccess.viewRoles]
        }
      },
      {
        path: 'role-details/:id',
        component: RoleDetailsComponent,
        data: {
          title: 'roles details',
          roles: [RoleAccess.viewRoleDetails]
        }
      },
      {
        path: 'add-role',
        component: CreateRoleComponent,
        data: {
          title: 'add role',
          roles: [RoleAccess.addRoles]
        }
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RolesRoutingModule { }
