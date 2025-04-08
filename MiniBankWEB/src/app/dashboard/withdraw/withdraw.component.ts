import {Component, Input} from '@angular/core';
import {Client} from '../../models/client.model';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';
import {FormatAccountNumberPipe} from '../../format-account-number.pipe';

@Component({
  selector: 'app-withdraw',
    imports: [
        ReactiveFormsModule,
        NgForOf,
        FormatAccountNumberPipe,
        NgIf
    ],
  templateUrl: './withdraw.component.html',
  styleUrl: './withdraw.component.scss'
})
export class WithdrawComponent {

    transferForm!: FormGroup;

    @Input() accounts: Client[] = [];

    constructor(private fb: FormBuilder) {}

    ngOnInit(): void {
        this.transferForm = this.fb.group({
            fromAccount: [null, Validators.required],
            amount: [null, [Validators.required, Validators.min(0.01)]]
        });
    }

    sendTransfer(): void {
        if (this.transferForm.valid) {

        } else {
            this.transferForm.markAllAsTouched();
        }
    }

}
