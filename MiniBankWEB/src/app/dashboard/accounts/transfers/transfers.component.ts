import {Component, Input} from '@angular/core';
import {AccountService} from '../../../services/account.service';
import {CurrencyPipe, DatePipe} from '@angular/common';
import {FormatAccountNumberPipe} from '../../../format-account-number.pipe';

@Component({
    selector: 'app-transfers',
    imports: [
        DatePipe,
        CurrencyPipe,
        FormatAccountNumberPipe
    ],
    templateUrl: './transfers.component.html',
    styleUrl: './transfers.component.scss'
})
export class TransfersComponent {

    @Input() accountNumber = '';

    page = 1;
    pageSize = 10;

    constructor(protected accountService: AccountService) {
    }

    get pagedTransfers() {
        const start = (this.page - 1) * this.pageSize;
        return this.accountService.getTransfersByAccountNumber(this.accountNumber).slice(start, start + this.pageSize);
    }

    totalTransfersPages(): number {
        return Math.ceil(this.accountService.getTransfersByAccountNumber(this.accountNumber).length / this.pageSize);
    }

    changePage(newPage: number) {
        if (newPage >= 1 && newPage <= this.totalTransfersPages()) {
            this.page = newPage;
        }
    }

    changeShowTransfers() {
        if (this.accountService.showTransfers.indexOf(this.accountNumber) === -1) {
            this.accountService.loadTransfers(this.accountNumber).then(() => {
                this.accountService.showTransfers.push(this.accountNumber);
            });
        } else {
            this.accountService.showTransfers.splice(this.accountService.showTransfers.indexOf(this.accountNumber), 1);
        }
    }

}
