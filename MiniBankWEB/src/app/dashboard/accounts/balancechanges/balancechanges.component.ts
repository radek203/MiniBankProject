import {Component, Input} from '@angular/core';
import {AccountService} from '../../../services/account.service';
import {CurrencyPipe, DatePipe} from '@angular/common';

@Component({
  selector: 'app-balancechanges',
    imports: [
        DatePipe,
        CurrencyPipe
    ],
  templateUrl: './balancechanges.component.html',
  styleUrl: './balancechanges.component.scss'
})
export class BalancechangesComponent {

    @Input() accountNumber = '';

    page = 1;
    pageSize = 10;

    constructor(protected accountService: AccountService) {
    }

    get pagedBalanceChanges() {
        const start = (this.page - 1) * this.pageSize;
        return this.accountService.getBalanceChangesByAccountNumber(this.accountNumber).slice(start, start + this.pageSize);
    }

    totalBalanceChangesPages(): number {
        return Math.ceil(this.accountService.getBalanceChangesByAccountNumber(this.accountNumber).length / this.pageSize);
    }

    changePage(newPage: number) {
        if (newPage >= 1 && newPage <= this.totalBalanceChangesPages()) {
            this.page = newPage;
        }
    }

    changeShowBalanceChanges() {
        if (this.accountService.showBalanceChanges.indexOf(this.accountNumber) === -1) {
            this.accountService.loadBalanceChanges(this.accountNumber).then(() => {
                this.accountService.showBalanceChanges.push(this.accountNumber);
            });
        } else {
            this.accountService.showBalanceChanges.splice(this.accountService.showBalanceChanges.indexOf(this.accountNumber), 1);
        }
    }

}
