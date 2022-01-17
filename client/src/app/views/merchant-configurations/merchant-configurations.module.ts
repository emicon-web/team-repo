import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AlertModule } from 'ngx-bootstrap/alert';
import { ModalModule } from 'ngx-bootstrap/modal';
import { NgxPaginationModule } from 'ngx-pagination';
import { NgSelectModule } from '@ng-select/ng-select';
import { Ng2TelInputModule } from 'ng2-tel-input';

import { MerchantConfigurationRoutingModule } from './merchant-configurations-routing.module';
import { ViewConfigurationDetailsComponent } from './view-configuration-details/view-configuration-details.component';

import { SharedModule } from '../../shared.module';
import { EditConfigurationComponent } from './edit-configuration/edit-configuration.component';
import { NgxSpinnerModule } from 'ngx-spinner';

@NgModule({
  declarations: [ViewConfigurationDetailsComponent, EditConfigurationComponent],
  imports: [
    MerchantConfigurationRoutingModule,
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
export class MerchantConfigurationModule {}
