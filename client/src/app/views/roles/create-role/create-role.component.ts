import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {
  FormGroup,
  FormControl,
  FormBuilder,
  Validators,
  FormArray,
} from '@angular/forms';

import { NotificationService } from '../../../services/notification.service';
import { UserService } from '../../../services/user.service';
import { RolesService } from '../../../services/roles.service';
import { roleAccess } from '../../../models/action-list';
import { Role } from '../../../models/role';
import { NgxSpinnerService } from 'ngx-spinner';
@Component({
  selector: 'app-create-role',
  templateUrl: './create-role.component.html',
  styleUrls: ['./create-role.component.scss'],
})
export class CreateRoleComponent implements OnInit {
  roleActions;

  roleForm: FormGroup;
  formSubmitted = false;
  categories = [];
  actions = [];

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private notificationService: NotificationService,
    private userService: UserService,
    private roleService: RolesService,
    private spinner: NgxSpinnerService
  ) {}
  loading = false;
  ngOnInit(): void {
    this.roleActions = roleAccess;
    this.selectRoles();
    // this.createForm();
  }
  // roleActions = [
  //   {
  //     "id": 1,
  //     "action": "ADD_USER"
  //   },
  //   {
  //     "id": 2,
  //     "action": "DELETE_USER"
  //   },
  //   {
  //     "id": 3,
  //     "action": "READ_USER"
  //   },
  //   {
  //     "id": 4,
  //     "action": "UPDATE_USER"
  //   },
  //   {
  //     "id": 5,
  //     "action": "USER_DETAIL"
  //   },
  //   {
  //     "id": 6,
  //     "action": "ADD_ROLE"
  //   },
  //   {
  //     "id": 7,
  //     "action": "ROLE_DETAIL"
  //   },
  //   {
  //     "id": 8,
  //     "action": "UPDATE_ROLE"
  //   },
  //   {
  //     "id": 9,
  //     "action": "DELETE_ROLE"
  //   },
  //   {
  //     "id": 10,
  //     "action": "READ_ROLE"
  //   },
  //   {
  //     "id": 14,
  //     "action": "VIEW_TOKENS"
  //   },
  //   {
  //     "id": 15,
  //     "action": "CHANGE_STATE"
  //   },
  //   {
  //     "id": 16,
  //     "action": "VIEW_TRANSACTIONS"
  //   },
  //   {
  //     "id": 17,
  //     "action": "VIEW_AUDIT_TRAIL"
  //   }
  // ]
  selectRoles() {
    // for (const key of Object.values(Role)) {
    //   this.categories.push({ 'name': key });
    // }
    for (let i = 0; i < this.roleActions.length; i++) {
      console.log(this.roleActions);
      this.categories.push({
        id: this.roleActions[i].id,
        action: this.roleActions[i].action,
      });
      if (i == this.roleActions.length - 1) {
        this.createForm();
        this.loading = true;
      }
    }
    // this.categories = this.roleActions
    // this.role = [
    //   { name: 'ROLE_ADMIN' }
    // ];
  }

  get f() {
    return this.roleForm.controls;
  }

  createForm() {
    this.roleForm = this.formBuilder.group({
      // roleId: ['', Validators.required],
      name: ['', Validators.required],
      description: ['', Validators.required],
      roleCreatedBy: [this.userService.userValue.id],
      merchantId: [this.userService.userValue.merchantId],
      actions: [[]],
    });
  }

  addRole() {
    this.spinner.show();
    this.formSubmitted = true;
    if (this.roleForm.invalid) {
      this.spinner.hide();
      return;
    }
    this.userService.addRole(this.roleForm.value).subscribe(
      (data: any) => {
        this.spinner.hide();
        this.notificationService.showSuccess(data.message, '');
        this.router.navigate([`/roles`]);
      },
      (error) => {
        this.spinner.hide();
      }
    );
  }

  selectActions(event, val) {
    if (event) {
      this.actions.push(val);
    } else {
      const itemIndex = this.actions.indexOf(val);
      if (itemIndex != -1) this.actions.splice(itemIndex, 1);
    }
    this.roleForm.patchValue({ actions: this.actions });
  }

  goBack() {
    this.router.navigate(['/roles']);
  }
}
