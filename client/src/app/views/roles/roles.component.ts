import { Component, OnInit, ViewChild } from '@angular/core';
import { ModalDirective } from 'ngx-bootstrap/modal';
import { Router } from '@angular/router';

import { RolesService } from '../../services/roles.service';
import { UserService } from '../../services/user.service';
import { NotificationService } from '../../services/notification.service';

import { RoleAccess } from '../../models/role-access';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-roles',
  templateUrl: './roles.component.html',
  styleUrls: ['./roles.component.scss'],
})
export class RolesComponent implements OnInit {
  @ViewChild('dangerModal', { static: false })
  public dangerModal: ModalDirective;
  error: any;
  public data;
  public filterQuery = '';

  collection: any;
  p: number;
  itemsPerPage = 10;
  totalItems: any;

  roleAccess = RoleAccess;
  doNotShowDelete;

  constructor(
    private rolesService: RolesService,
    private router: Router,
    private userService: UserService,
    private notificationService: NotificationService,
    private spinner: NgxSpinnerService
  ) {
    this.getAllRoles();
  }

  ngOnInit(): void {}

  getAllRoles() {
    this.spinner.show();
    this.rolesService.getAllRoles().subscribe(
      (data: any) => {
        this.spinner.hide();
        this.data = data;
        this.collection = data;
        this.totalItems = data.totalItems;
      },
      (error) => {
        this.spinner.hide();
        this.error = error;
      }
    );
  }

  getPage(page) {
    this.rolesService.getAllRoles().subscribe(
      (data: any) => {
        this.data = data;
        this.collection = data;
        this.totalItems = data.totalItems;
      },
      (error) => (this.error = error)
    );
  }

  itemOnPage(no) {
    this.itemsPerPage = no;
    this.rolesService.getAllRoles().subscribe(
      (data: any) => {
        this.data = data;
        this.collection = data;
        this.totalItems = data.totalItems;
      },
      (error) => (this.error = error)
    );
  }

  viewDetails(id) {
    if (
      this.userService.havePermission(this.roleAccess.viewRoleDetails) == true
    ) {
      this.router.navigate(['/roles/role-details/' + id]);
    } else {
      this.router.navigate(['/roles']);
    }
  }

  deleteRoleId;
  confirmDelete(id) {
    this.deleteRoleId = id;
    this.dangerModal.show();
  }

  deleteRole() {
    this.rolesService.deleteRole(this.deleteRoleId).subscribe(
      (data: any) => {
        this.getAllRoles();
        this.notificationService.showSuccess(data.message, '');
        this.dangerModal.hide();
      },
      (error) => (this.error = error)
    );
  }

  public toInt(num: string) {
    return +num;
  }

  public sortByWordLength = (a: any) => {
    return a.name.length;
  };

  public getDate(regDate: string) {
    const date = new Date(regDate);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: '2-digit',
    });
  }
}
