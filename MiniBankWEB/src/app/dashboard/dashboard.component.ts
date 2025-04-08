import {Component, OnInit} from '@angular/core';
import {AccountsComponent} from './accounts/accounts.component';
import {TransferComponent} from './transfer/transfer.component';
import {Client} from '../models/client.model';
import {AuthService} from '../services/auth.service';
import {AccountService} from '../services/account.service';
import {DepositComponent} from './deposit/deposit.component';
import {WithdrawComponent} from './withdraw/withdraw.component';

@Component({
  selector: 'app-dashboard',
    imports: [
        AccountsComponent,
        TransferComponent,
        DepositComponent,
        WithdrawComponent
    ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {

    protected accounts: Client[] = [];

    constructor(private authService: AuthService, private accountService: AccountService) {
    }

    ngOnInit(): void {
        this.accountService.getAccounts(this.authService.user.id).subscribe({
            next: (response) => {
                this.accounts = response;
            },
            error: (error) => {
                console.error('Error fetching accounts:', error);
            }
        })
    }

}
