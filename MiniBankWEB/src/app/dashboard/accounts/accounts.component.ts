import {Component, OnInit} from '@angular/core';
import {AccountService} from '../../services/account.service';
import {AuthService} from '../../services/auth.service';
import {getBranchName, getBranchShortName} from '../../app.utils';
import {FormatAccountNumberPipe} from '../../format-account-number.pipe';
import {CurrencyPipe} from '@angular/common';

@Component({
  selector: 'app-accounts',
    imports: [
        FormatAccountNumberPipe,
        CurrencyPipe
    ],
  templateUrl: './accounts.component.html',
  styleUrl: './accounts.component.scss'
})
export class AccountsComponent implements OnInit {

    constructor(private authService: AuthService, protected accountService: AccountService) {
    }

    protected readonly getBranchName = getBranchName;
    protected readonly getBranchShortName = getBranchShortName;

    ngOnInit(): void {
        this.accountService.loadAccounts(this.authService.user.id);
    }
}
