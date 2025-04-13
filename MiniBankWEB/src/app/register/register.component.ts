import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgIf} from '@angular/common';
import {AuthService} from '../services/auth.service';
import {User} from '../models/user.model';

@Component({
    selector: 'app-register',
    imports: [
        ReactiveFormsModule,
        NgIf
    ],
    templateUrl: './register.component.html',
    styleUrl: './register.component.scss'
})
export class RegisterComponent implements OnInit {

    registrationForm!: FormGroup;
    submitted = false;

    constructor(private fb: FormBuilder, private authService: AuthService) {
    }

    ngOnInit(): void {
        this.registrationForm = this.fb.group({
            email: [
                '',
                [Validators.required, Validators.email]
            ],
            username: [
                '',
                [Validators.required]
            ],
            password: [
                '',
                [Validators.required, Validators.minLength(6)]
            ]
        });
    }

    onSubmit(): void {
        this.submitted = true;
        if (this.registrationForm.valid) {
            this.authService.postRegistration(this.registrationForm.value).subscribe({
                next: (response: User) => {
                    console.log('Registration successful', response);
                },
                error: (error) => {
                    console.error('Registration failed', error);
                }
            })
        }
    }

}
