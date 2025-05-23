import {Component, OnInit} from '@angular/core';
import {FormatCardNumberPipe} from "../../format-card-number.pipe";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";
import {CardsService} from '../../services/cards.service';
import {AccountService} from '../../services/account.service';
import {NotificationService} from '../../services/notification.service';

@Component({
    selector: 'app-del',
    imports: [
        FormatCardNumberPipe,
        FormsModule,
        NgForOf,
        NgIf,
        ReactiveFormsModule
    ],
    templateUrl: './del.component.html',
    styleUrl: './del.component.scss'
})
export class DelComponent implements OnInit {

    deleteForm!: FormGroup;

    constructor(private fb: FormBuilder, protected cardsService: CardsService, protected accountService: AccountService, private notificationService: NotificationService) {
    }

    ngOnInit(): void {
        this.deleteForm = this.fb.group({
            card: [null, Validators.required]
        });
    }

    deleteCard() {
        if (this.deleteForm.valid) {
            const cardNumber = this.deleteForm.value['card'];
            const card = this.cardsService.cards.find(card => card.cardNumber === cardNumber);
            if (!card) {
                return;
            }
            const client = this.accountService.getOwnerByAccountNumber(card.accountNumber);
            if (!client) {
                return;
            }
            this.cardsService.deleteCard(client, cardNumber).subscribe({
                next: () => {
                    this.cardsService.cards = this.cardsService.cards.filter(c => c.cardNumber !== cardNumber);
                    this.deleteForm.reset();
                    this.notificationService.clearNotifications();
                },
                error: (error) => {
                    this.notificationService.addNotification(error);
                }
            });
        } else {
            this.deleteForm.markAllAsTouched();
        }
    }

}
