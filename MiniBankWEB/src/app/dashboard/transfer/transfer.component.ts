import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';
import {FormatAccountNumberPipe} from '../../format-account-number.pipe';
import {AccountService} from '../../services/account.service';
import {AuthService} from '../../services/auth.service';
import {TransferStatus} from '../../models/transfer.model';
import {NotificationService} from '../../services/notification.service';

@Component({
    selector: 'app-transfer',
    imports: [
        FormsModule,
        NgForOf,
        ReactiveFormsModule,
        NgIf,
        FormatAccountNumberPipe
    ],
    templateUrl: './transfer.component.html',
    styleUrl: './transfer.component.scss'
})
export class TransferComponent implements OnInit {

    transferForm!: FormGroup;

    constructor(private fb: FormBuilder, protected accountService: AccountService, private authService: AuthService, private notificationService: NotificationService) {
    }

    ngOnInit(): void {
        this.transferForm = this.fb.group({
            fromAccount: [null, Validators.required],
            toAccount: ['', [Validators.required, Validators.pattern(/^\d{2} (\d{4}) (\d{4}) (\d{4}) (\d{4}) (\d{4}) (\d{4})$/)]],
            amount: [null, [Validators.required, Validators.min(0.01)]]
        });
    }

    formatAccountNumber(): void {
        const rawValue: string = this.transferForm.get('toAccount')?.value || '';

        const digitsOnly = rawValue.replace(/\D/g, '');

        let formatted = digitsOnly.replace(/^(\d{2})(\d{0,4})(\d{0,4})(\d{0,4})(\d{0,4})(\d{0,4})(\d{0,4})(\d{0,4})/, '$1 $2 $3 $4 $5 $6 $7');

        this.transferForm.get('toAccount')?.setValue(formatted, {emitEvent: false});
    }

    sendTransfer(): void {
        if (this.transferForm.valid) {
            const fromAccount = this.transferForm.value['fromAccount'];
            const toAccount = this.transferForm.value['toAccount'].replaceAll(" ", "");
            this.accountService.makeTransfer(fromAccount, toAccount, this.transferForm.value['amount']).subscribe({
                next: (response) => {
                    this.transferForm.reset();
                    setTimeout(() => {
                        this.accountService.getTransfer(fromAccount, response.id).subscribe({
                            next: (response) => {
                                if (response.status === TransferStatus.COMPLETED) {
                                    this.accountService.loadSingleAccountByAccountNumber(fromAccount);
                                    this.accountService.loadSingleAccountByAccountNumber(toAccount);
                                    this.notificationService.clearNotifications();
                                }
                            },
                            error: (error) => {
                                this.notificationService.addNotification(error);
                            }
                        });
                    }, 1000);
                },
                error: (error) => {
                    this.notificationService.addNotification(error);
                }
            });
        } else {
            this.transferForm.markAllAsTouched();
        }
    }

}
