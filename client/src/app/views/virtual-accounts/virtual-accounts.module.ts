import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AlertModule } from 'ngx-bootstrap/alert';
import { ModalModule } from 'ngx-bootstrap/modal';
import { NgxPaginationModule } from 'ngx-pagination';
import { NgSelectModule } from '@ng-select/ng-select';
import { Ng2TelInputModule } from 'ng2-tel-input';

import { VirtualAccountRoutingModule } from './virtual-accounts-routing.module';
import { VirtualAccountComponent } from './virtual-accounts.component';

import { SharedModule } from '../../shared.module';
import { CreateVirtualAccountComponent } from './create-virtual-account/create-virtual-account.component';
import { VirtualAccountDetailsComponent } from './virtual-account-details/virtual-account-details.component';
import { FileUploadListComponent } from './file-upload-listing/file-upload-list.component';
import { InstaPayoutComponent } from './insta-payout/insta-payout.component';
import { SelfPayoutComponent } from './self-payout/self-payout.component';
import { ClosedVirtualAccountListComponent } from './closed-virtual-accounts/closed-virtual-account-list.component';
import { FileUploadComponent } from './file-upload/file-upload.component';
import { NgxSpinnerModule } from 'ngx-spinner';
import { AccountStatementComponent } from './account-statement/account-statement.component';
import { FileUploadHistoryComponent } from './file-history/file-history.component';

@NgModule({
  declarations: [
    VirtualAccountComponent,
    CreateVirtualAccountComponent,
    VirtualAccountDetailsComponent,
    FileUploadListComponent,
    InstaPayoutComponent,
    SelfPayoutComponent,
    ClosedVirtualAccountListComponent,
    FileUploadComponent,
    AccountStatementComponent,
    FileUploadHistoryComponent,
  ],
  imports: [
    VirtualAccountRoutingModule,
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
export class VirtualAccountModule { }
