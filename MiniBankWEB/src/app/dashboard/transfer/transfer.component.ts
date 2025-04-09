import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {DecimalPipe, NgForOf, NgIf} from '@angular/common';
import {FormatAccountNumberPipe} from '../../format-account-number.pipe';
import {Client} from '../../models/client.model';
import {AccountService} from '../../services/account.service';

@Component({
  selector: 'app-transfer',
    imports: [
        FormsModule,
        NgForOf,
        ReactiveFormsModule,
        NgIf,
        FormatAccountNumberPipe
    ],
  templateUrl: './transfer.component.html',
  styleUrl: './transfer.component.scss'
})
export class TransferComponent implements OnInit {

    transferForm!: FormGroup;

    constructor(private fb: FormBuilder, protected accountService: AccountService) {}

    ngOnInit(): void {
        this.transferForm = this.fb.group({
            fromAccount: [null, Validators.required],
            toAccount: ['', [Validators.required, Validators.pattern(/^\d{2} (\d{4}) (\d{4}) (\d{4}) (\d{4}) (\d{4}) (\d{4})$/)]],
            amount: [null, [Validators.required, Validators.min(0.01)]]
        });
    }

    formatAccountNumber(): void {
        const rawValue: string = this.transferForm.get('toAccount')?.value || '';

        const digitsOnly = rawValue.replace(/\D/g, '');

        let formatted = digitsOnly.replace(/^(\d{2})(\d{0,4})(\d{0,4})(\d{0,4})(\d{0,4})(\d{0,4})(\d{0,4})(\d{0,4})/, '$1 $2 $3 $4 $5 $6 $7');

        this.transferForm.get('toAccount')?.setValue(formatted, { emitEvent: false });
    }

    sendTransfer(): void {
        if (this.transferForm.valid) {

        } else {
            this.transferForm.markAllAsTouched();
        }
    }

}
