import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {CreditCard} from '../models/card.model';
import {Client} from '../models/client.model';
import {getBranchUrl} from '../app.utils';

@Injectable({providedIn: 'root'})
export class CardsService {

    constructor(private httpService: HttpClient, private router: Router) {
    }

    getCards(id: number) {
        return this.httpService.get<CreditCard[]>('creditcard/' + id);
    }

    createCard(client: Client) {
        return this.httpService.post<CreditCard>(getBranchUrl(client.branch) + '/account/create/card/' + client.accountNumber, {});
    }

    deleteCard(cardNumber: string) {
        return this.httpService.delete('creditcard/delete/' + cardNumber);
    }

    payByCard(cardNumber: string, date: string, cvv: string, service: string, amount: number) {
        console.log('cardNumber', cardNumber);
        return this.httpService.patch('creditcard/pay/' + cardNumber + '/' + date + '/' + cvv + '/' + service + '/' + amount, {});
    }

}
