import {Component} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';
import {FormatAccountNumberPipe} from '../../format-account-number.pipe';
import {AccountService} from '../../services/account.service';
import {AuthService} from '../../services/auth.service';
import {BalanceChangeStatus} from '../../models/balance-change.model';
import {NotificationService} from '../../services/notification.service';

@Component({
    selector: 'app-withdraw',
    imports: [
        ReactiveFormsModule,
        NgForOf,
        FormatAccountNumberPipe,
        NgIf
    ],
    templateUrl: './withdraw.component.html',
    styleUrl: './withdraw.component.scss'
})
export class WithdrawComponent {

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
            this.accountService.makeWithdraw(account, this.transferForm.value['amount']).subscribe({
                next: (response) => {
                    this.transferForm.reset();
                    setTimeout(() => {
                        this.accountService.getBalanceChange(account, response.id).subscribe({
                            next: (response) => {
                                if (response.status === BalanceChangeStatus.COMPLETED) {
                                    this.accountService.loadSingleAccountByAccountNumber(account);
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
