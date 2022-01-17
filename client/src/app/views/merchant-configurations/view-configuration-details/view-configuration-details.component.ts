import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { BsModalRef, ModalModule, BsModalService } from 'ngx-bootstrap/modal';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { NotificationService } from '../../../services/notification.service';
import { UserService } from '../../../services/user.service';
import { RoleAccess } from '../../../models/role-access';
import { EditConfigurationComponent } from '../edit-configuration/edit-configuration.component';
import { ConfigurationService } from 'src/app/services/configuration.service';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-view-configuration-details',
  templateUrl: './view-configuration-details.component.html',
  styleUrls: ['./view-configuration-details.component.scss'],
})
export class ViewConfigurationDetailsComponent implements OnInit {
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
    private modalService: BsModalService,
    private notifyService: NotificationService,
    private router: Router,
    private congiurationService: ConfigurationService,
    private spinner: NgxSpinnerService
  ) {}

  ngOnInit(): void {
    this.merchantData = this.userService.userValue;
    this.getConfigurationDetails();
  }

  getConfigurationDetails() {
    this.spinner.show();
    this.congiurationService
      .getDetails({ merchantid: this.merchantData.merchantId })
      .subscribe(
        (data) => {
          this.spinner.hide();
          this.data = data;
          if (this.data?.paymentMode !== '' && this.data != null) {
            this.data.paymentMode = this.data?.paymentMode.split(',');
          }
        },
        (error) => {
          this.spinner.hide();
          this.error = error;
        }
      );
  }

  editConfiguration() {
    this.bsModalRef = this.modalService.show(EditConfigurationComponent, {
      class: 'modal-lg',
      backdrop: 'static',
    });
    this.bsModalRef.content.passIsUpdated.subscribe((receivedEntry) => {
      this.getConfigurationDetails();
    });
  }

  goBack() {
    this.router.navigate(['/configuration']);
  }
}
