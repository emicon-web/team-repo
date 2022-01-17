import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { BsModalRef, ModalModule, BsModalService } from 'ngx-bootstrap/modal';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';

import { NotificationService } from '../../../services/notification.service';
import { UserService } from '../../../services/user.service';
import { RoleAccess } from '../../../models/role-access';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { VirtualAccountService } from 'src/app/services/payout-account.service';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-virtual-account-details',
  templateUrl: './virtual-account-details.component.html',
  styleUrls: ['./virtual-account-details.component.scss'],
})
export class VirtualAccountDetailsComponent implements OnInit {
  bsModalRef: BsModalRef;
  state$: Observable<object>;
  id;
  roleAccess = RoleAccess;
  error: any;
  public data;
  merchantData: any;

  constructor(
    public activatedRoute: ActivatedRoute,
    private userService: UserService,
    private router: Router,
    private virtualAccountService: VirtualAccountService,
    private spinner: NgxSpinnerService
  ) { }

  ngOnInit(): void {
    this.merchantData = this.userService.userValue;
    this.getId();
  }

  getId() {
    this.id = this.activatedRoute.snapshot.paramMap.get('id');
    console.log(this.id)
    this.getVirtualAccountDetails(this.id);
  }

  getVirtualAccountDetails(id) {
    this.spinner.show();
    this.virtualAccountService.getPADetails(id).subscribe(
      (data) => {
        this.spinner.hide();
        this.data = data;
      },
      (error) => {
        this.spinner.hide();
        this.error = error;
      }
    );
  }

  goBack() {
    this.router.navigate(['/virtual-accounts']);
  }
}
