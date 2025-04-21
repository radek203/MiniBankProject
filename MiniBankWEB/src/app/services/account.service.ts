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
    public transfers: Transfer[] = [];
    public balanceChanges: BalanceChange[] = [];
    public showTransfers: String[] = [];
    public showBalanceChanges: String[] = [];

    constructor(private httpService: HttpClient, private router: Router) {
    }

    loadAccounts(id: number) {
        this.httpService.get<Client[]>('client/' + id).subscribe({
            next: (response) => {
                this.accounts = [];
                response.forEach(account => {
                    this.loadSingleAccount(account);
                });
            },
            error: (error) => {
                console.error('Error fetching accounts:', error);
            }
        });
    }

    loadSingleAccount(client: Client) {
        this.httpService.get<Client>(getBranchUrl(client.branch) + '/account/' + client.id).subscribe({
            next: (response) => {
                const existingIndex = this.accounts.findIndex(account => account.id === response.id);
                if (existingIndex !== -1) {
                    this.accounts[existingIndex] = response;
                } else {
                    this.accounts.push(response);
                }
                this.showTransfers = this.showTransfers.filter(transfer => transfer !== response.accountNumber);
                this.showBalanceChanges = this.showBalanceChanges.filter(balanceChange => balanceChange !== response.accountNumber);
            },
            error: (error) => {
                console.error('Error fetching account:', error);
            }
        });
    }

    loadSingleAccountByAccountNumber(accountNumber: string) {
        const client = this.getOwnerByAccountNumber(accountNumber);
        if (client) {
            this.loadSingleAccount(client);
        }
    }

    createAccount(client: Client) {
        return this.httpService.post<Client>(getBranchUrl(client.branch) + '/account/create', client);
    }

    updateAccount(clientId: string, client: Client) {
        return this.httpService.put<Client>(getBranchUrl(client.branch) + '/account/update/' + clientId, client);
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
        return this.httpService.get<Transfer>(getBranchUrl(client?.branch) + '/transfer/transfer/' + client?.accountNumber + '/' + id);
    }

    getBalanceChange(accountNumber: string, id: string) {
        const client = this.getOwnerByAccountNumber(accountNumber);
        return this.httpService.get<BalanceChange>(getBranchUrl(client?.branch) + '/transfer/balance/' + client?.accountNumber + '/' + id);
    }

    loadTransfers(accountNumber: string) {
        return new Promise((resolve, reject) => {
            const client = this.getOwnerByAccountNumber(accountNumber);
            this.httpService.get<Transfer[]>(getBranchUrl(client?.branch) + '/transfer/transfer/all/' + accountNumber).subscribe({
                next: (response) => {
                    this.transfers = this.transfers.filter(transfer => transfer.toAccount !== accountNumber && transfer.fromAccount !== accountNumber);
                    this.transfers.push(...response);
                    this.transfers.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());
                    resolve(response);
                },
                error: (error) => {
                    console.error('Error fetching transfers:', error);
                    reject(error);
                }
            });
        });
    }

    loadBalanceChanges(accountNumber: string) {
        return new Promise((resolve, reject) => {
            const client = this.getOwnerByAccountNumber(accountNumber);
            this.httpService.get<BalanceChange[]>(getBranchUrl(client?.branch) + '/transfer/balance/all/' + accountNumber).subscribe({
                next: (response) => {
                    this.balanceChanges = this.balanceChanges.filter(balanceChange => balanceChange.account !== accountNumber);
                    this.balanceChanges.push(...response);
                    this.balanceChanges.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());
                    resolve(response);
                },
                error: (error) => {
                    console.error('Error fetching balance changes:', error);
                    reject(error);
                }
            });
        });
    }

    getTransfersByAccountNumber(accountNumber: string): Transfer[] {
        return this.transfers.filter(transfer => transfer.toAccount === accountNumber || transfer.fromAccount === accountNumber);
    }

    getBalanceChangesByAccountNumber(accountNumber: string): BalanceChange[] {
        return this.balanceChanges.filter(balanceChange => balanceChange.account === accountNumber);
    }

}
