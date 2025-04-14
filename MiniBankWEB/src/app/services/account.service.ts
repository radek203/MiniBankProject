import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {Client} from '../models/client.model';
import {getBranchUrl} from '../app.utils';
import {BalanceChange} from '../models/balance-change.model';
import {Transfer} from '../models/transfer.model';

@Injectable({providedIn: 'root'})
export class AccountService {

    public accounts: Client[] = [];

    constructor(private httpService: HttpClient, private router: Router) {
    }

    loadAccounts(id: number) {
        this.httpService.get<Client[]>('client/' + id).subscribe({
            next: (response) => {
                this.accounts = response;
            },
            error: (error) => {
                console.error('Error fetching accounts:', error);
            }
        })
    }

    getOwnerByAccountNumber(accountNumber: string): Client | undefined {
        return this.accounts.find(account => account.accountNumber === accountNumber);
    }

    getOwnerNameByAccountNumber(accountNumber: string): string {
        const client = this.getOwnerByAccountNumber(accountNumber);
        return client ? client.firstName + ' ' + client.lastName : 'Unknown';
    }

    makeTransfer(accountNumber: string, toAccountNumber: string, amount: number) {
        const client = this.getOwnerByAccountNumber(accountNumber);
        return this.httpService.get<Transfer>(getBranchUrl(client?.branch) + '/transfer/' + accountNumber + "/" + toAccountNumber + "/" + amount);
    }

    makeDeposit(accountNumber: string, amount: number) {
        const client = this.getOwnerByAccountNumber(accountNumber);
        return this.httpService.get<BalanceChange>(getBranchUrl(client?.branch) + '/transfer/deposit/' + accountNumber + "/" + amount);
    }

    makeWithdraw(accountNumber: string, amount: number) {
        const client = this.getOwnerByAccountNumber(accountNumber);
        return this.httpService.get<BalanceChange>(getBranchUrl(client?.branch) + '/transfer/withdraw/' + accountNumber + "/" + amount);
    }

    getTransfer(accountNumber: string, id: string) {
        const client = this.getOwnerByAccountNumber(accountNumber);
        return this.httpService.get<Transfer>(getBranchUrl(client?.branch) + '/transfer/transfer/' + id);
    }

    getBalanceChange(accountNumber: string, id: string) {
        const client = this.getOwnerByAccountNumber(accountNumber);
        return this.httpService.get<BalanceChange>(getBranchUrl(client?.branch) + '/transfer/balance/' + id);
    }

}
