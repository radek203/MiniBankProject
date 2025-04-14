import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {CreditCard} from '../models/card.model';
import {Client} from '../models/client.model';
import {getBranchUrl} from '../app.utils';

@Injectable({providedIn: 'root'})
export class CardsService {

    public cards: CreditCard[] = [];

    constructor(private httpService: HttpClient, private router: Router) {
    }

    loadCards(id: number) {
        this.httpService.get<CreditCard[]>('creditcard/' + id).subscribe({
            next: (response) => {
                this.cards = response;
            },
            error: (error) => {
                console.error('Error fetching cards:', error);
            }
        });
    }

    createCard(client: Client) {
        return this.httpService.post<CreditCard>(getBranchUrl(client.branch) + '/account/create/card/' + client.accountNumber, {});
    }

    deleteCard(cardNumber: string) {
        return this.httpService.delete('creditcard/delete/' + cardNumber);
    }

    payByCard(cardNumber: string, date: string, cvv: string, service: string, amount: number) {
        return this.httpService.patch('creditcard/pay/' + cardNumber + '/' + date + '/' + cvv + '/' + service + '/' + amount, {});
    }

}
