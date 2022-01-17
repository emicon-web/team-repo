import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { UsersComponent } from './users.component';
import { UserDetailsComponent } from './user-details/user-details.component';
import { CreateUserComponent } from './create-user/create-user.component';
import { Role } from '../../models/role';
import { RoleAccess } from '../../models/role-access';

const routes: Routes = [
  {
    path: '',
    data: {
      title: 'Users'
    },
    children: [
      {
        path: '',
        redirectTo: 'users',
      },
      {
        path: 'users',
        component: UsersComponent,
        data: {
          title: 'All-Users',
          roles: [RoleAccess.viewUsers]
        }
      },
      {
        path: 'user-details/:id',
        component: UserDetailsComponent,
        data: {
          title: 'user details',
          roles: [RoleAccess.viewUserDetails]
        }
      },
      {
        path: 'add-user',
        component: CreateUserComponent,
        data: {
          title: 'add user',
          roles: [RoleAccess.addUser]
        }
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UsersRoutingModule { }
