import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {Client} from '../models/client.model';

@Injectable({providedIn: 'root'})
export class AccountService {

    constructor(private httpService: HttpClient, private router: Router) {
    }

    getAccounts(id: number) {
        return this.httpService.get<Client[]>('client/' + id);
    }

}
