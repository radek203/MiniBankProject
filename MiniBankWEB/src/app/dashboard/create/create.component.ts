import {Component} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';
import {getBranches} from '../../app.utils';
import {AccountService} from '../../services/account.service';
import {NotificationService} from '../../services/notification.service';

@Component({
    selector: 'app-create',
    imports: [
        ReactiveFormsModule,
        NgIf,
        NgForOf
    ],
    templateUrl: './create.component.html',
    styleUrl: './create.component.scss'
})
export class CreateComponent {

    clientForm!: FormGroup;
    protected readonly getBranches = getBranches;

    constructor(private fb: FormBuilder, private accountService: AccountService, private notificationService: NotificationService) {
        this.clientForm = this.fb.group({
            firstName: ['', Validators.required],
            lastName: ['', Validators.required],
            phone: ['', Validators.required],
            address: ['', Validators.required],
            city: ['', Validators.required],
            branch: [null, Validators.required]
        });
    }

    submitClientForm() {
        if (this.clientForm.valid) {
            const client = {
                ...this.clientForm.value
            };

            this.accountService.createAccount(client).subscribe({
                next: (response) => {
                    this.clientForm.reset();
                    this.accountService.accounts.push(response);
                    this.notificationService.clearNotifications();
                },
                error: (error) => {
                    this.notificationService.addNotification(error);
                }
            });
        } else {
            this.clientForm.markAllAsTouched();
        }
    }
}
