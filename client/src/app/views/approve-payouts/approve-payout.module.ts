import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AlertModule } from 'ngx-bootstrap/alert';
import { ModalModule } from 'ngx-bootstrap/modal';
import { NgxPaginationModule } from 'ngx-pagination';
import { NgSelectModule } from '@ng-select/ng-select';
import { Ng2TelInputModule } from 'ng2-tel-input';

import { ApprovePayoutRoutingModule } from './approve-payout-routing.module';
import { ApproveInstaPayoutListComponent } from './insta-payout-list/approve-insta-payout.component';
import { DataFilterPipe } from './datafilterpipe';

import { SharedModule } from '../../shared.module';
import { ApproveSelfPayoutListComponent } from './self-payout-list/approve-self-payout.component';
import { ApproveCancelPayoutListComponent } from './cancel-payout-list/cancel-payout-list.component';
import { NgxSpinnerModule } from 'ngx-spinner';
import { ApproveApiPayoutListComponent } from './api-payout-list/approve-api-payout.component';

@NgModule({
  declarations: [
    ApproveInstaPayoutListComponent,
    ApproveSelfPayoutListComponent,
    DataFilterPipe,
    ApproveCancelPayoutListComponent,
    ApproveApiPayoutListComponent
  ],
  imports: [
    ApprovePayoutRoutingModule,
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
export class ApprovePayoutModule {}
