import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';


import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AlertModule } from 'ngx-bootstrap/alert';
import { ModalModule } from 'ngx-bootstrap/modal';
import { NgxPaginationModule } from 'ngx-pagination';
import { NgSelectModule } from '@ng-select/ng-select';
import { Ng2TelInputModule } from 'ng2-tel-input';

import { UsersRoutingModule } from './users-routing.module';
import { UsersComponent } from './users.component';
import { UserDetailsComponent } from './user-details/user-details.component';
import { CreateUserComponent } from './create-user/create-user.component';
import { DataFilterPipe } from './datafilterpipe';
import { DataFilterEmailPipe } from './datafilteremailpipe';
import { EditUserComponent } from './edit-user/edit-user.component';

import { SharedModule } from '../../shared.module';
import { DataFilterIssuerPipe } from './datafilterissuerpipe';
import { NgxSpinnerModule } from 'ngx-spinner';

@NgModule({
  declarations: [
    UsersComponent,
    DataFilterPipe,
    DataFilterEmailPipe,
    UserDetailsComponent,
    CreateUserComponent,
    EditUserComponent,
    DataFilterIssuerPipe,
  ],
  imports: [
    UsersRoutingModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    AlertModule,
    ModalModule.forRoot(),
    NgxPaginationModule,
    SharedModule,
    NgSelectModule,
    Ng2TelInputModule,
    NgxSpinnerModule,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class UsersModule {}
