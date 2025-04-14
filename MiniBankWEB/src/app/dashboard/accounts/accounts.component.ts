import {Component, OnInit} from '@angular/core';
import {AccountService} from '../../services/account.service';
import {AuthService} from '../../services/auth.service';
import {getBranchName, getBranchShortName} from '../../app.utils';
import {FormatAccountNumberPipe} from '../../format-account-number.pipe';
import {CurrencyPipe} from '@angular/common';
import {TransfersComponent} from './transfers/transfers.component';
import {BalancechangesComponent} from './balancechanges/balancechanges.component';

@Component({
    selector: 'app-accounts',
    imports: [
        FormatAccountNumberPipe,
        CurrencyPipe,
        TransfersComponent,
        BalancechangesComponent
    ],
    templateUrl: './accounts.component.html',
    styleUrl: './accounts.component.scss'
})
export class AccountsComponent implements OnInit {

    protected readonly getBranchName = getBranchName;
    protected readonly getBranchShortName = getBranchShortName;

    constructor(private authService: AuthService, protected accountService: AccountService) {
    }

    ngOnInit(): void {
        this.accountService.loadAccounts(this.authService.user.id);
    }
}
