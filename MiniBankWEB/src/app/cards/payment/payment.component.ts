import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {NgIf} from "@angular/common";
import {CardsService} from '../../services/cards.service';
import {AccountService} from '../../services/account.service';
import {NotificationService} from '../../services/notification.service';

@Component({
    selector: 'app-payment',
    imports: [
        FormsModule,
        NgIf,
        ReactiveFormsModule
    ],
    templateUrl: './payment.component.html',
    styleUrl: './payment.component.scss'
})
export class PaymentComponent implements OnInit {

    paymentForm!: FormGroup;

    constructor(private fb: FormBuilder, private cardsService: CardsService, protected accountService: AccountService, private notificationService: NotificationService) {
    }

    ngOnInit(): void {
        this.paymentForm = this.fb.group({
            cardNumber: ['', [Validators.required, Validators.pattern(/^\d{4} \d{4} \d{4} \d{4}$/)]],
            expirationDate: ['', [Validators.required, Validators.pattern(/^(0[1-9]|1[0-2])\/\d{2}$/)]],
            cvv: ['', [Validators.required, Validators.pattern(/^\d{3}$/)]],
            uuid: ['', [Validators.required, Validators.pattern(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i)]],
            amount: [null, [Validators.required, Validators.min(0.01)]]
        });
    }

    formatCardNumber(): void {
        const rawValue: string = this.paymentForm.get('cardNumber')?.value || '';
        const digitsOnly = rawValue.replace(/\D/g, '').substring(0, 16);
        const formatted = digitsOnly.replace(/(.{4})/g, '$1 ').trim();

        this.paymentForm.get('cardNumber')?.setValue(formatted, {emitEvent: false});
    }

    submitPayment(): void {
        if (this.paymentForm.valid) {
            this.cardsService.payByCard(this.paymentForm.value['cardNumber'].replaceAll(" ", ""), this.paymentForm.value['expirationDate'].replace("/", ""), this.paymentForm.value['cvv'], this.paymentForm.value['uuid'], this.paymentForm.value['amount']).subscribe({
                next: (response) => {
                    this.paymentForm.reset();
                },
                error: (error) => {
                    this.notificationService.addNotification(error);
                }
            });
        } else {
            this.paymentForm.markAllAsTouched();
        }
    }

}
