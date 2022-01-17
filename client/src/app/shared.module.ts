import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UserAccessDirective } from './directives/user-access.directive';
import { SearchPipe } from './pipes/search.pipe';
import { IsDecimalDirective } from './directives/decimal-number.directive';

@NgModule({
  declarations: [UserAccessDirective, SearchPipe, IsDecimalDirective],
  imports: [CommonModule],
  exports: [UserAccessDirective, SearchPipe, IsDecimalDirective],
})
export class SharedModule {}
