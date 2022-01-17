import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AlertModule } from 'ngx-bootstrap/alert';
import { ModalModule } from 'ngx-bootstrap/modal';
import { NgxPaginationModule } from 'ngx-pagination';
import { NgSelectModule } from '@ng-select/ng-select';
import { Ng2TelInputModule } from 'ng2-tel-input';

import { CancelPayoutRoutingModule } from './cancel-payout-routing.module';
import { CancelPayoutComponent } from './cancel-payout/cancel-payout.component';
import { SharedModule } from 'src/app/shared.module';
import { NgxSpinnerModule } from 'ngx-spinner';

@NgModule({
  declarations: [CancelPayoutComponent],
  imports: [
    CancelPayoutRoutingModule,
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
export class CancelPayoutModule {}
