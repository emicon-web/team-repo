import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AlertModule } from 'ngx-bootstrap/alert';
import { ModalModule } from 'ngx-bootstrap/modal';
import { NgxPaginationModule } from 'ngx-pagination';
import { NgSelectModule } from '@ng-select/ng-select';

import { RolesRoutingModule } from './roles-routing.module';
import { SharedModule } from '../../shared.module';
import { DataFilterPipe } from './datafilterpipe';
import { RolesComponent } from './roles.component';
import { CreateRoleComponent } from './create-role/create-role.component';
import { EditRoleComponent } from './edit-role/edit-role.component';
import { RoleDetailsComponent } from './role-details/role-details.component';
import { NgxSpinnerModule } from 'ngx-spinner';


@NgModule({
  declarations: [RolesComponent, DataFilterPipe, CreateRoleComponent, EditRoleComponent, RoleDetailsComponent],
  imports: [
    CommonModule,
    RolesRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    NgxPaginationModule,
    AlertModule,
    ModalModule.forRoot(),
    NgSelectModule,
    SharedModule,
    NgxSpinnerModule,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class RolesModule { }
