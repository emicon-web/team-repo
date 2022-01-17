import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import {
  FormGroup,
  FormControl,
  FormBuilder,
  Validators,
  FormArray,
} from '@angular/forms';
import { BsModalService } from 'ngx-bootstrap/modal';

import { UserService } from '../../../services/user.service';
import { NotificationService } from '../../../services/notification.service';

import { Role } from '../../../models/role';

@Component({
  selector: 'app-edit-user',
  templateUrl: './edit-user.component.html',
  styleUrls: ['./edit-user.component.scss'],
})
export class EditUserComponent implements OnInit {
  formSubmitted;
  public id: any;
  data;
  editForm: FormGroup;
  categories = [];
  role;
  selectedRoles = [];
  @Output() passIsUpdated: EventEmitter<any> = new EventEmitter();

  constructor(
    private userService: UserService,
    private formBuilder: FormBuilder,
    private modalService: BsModalService,
    private notificationService: NotificationService
  ) {}
  loaded = false;
  ngOnInit(): void {
    this.getUserDetails(this.id);
    // this.selectRoles();
  }

  selectRoles() {
    this.userService.getRoles().subscribe((data: any) => {
      this.role = data;
      for (let i = 0; i < this.role.length; i++) {
        this.categories.push({
          id: this.role[i].id,
          name: this.role[i].name,
          isSelected: false,
        });
        if (i == this.role.length - 1) {
          this.createForm();
          this.getSelectedRoles();
          this.loaded = true;
        }
      }
    });
  }
  getSelectedValue() {
    // console.log(this.role);
  }

  // Getter for easy access to form fields
  get f() {
    return this.editForm.controls;
  }

  getUserDetails(id) {
    this.userService.getUserDetails(id).subscribe((data: any) => {
      this.data = data;
      this.selectRoles();
      // this.role = data.roles;
      // this.createForm();
    });

    // this.createForm();
  }

  createForm() {
    // if (this.data.userIsLocked == null) {
    //   this.data.userIsLocked = false;
    // }
    this.data.userIsLocked = this.data.userIsLocked
      ? this.data.userIsLocked
      : false;
    this.editForm = this.formBuilder.group({
      userFirstName: [this.data?.userFirstName, Validators.required],
      userLastName: [this.data?.userLastName, Validators.required],
      userName: [this.data?.userName, Validators.required],
      userEmail: [
        this.data?.userEmail,
        [
          Validators.required,
          Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$'),
        ],
      ],
      userPhone: [this.data?.userPhone],
      userIsActive: [this.data?.userIsActive, Validators.required],
      userIsLocked: [this.data?.userIsLocked, Validators.required],
      roles: [this.data?.roles, [Validators.required]],
      userUpdatedBy: [this.userService.userValue.id],
      userType: ['MERCHANT'],
      merchantId: ['MERCHANT1'],
    });
  }

  countryCode = '91';
  flag = false;
  onCountryChange(event) {
    this.flag = true;
    this.countryCode = event.dialCode;
  }

  editUser() {
    this.formSubmitted = true;
    // if (this.flag == true) {
    //   this.editForm.value.userPhone = this.countryCode + this.editForm.value.userPhone;
    // }
    if (this.editForm.invalid) {
      return;
    }
    this.userService
      .editUser(this.data.id, this.editForm.value)
      .subscribe((data: any) => {
        this.notificationService.showSuccess(data.message, '');
        this.passIsUpdated.emit();
        this.close();
      });
  }

  close() {
    this.modalService._hideModal(1);
  }

  getSelectedRoles() {
    if (this.data.roles.length && this.categories.length) {
      this.categories.forEach((e) => {
        this.data.roles.forEach((role) => {
          delete role.description;
          if (e.id === role.id) {
            e.isSelected = true;
            this.selectActions(true, e);
          }
        });
      });
    }
  }

  selectActions(event, val) {
    if (event) {
      this.selectedRoles.push(val);
    } else {
      const itemIndex = this.selectedRoles.indexOf(val);
      if (itemIndex != -1) this.selectedRoles.splice(itemIndex, 1);
    }
    this.editForm.patchValue({ roles: this.selectedRoles });
  }

  isNumber(evt) {
    evt = evt ? evt : window.event;
    var charCode = evt.which ? evt.which : evt.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
      return false;
    }
    return true;
  }
}
