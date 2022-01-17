import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { BsModalRef, ModalModule, BsModalService } from 'ngx-bootstrap/modal';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { NotificationService } from '../../../services/notification.service';
import { UserService } from '../../../services/user.service';
import { EditUserComponent } from '../edit-user/edit-user.component';
import { RoleAccess } from '../../../models/role-access';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-user-details',
  templateUrl: './user-details.component.html',
  styleUrls: ['./user-details.component.scss'],
})
export class UserDetailsComponent implements OnInit {
  bsModalRef: BsModalRef;
  state$: Observable<object>;
  id;
  roleAccess = RoleAccess;
  error: any;
  public data;

  constructor(
    public activatedRoute: ActivatedRoute,
    private userService: UserService,
    private modalService: BsModalService,
    private notifyService: NotificationService,
    private router: Router,
    private spinner: NgxSpinnerService
  ) {}

  ngOnInit(): void {
    this.getId();
  }

  getId() {
    this.id = this.activatedRoute.snapshot.paramMap.get('id');
    this.getUserDetails(this.id);
  }

  getUserDetails(id) {
    this.spinner.show()
    this.userService.getUserDetails(id).subscribe(
      (data) => {
        this.spinner.hide()
        this.data = data;
      },
      (error) => {
        this.spinner.hide()
        this.error = error
      }
    );
  }

  editUser() {
    const initialState = {
      id: this.id,
    };
    this.bsModalRef = this.modalService.show(EditUserComponent, {
      initialState,
      class: 'modal-lg',
    });
    this.bsModalRef.content.id = this.id;
    this.bsModalRef.content.passIsUpdated.subscribe((receivedEntry) => {
      this.getUserDetails(this.id);
    });

    // this.bsModalRef.content.onClose.subscribe(result => {
    //   console.log('results', result);
    // })
  }

  goBack() {
    this.router.navigate(['/users']);
  }
}
