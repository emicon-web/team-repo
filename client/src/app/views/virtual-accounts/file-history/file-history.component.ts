import { Component, OnInit, ViewChild } from '@angular/core';
import { ModalDirective } from 'ngx-bootstrap/modal';
import { Router } from '@angular/router';
import { NotificationService } from '../../../services/notification.service';
import { RoleAccess } from '../../../models/role-access';
import { UserService } from 'src/app/services/user.service';
import { FileUploadService } from 'src/app/services/file-upload.service';
import * as FileSaver from 'file-saver';
import Swal from 'sweetalert2';
import { NgxSpinnerService } from 'ngx-spinner';
import { Location } from '@angular/common';

@Component({
  selector: 'app-file-history',
  templateUrl: './file-history.component.html',
  styleUrls: ['./file-history.component.scss'],
})
export class FileUploadHistoryComponent implements OnInit {
  @ViewChild('dangerModal', { static: false })
  public dangerModal: ModalDirective;
  roleAccess = RoleAccess;
  // api response error or data
  error: any;
  public data;

  // for search
  public filterQuery = '';
  collection: any = [];
  headingText = 'Uploaded Files History';
  systemCollection = [];
  p: number = 1;
  itemsPerPage = 10;
  totalItems: any;

  deleteUserId;
  showCol: boolean;
  merchantData: any;
  formatSelected = 'week';

  constructor(
    private fileUploadService: FileUploadService,
    private router: Router,
    private notificationService: NotificationService,
    private userService: UserService,
    private spinner: NgxSpinnerService,
    private _location: Location
  ) {
    this.merchantData = this.userService.userValue;
  }

  ngOnInit(): void {
    this.getAllUploadedFiles();
  }

  false;
  getAllUploadedFiles() {
    this.p = 1;
    this.spinner.show();
    const days =
      this.formatSelected === 'week'
        ? 7
        : this.formatSelected === 'month'
        ? 30
        : 1;
    this.totalItems = 0;
    this.fileUploadService
      .getFileHistory(
        { merchantid: this.merchantData.merchantId },
        'MAKER',
        days
      )
      .subscribe(
        (data: any) => {
          this.spinner.hide();
          this.collection = data;
          this.totalItems = this.collection.length;
        },
        (error) => {
          this.spinner.hide();
          this.error = error;
        }
      );
  }

  getPage(page) {
    console.log(page);
  }

  itemOnPage(no) {
    this.p = 1;
    this.itemsPerPage = no;
  }

  approveRejectFile(fileId, status) {
    let options = {
      payoutRoleType: 'CHECKER',
      merchantId: this.merchantData.merchantId,
      payoutFileUploadId: fileId,
      action: status,
    };

    Swal.fire({
      title: 'Are you sure?',
      text: `you want to ${status.toLowerCase()} this file?`,
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Yes',
    }).then((result) => {
      if (result.isConfirmed) {
        this.spinner.show();
        this.fileUploadService.approveFile(options).subscribe(
          (data: any) => {
            const msg = status === 'APPROVE' ? 'approved' : 'rejected';
            this.notificationService.showSuccess(
              `File ${msg} successfully`,
              ''
            );
            this.spinner.hide();
            this.getAllUploadedFiles();
          },
          (err) => {
            this.spinner.hide();
            this.notificationService.showError('Error in approving file', '');
            console.log(err);
          }
        );
      }
    });
  }

  downloadFile(fileId, fileName) {
    this.spinner.show();
    let options = {
      payoutRoleType: 'MAKER',
      merchantId: this.merchantData.merchantId,
      payoutFileUploadId: fileId,
    };
    this.fileUploadService.getFileUrl(options).subscribe(
      (blob: Blob) => {
        this.spinner.hide();
        if (!blob) return;
        const file = new File([blob], fileName, {
          type: blob.type,
        });
        FileSaver.saveAs(file);
      },
      (err) => {
        this.spinner.hide();
        console.log(err);
      }
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

  onTypeChange(evt) {
    this.formatSelected = evt.target.value;
    this.getAllUploadedFiles();
  }

  goBack() {
    // this.router.navigate(['/virtual-accounts/file-upload']);
    this._location.back();
  }
}
