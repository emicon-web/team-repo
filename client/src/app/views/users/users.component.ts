import { Component, OnInit, ViewChild } from '@angular/core';
import { ModalDirective } from 'ngx-bootstrap/modal';
import { Router } from '@angular/router';

import { UserService } from '../../services/user.service';

import { NotificationService } from '../../services/notification.service';

import { RoleAccess } from '../../models/role-access';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss'],
})
export class UsersComponent implements OnInit {
  @ViewChild('dangerModal', { static: false })
  public dangerModal: ModalDirective;
  roleAccess = RoleAccess;
  // api response error or data
  error: any;
  public data;

  // for search
  public filterQuery = '';
  public filterQueryEmail = '';
  public filterQueryIssuer = '';

  collection: any;
  btnText = 'View Issuer Users';
  headingText = 'System Users';
  systemCollection = [];
  issuerCollection = [];
  p: number;
  itemsPerPage = 10;
  totalItems: any;

  deleteUserId;
  showCol: boolean;

  constructor(
    private userService: UserService,
    private router: Router,
    private notificationService: NotificationService,
    private spinner: NgxSpinnerService
  ) {
    this.getAllUsers();
  }

  ngOnInit(): void {}

  togl() {
    if (this.btnText == 'View Issuer Users') {
      this.p = 0;
      this.collection = this.issuerCollection;
      this.totalItems = this.collection.length;
      this.btnText = 'View System Users';
      this.headingText = 'Issuer Users';
      this.showCol = true;
    } else {
      this.p = 0;
      this.collection = this.systemCollection;
      this.totalItems = this.collection.length;
      this.headingText = 'System Users';
      this.btnText = 'View Issuer Users';
      this.showCol = false;
    }
  }
  false;
  getAllUsers() {
    this.spinner.show()
    this.collection = [];
    this.totalItems = 0;
    this.userService.getAllUsers().subscribe(
      (data: any) => {
        this.spinner.hide()
        this.distribute(data);
        // this.collection = data;
        // this.totalItems = this.collection.length;
        this.collection = this.systemCollection;

        this.deleteYourself(this.collection);
        this.totalItems = this.collection.length;
      },
      (error) => {
        this.spinner.hide()
        this.error = error
      }
    );
  }

  distribute(data) {
    this.systemCollection = [];
    for (let i = 0; i < data.length; i++) {
      if (data[i].userType == 'ISSUER') {
        this.issuerCollection.push(data[i]);
      } else {
        this.systemCollection.push(data[i]);
      }
    }
  }

  getPage(page) {
    console.log(page);
    // this.userService.getAllUsers()
    //   .subscribe(
    //     (data: any) => {
    //       this.data = data;
    //       this.collection = data;
    //       this.totalItems = data.length;
    //     },
    //     error => this.error = error
    //   );
  }

  itemOnPage(no) {
    this.p = 0;
    this.itemsPerPage = no;
    // this.userService.getAllUsers()
    //   .subscribe(
    //     (data: any) => {
    //       this.data = data;
    //       this.collection = data;
    //       this.totalItems = data.length;
    //     },
    //     error => this.error = error
    //   );
  }

  deleteUser() {
    this.userService.deleteUser(this.deleteUserId).subscribe(
      (data: any) => {
        this.getAllUsers();
        this.notificationService.showSuccess(data.message, '');
        this.dangerModal.hide();
      },
      (error) => (this.error = error)
    );
  }

  viewDetails(id) {
    if (
      this.userService.havePermission(this.roleAccess.viewUserDetails) == true
    ) {
      this.router.navigate(['/users/user-details/' + id]);
    } else {
      this.router.navigate(['/users']);
    }
  }
  confirmDelete(id) {
    this.deleteUserId = id;
    this.dangerModal.show();
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

  doNotShowDelete;
  deleteYourself(data) {
    for (let i = 0; i < data.length; i++) {
      if (data[i].id == this.userService.userValue.id) {
        console.log(data[i].id);
        this.doNotShowDelete = data[i].id;
        // data.splice(i, 1);
      }
    }
  }
}
