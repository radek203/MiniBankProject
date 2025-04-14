import {Component} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';
import {FormatAccountNumberPipe} from '../../format-account-number.pipe';
import {AccountService} from '../../services/account.service';
import {BalanceChangeStatus} from '../../models/balance-change.model';
import {AuthService} from '../../services/auth.service';
import {NotificationService} from '../../services/notification.service';

@Component({
    selector: 'app-deposit',
    imports: [
        ReactiveFormsModule,
        NgForOf,
        FormatAccountNumberPipe,
        NgIf
    ],
    templateUrl: './deposit.component.html',
    styleUrl: './deposit.component.scss'
})
export class DepositComponent {

    transferForm!: FormGroup;

    constructor(private fb: FormBuilder, protected accountService: AccountService, private authService: AuthService, private notificationService: NotificationService) {
    }

    ngOnInit(): void {
        this.transferForm = this.fb.group({
            fromAccount: [null, Validators.required],
            amount: [null, [Validators.required, Validators.min(0.01)]]
        });
    }

    sendTransfer(): void {
        if (this.transferForm.valid) {
            const account = this.transferForm.value['fromAccount'];
            this.accountService.makeDeposit(account, this.transferForm.value['amount']).subscribe({
                next: (response) => {
                    this.transferForm.reset();
                    setTimeout(() => {
                        this.accountService.getBalanceChange(account, response.id).subscribe({
                            next: (response) => {
                                if (response.status === BalanceChangeStatus.COMPLETED) {
                                    this.accountService.loadAccounts(this.authService.user.id);
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
