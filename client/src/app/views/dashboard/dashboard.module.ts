import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { ButtonsModule } from 'ngx-bootstrap/buttons';

import { DashboardRoutingModule } from './dashboard-routing.module';
import { DashboardComponent } from './dashboard.component';
import { SharedModule } from '../../shared.module';
import { NgxSpinnerModule } from 'ngx-spinner';
import { NgxChartsModule } from '@swimlane/ngx-charts';

@NgModule({
  declarations: [DashboardComponent],
  imports: [
    CommonModule,
    FormsModule,
    DashboardRoutingModule,
    BsDropdownModule,
    ButtonsModule.forRoot(),
    SharedModule,
    NgxSpinnerModule,
    NgxChartsModule,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class DashboardModule {}
