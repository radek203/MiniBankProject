import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {Client} from '../models/client.model';

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

}
