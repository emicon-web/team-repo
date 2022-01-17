import {
  Component,
  ElementRef,
  EventEmitter,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { BsModalRef, ModalModule, BsModalService } from 'ngx-bootstrap/modal';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';

import { NotificationService } from '../../../services/notification.service';
import { UserService } from '../../../services/user.service';
import { RoleAccess } from '../../../models/role-access';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FileUploadService } from 'src/app/services/file-upload.service';
import * as FileSaver from 'file-saver';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.scss'],
})
export class FileUploadComponent implements OnInit {
  bsModalRef: BsModalRef;
  state$: Observable<object>;
  id;
  roleAccess = RoleAccess;
  error: any;
  public data;
  merchantData: any;
  uploadFileView: boolean = false;
  uploadedFileView: boolean = false;
  isImage: boolean;
  fileUrl: string;
  file: any;
  uploadFileForm: FormGroup;
  formSubmitted: boolean = false;
  fileData: any;
  fileName: any;
  @ViewChild('fileInput')
  fileInputRef: ElementRef;

  constructor(
    public activatedRoute: ActivatedRoute,
    private userService: UserService,
    private modalService: BsModalService,
    private notificationService: NotificationService,
    private router: Router,
    private formBuilder: FormBuilder,
    private fileUploadService: FileUploadService,
    private spinner: NgxSpinnerService
  ) {}

  ngOnInit(): void {
    this.merchantData = this.userService.userValue;
    this.createForm();
  }

  // Getter for easy access to form fields
  get f() {
    return this.uploadFileForm.controls;
  }

  createForm() {
    this.uploadFileForm = this.formBuilder.group({
      file: [null],
      checksum: ['', [Validators.required]],
    });
  }

  goBackUpload() {
    this.createForm();
    this.uploadedFileView = false;
  }

  uploadFileStatusChange() {
    this.uploadFileView = true;
  }

  toFormData<T>(formValue: T) {
    console.log(formValue);
    const formData = new FormData();

    for (const key of Object.keys(formValue)) {
      let value = formValue[key];
      formData.append(key, value);
    }
    return formData;
  }

  isValidMimeType(mimeType) {
    if (
      mimeType.match(/image\/*/) ||
      mimeType.match(/pdf\/*/) ||
      mimeType.match(/application\/*/) ||
      mimeType.match(/text\/*/)
    ) {
      return true;
    }
  }

  onSelectFile(event, type) {
    let fileToUpload = event.target.files[0];
    if (fileToUpload.length === 0) return;

    // let mimeType = fileToUpload.type;
    // if (!this.isValidMimeType(mimeType)) return;

    let reader = new FileReader();
    reader.readAsDataURL(fileToUpload);
    reader.onload = (_event) => {
      const imgURL = reader.result as string;
      this.fileUrl = imgURL;
      if (type === 'again') {
        this.fileName = fileToUpload;
      }
      this.uploadFileForm.patchValue({ file: fileToUpload });
    };
  }

  saveUploadData() {
    this.spinner.show();
    this.formSubmitted = true;

    if (this.uploadFileForm.invalid) {
      this.spinner.hide();
      return;
    }

    if (this.uploadFileForm.value.file == null) {
      this.spinner.hide();
      this.notificationService.showError('', 'Please upload file');
      return;
    }

    const data = this.toFormData(this.uploadFileForm.value);

    this.fileUploadService.uploadFile(data).subscribe(
      (data: any) => {
        this.spinner.hide();
        console.log(data);
        if (!data.message) {
          this.notificationService.showSuccess(
            'File uploaded successfully',
            ''
          );
          this.fileData = data;
          this.formSubmitted = false;
          this.uploadedFileView = true;
        } else {
          this.formSubmitted = false;
          this.notificationService.showError(data.message, '');
        }
      },
      (err) => {
        this.spinner.hide();
        console.log(err);
      }
    );
  }

  downloadFile(fileId, fileName) {
    // this.spinner.show();
    let options = {
      payoutRoleType: 'MAKER',
      merchantId: this.merchantData.merchantId,
      payoutFileUploadId: fileId,
    };
    this.fileUploadService.getFileUrl(options).subscribe(
      (blob: Blob) => {
        // this.spinner.hide();
        if (!blob) return;
        const file = new File([blob], fileName, {
          type: blob.type,
        });
        FileSaver.saveAs(file);
      },
      (err) => {
        // this.spinner.show();
        console.log(err);
      }
    );
  }

  toViewFileHistory() {
    this.router.navigate(['/virtual-accounts/files-history']);
  }

  resetForm() {
    this.createForm();
    this.fileInputRef.nativeElement.value = '';
  }
}
