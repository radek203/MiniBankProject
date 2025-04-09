import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {CardsService} from '../services/cards.service';
import {AuthService} from '../services/auth.service';
import {CreditCard} from '../models/card.model';
import {FormatCardNumberPipe} from '../format-card-number.pipe';
import {AccountService} from '../services/account.service';
import {FormatAccountNumberPipe} from '../format-account-number.pipe';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-cards',
    imports: [
        FormatCardNumberPipe,
        FormatAccountNumberPipe,
        ReactiveFormsModule,
        NgForOf,
        NgIf
    ],
  templateUrl: './cards.component.html',
  styleUrl: './cards.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class CardsComponent implements OnInit {

    orderForm!: FormGroup;
    deleteForm!: FormGroup;
    paymentForm!: FormGroup;
    protected cards: CreditCard[] = [];

    constructor(private fb: FormBuilder, private authService: AuthService, private cardsService: CardsService, protected accountService: AccountService) {
    }

    ngOnInit(): void {
        this.orderForm = this.fb.group({
            account: [null, Validators.required]
        });
        this.deleteForm = this.fb.group({
            card: [null, Validators.required]
        });
        this.paymentForm = this.fb.group({
            cardNumber: ['', [Validators.required, Validators.pattern(/^\d{4} \d{4} \d{4} \d{4}$/)]],
            expirationDate: ['', [Validators.required, Validators.pattern(/^(0[1-9]|1[0-2])\/\d{2}$/)]],
            cvv: ['', [Validators.required, Validators.pattern(/^\d{3}$/)]],
            uuid: ['', [Validators.required, Validators.pattern(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i)]],
            amount: [null, [Validators.required, Validators.min(0.01)]]
        });
        this.cardsService.getCards(this.authService.user.id).subscribe({
            next: (response) => {
                this.cards = response;
            },
            error: (error) => {
                console.error('Error fetching cards:', error);
            }
        });
    }

    formatCardNumber(): void {
        const rawValue: string = this.paymentForm.get('cardNumber')?.value || '';
        const digitsOnly = rawValue.replace(/\D/g, '').substring(0, 16);
        const formatted = digitsOnly.replace(/(.{4})/g, '$1 ').trim();

        this.paymentForm.get('cardNumber')?.setValue(formatted, { emitEvent: false });
    }

    orderCard() {
        if (this.orderForm.valid) {
            const client = this.accountService.getOwnerByAccountNumber(this.orderForm.value['account']);
            if (client) {
                this.cardsService.createCard(client).subscribe({
                    next: (response) => {
                        this.cards.push(response);
                        console.log('Card ordered successfully:', response);
                    },
                    error: (error) => {
                        console.error('Error ordering card:', error);
                    }
                });
            }
        } else {
            this.orderForm.markAllAsTouched();
        }
    }

    deleteCard() {
        if (this.deleteForm.valid) {
            this.cardsService.deleteCard(this.deleteForm.value['card']).subscribe({
                next: () => {
                    this.cards = this.cards.filter(c => c.cardNumber !== this.deleteForm.value['card']);
                    this.deleteForm.get('card')?.setValue(null);
                    console.log('Card deleted successfully');
                },
                error: (error) => {
                    console.error('Error deleting card:', error);
                }
            });
        } else {
            this.deleteForm.markAllAsTouched();
        }
    }

    submitPayment(): void {
        if (this.paymentForm.valid) {
            this.cardsService.payByCard(this.paymentForm.value['cardNumber'].replaceAll(" ", ""), this.paymentForm.value['expirationDate'].replace("/", ""), this.paymentForm.value['cvv'], this.paymentForm.value['uuid'], this.paymentForm.value['amount']).subscribe({
                next: (response) => {
                    console.log('Payment successful:', response);
                    this.paymentForm.reset();
                },
                error: (error) => {
                    console.error('Error processing payment:', error);
                }
            });
        } else {
            this.paymentForm.markAllAsTouched();
        }
    }

}
