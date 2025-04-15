import {Component} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {AccountService} from '../../services/account.service';
import {NotificationService} from '../../services/notification.service';
import {FormatAccountNumberPipe} from '../../format-account-number.pipe';
import {Client, emptyClient} from '../../models/client.model';

@Component({
    selector: 'app-edit',
    imports: [
        NgForOf,
        NgIf,
        ReactiveFormsModule,
        FormatAccountNumberPipe
    ],
    templateUrl: './edit.component.html',
    styleUrl: './edit.component.scss'
})
export class EditComponent {

    clientForm!: FormGroup;
    clientId: string = '';
    clientBranch: number = 0;

    constructor(private fb: FormBuilder, protected accountService: AccountService, private notificationService: NotificationService) {
        this.setClientForm({...emptyClient});
    }

    setClientForm(client: Client) {
        this.clientForm = this.fb.group({
            firstName: [client.firstName, Validators.required],
            lastName: [client.lastName, Validators.required],
            phone: [client.phone, Validators.required],
            address: [client.address, Validators.required],
            city: [client.city, Validators.required],
            accountNumber: [client.accountNumber.length == 0 ? null : client.accountNumber, Validators.required]
        });
    }

    onAccountChange() {
        const accountNumber = this.clientForm.get('accountNumber')?.value;
        if (accountNumber) {
            const client = this.accountService.getOwnerByAccountNumber(accountNumber);
            if (client) {
                this.clientId = client.id;
                this.clientBranch = client.branch;
                this.setClientForm({...client});
            }
        }
    }

    submitClientForm() {
        if (this.clientForm.valid) {
            const client = {
                ...this.clientForm.value,
                'branch': this.clientBranch
            };

            this.accountService.updateAccount(this.clientId, client).subscribe({
                next: (response) => {
                    this.clientForm.reset();
                    this.accountService.accounts = this.accountService.accounts.map(client => client.id === response.id ? response : client);
                    this.notificationService.clearNotifications();
                },
                error: (error) => {
                    console.log(error);
                    this.notificationService.addNotification(error);
                }
            });
        } else {
            this.clientForm.markAllAsTouched();
        }
    }

}
