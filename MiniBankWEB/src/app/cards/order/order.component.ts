import {Component, OnInit} from '@angular/core';
import {FormatAccountNumberPipe} from "../../format-account-number.pipe";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";
import {CardsService} from '../../services/cards.service';
import {AccountService} from '../../services/account.service';
import {NotificationService} from '../../services/notification.service';

@Component({
    selector: 'app-order',
    imports: [
        FormatAccountNumberPipe,
        FormsModule,
        NgForOf,
        NgIf,
        ReactiveFormsModule
    ],
    templateUrl: './order.component.html',
    styleUrl: './order.component.scss'
})
export class OrderComponent implements OnInit {

    orderForm!: FormGroup;

    constructor(private fb: FormBuilder, private cardsService: CardsService, protected accountService: AccountService, private notificationService: NotificationService) {
    }

    ngOnInit(): void {
        this.orderForm = this.fb.group({
            account: [null, Validators.required]
        });
    }

    orderCard() {
        if (this.orderForm.valid) {
            const client = this.accountService.getOwnerByAccountNumber(this.orderForm.value['account']);
            if (client) {
                this.cardsService.createCard(client).subscribe({
                    next: (response) => {
                        this.cardsService.cards.push(response);
                        this.orderForm.reset();
                        this.notificationService.clearNotifications();
                    },
                    error: (error) => {
                        this.notificationService.addNotification(error);
                    }
                });
            }
        } else {
            this.orderForm.markAllAsTouched();
        }
    }

}
