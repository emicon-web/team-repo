import { Component, OnInit } from '@angular/core';
import { BsModalRef, BsModalService } from 'ngx-bootstrap/modal';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';

import { RolesService } from '../../../services/roles.service';
import { EditRoleComponent } from '../edit-role/edit-role.component';
import { RoleAccess } from '../../../models/role-access';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-role-details',
  templateUrl: './role-details.component.html',
  styleUrls: ['./role-details.component.scss'],
})
export class RoleDetailsComponent implements OnInit {
  bsModalRef: BsModalRef;
  state$: Observable<object>;
  id;
  roleAccess = RoleAccess;
  error: any;
  public data;
  constructor(
    public activatedRoute: ActivatedRoute,
    private modalService: BsModalService,
    private roleService: RolesService,
    private router: Router,
    private spinner: NgxSpinnerService
  ) { }

  ngOnInit(): void {
    this.getId();
  }

  getId() {
    this.id = this.activatedRoute.snapshot.paramMap.get('id');
    this.getRoleDetails(this.id);
  }

  getRoleDetails(id) {
    this.spinner.show()
    this.roleService.getRoleDetails(id).subscribe((data) => {
      this.spinner.hide()
      this.data = data;
    }, error => {
      this.spinner.hide()
    });
  }

  editRole() {
    const initialState = {
      id: this.id,
    };
    this.bsModalRef = this.modalService.show(EditRoleComponent, {
      initialState,
      class: 'modal-xl',
    });
    this.bsModalRef.content.id = this.id;
    this.bsModalRef.content.passIsRoleUpdated.subscribe((receivedEntry) => {
      this.getRoleDetails(this.id);
    });
  }

  goBack() {
    this.router.navigate(['/roles']);
  }
}
