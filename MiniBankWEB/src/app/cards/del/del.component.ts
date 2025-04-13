import {Component, OnInit} from '@angular/core';
import {FormatCardNumberPipe} from "../../format-card-number.pipe";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";
import {CardsService} from '../../services/cards.service';
import {AccountService} from '../../services/account.service';

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

    constructor(private fb: FormBuilder, protected cardsService: CardsService, protected accountService: AccountService) {
    }

    ngOnInit(): void {
        this.deleteForm = this.fb.group({
            card: [null, Validators.required]
        });
    }

    deleteCard() {
        if (this.deleteForm.valid) {
            this.cardsService.deleteCard(this.deleteForm.value['card']).subscribe({
                next: () => {
                    this.cardsService.cards = this.cardsService.cards.filter(c => c.cardNumber !== this.deleteForm.value['card']);
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

}
