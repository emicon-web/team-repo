import { Component, OnInit } from '@angular/core';

import { UserService } from '../../services/user.service';
import { RoleAccess } from '../../models/role-access';
import { PreviousRouteService } from '../../services/previous-route.service';
import { VirtualAccountService } from '../../services/payout-account.service';
import { navItems } from '../../_nav';

@Component({
  selector: 'app-default-layout',
  templateUrl: './default-layout.component.html',
  styles: [
  ]
})
export class DefaultLayoutComponent implements OnInit {
  roleAccess = RoleAccess;
  userName;
  userRole;
  public sidebarMinimized = false;
  public navItems = navItems;

  constructor(private userService: UserService,
    private previousRouteService: PreviousRouteService,
    private virtualAccountService: VirtualAccountService) { }

  ngOnInit() {
    if (this.previousRouteService.getPreviousUrl() == "/login") {
      location.reload(true);
    }
    this.userName = this.userService.userValue.userName;
    this.userRole = this.userService.userValue.roles[0];
    if (this.userService.havePermission(this.roleAccess.getVirtualAccounts) == true) {
      this.getPayoutAccounts();
    } else {
      this.filterNavbar();
    }

  }

  collection;
  totalItems;
  error;
  getPayoutAccounts() {
    this.collection = [];
    this.totalItems = 0;
    this.virtualAccountService.getAllVirtualAccounts().subscribe(
      (data: any) => {
        this.collection = data;
        this.totalItems = this.collection.length;
        this.filterNavbar();
      },
      (error) => (this.error = error)
    );
  }

  token;
  showNav = false;
  filterNavbar() {
    this.token = this.userService.userValue;

    for (let i = 0; i < navItems.length; i++) {

      if (!this.checkPermission(this.token.actions, navItems[i].permissions)) {
        navItems[i] = {};
      }
      // if (this.navItems[i].name == "Payout Accounts") {
      //   if (this.collection.length > 0) {
      //     this.navItems[i].children;
      //     let abc = [];
      //     for (let i = 0; i < this.collection.length; i++) {
      //       abc.push({
      //         name: this.collection[i].virtualAccountID,
      //         url: '/virtual-accounts',
      //         icon: 'fa fa-server',
      //         permissions: [RoleAccess.viewVirtualAccounts],
      //         children: [
      //           {
      //             name: "Close",
      //             url: '/virtual-accounts',
      //             icon: 'fa fa-trash',
      //             permissions: [RoleAccess.editVirtualAccount],
      //           },
      //         ]
      //       })
      //     }
      //     this.navItems[i].children = abc;
      //   }
      // }
      if (navItems[i]?.children) {
        for (let j = 0; j < navItems[i]?.children?.length; j++) {
          if (!this.checkPermission(this.token.actions, navItems[i]?.children[j]?.permissions)) {
            navItems[i].children[j] = {};
          }
        }
      }
      if (i == (navItems.length - 1)) {
        this.showNav = true;
      }
    }
  }

  checkPermission(from, inn) {
    let flag = false;
    for (let i = 0; i < from.length; i++) {
      for (let j = 0; j < inn.length; j++) {
        if (from[i] == inn[j]) {
          flag = true;
        }
      }
      if (i == (from.length - 1)) {
        return flag;
      }
    }
  }

  toggleMinimize(e) {
    this.sidebarMinimized = e;
  }

  logout() {
    this.userService.logout();
  }

}
