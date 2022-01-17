import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, Validators, FormArray } from '@angular/forms';
import { BsModalRef, ModalModule, BsModalService } from 'ngx-bootstrap/modal';

import { RolesService } from '../../../services/roles.service';
import { UserService } from '../../../services/user.service';
import { NotificationService } from '../../../services/notification.service';

import { Role } from '../../../models/role';
import { roleAccess } from '../../../models/action-list';

@Component({
  selector: 'app-edit-role',
  templateUrl: './edit-role.component.html',
  styleUrls: ['./edit-role.component.scss'],
})
export class EditRoleComponent implements OnInit {
  bsModalRef: BsModalRef;
  formSubmitted;
  public id: any;
  data;
  editForm: FormGroup;
  categories = [];
  actions = [];
  roleActions;
  loading = false;
  @Output() passIsRoleUpdated: EventEmitter<any> = new EventEmitter();

  constructor(
    private roleService: RolesService,
    private formBuilder: FormBuilder,
    private modalService: BsModalService,
    private userService: UserService,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    this.roleActions = roleAccess;
    this.selectRoles();
    this.getRoleDetails(this.id);
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
      this.categories.push({
        id: this.roleActions[i].id,
        name: this.roleActions[i].action,
        isSelected: false,
      });
      if (i == this.roleActions.length - 1) {
        this.loading = true;
      }
    }
    // this.categories = this.roleActions
    // this.role = [
    //   { name: 'ROLE_ADMIN' }
    // ];
  }
  getSelectedValue() {
    // console.log(this.actions);
  }

  // Getter for easy access to form fields
  get f() {
    return this.editForm.controls;
  }

  getRoleDetails(id) {
    this.roleService.getRoleDetails(id).subscribe((data) => {
      this.data = data;
      if (this.data.actions?.length) {

        this.createForm();
        this.categories.forEach((e) => {
          this.data.actions.forEach((a2) => {
            delete a2.description;
            if (e.id === a2.id) {
              e.isSelected = true;
              this.selectActions(true, e);
            }
          });
        });
      }
      this.createForm();
    });

  }
  showForm = false;
  createForm() {
    this.editForm = this.formBuilder.group({
      id: [this.data?.id, Validators.required],
      name: [this.data?.name, Validators.required],
      description: [this.data?.description, Validators.required],
      actions: [this.data?.actions, Validators.required],
      roleUpdatedBy: [this.userService.userValue.id, Validators.required],
      issuerId: ['SYSTEM'],
    });
    this.showForm = true;
  }

  editRole() {
    this.formSubmitted = true;
    if (this.editForm.invalid) {
      return;
    }
    this.roleService.editRole(this.editForm.value).subscribe(
      (data: any) => {
        this.notificationService.showSuccess(data.message, '');
        this.close();
        this.passIsRoleUpdated.emit();
      },
      (err) => {
        // console.log(err);
      }
    );
  }

  close() {
    this.modalService._hideModal(1);
  }

  selectActions(event, val) {
    if (event) {
      this.actions.push(val);
      this.editForm.patchValue({ actions: this.actions });
    } else {
      const itemIndex = this.actions.indexOf(val);
      if (itemIndex != -1) this.actions.splice(itemIndex, 1);
    }
    this.editForm.patchValue({ actions: this.actions });
  }
}
