import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {CardsService} from '../services/cards.service';
import {AuthService} from '../services/auth.service';
import {FormatCardNumberPipe} from '../format-card-number.pipe';
import {AccountService} from '../services/account.service';
import {FormatAccountNumberPipe} from '../format-account-number.pipe';
import {FormBuilder, ReactiveFormsModule} from '@angular/forms';
import {OrderComponent} from './order/order.component';
import {DelComponent} from './del/del.component';
import {PaymentComponent} from './payment/payment.component';

@Component({
  selector: 'app-cards',
    imports: [
        FormatCardNumberPipe,
        FormatAccountNumberPipe,
        ReactiveFormsModule,
        OrderComponent,
        DelComponent,
        PaymentComponent
    ],
  templateUrl: './cards.component.html',
  styleUrl: './cards.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class CardsComponent implements OnInit {

    constructor(private authService: AuthService, protected cardsService: CardsService, protected accountService: AccountService) {
    }

    ngOnInit(): void {
        this.cardsService.loadCards(this.authService.user.id);
    }

}
