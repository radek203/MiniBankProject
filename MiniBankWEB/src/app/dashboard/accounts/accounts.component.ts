import {Component, Input, OnInit} from '@angular/core';
import {AccountService} from '../../services/account.service';
import {Client} from '../../models/client.model';
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
export class AccountsComponent {

    @Input() accounts: Client[] = [];

    protected readonly getBranchName = getBranchName;
    protected readonly getBranchShortName = getBranchShortName;
}
