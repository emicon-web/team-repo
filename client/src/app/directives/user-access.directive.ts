import { Directive, OnInit, TemplateRef, ViewContainerRef, Input } from '@angular/core';
import { RoleAccess } from '../models/role-access';

import { UserService } from '../services/user.service';

@Directive({
  selector: '[appUserAccess]'
})
export class UserAccessDirective implements OnInit {

  snapshot;
  constructor(private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private userService: UserService) {
  }

  userAccess: any;

  @Input()
  set appUserAccess(access: RoleAccess[]) {
    if (!access || !access.length) {
      throw new Error('Access value is empty or missed');
    }

    this.userAccess = access[0];
  }

  ngOnInit() {
    this.havePermission();
  }

  havePermission() {
    let hasAccess = false;
    let token = this.userService.userValue;
    if (token.actions.includes(this.userAccess)) {
      hasAccess = true;
    }

    if (hasAccess) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    } else {
      this.viewContainer.clear();
    }
  }

}
