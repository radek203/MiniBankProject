import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgIf} from '@angular/common';
import {User} from '../models/user.model';
import {AuthService} from '../services/auth.service';
import {TokenResponse} from '../models/token.model';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login',
    imports: [
        ReactiveFormsModule,
        NgIf
    ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit {

    loginForm!: FormGroup;
    submitted = false;

    constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {}

    ngOnInit(): void {
        this.loginForm = this.fb.group({
            username: ['', [Validators.required]],
            password: ['', [Validators.required]],
        });
    }

    onSubmit(): void {
        this.submitted = true;
        if (this.loginForm.valid) {
            this.authService.postLogin(this.loginForm.value).subscribe({
                next: (response: TokenResponse) => {
                    this.authService.handleLogin(response.token).then(() => {
                        this.loadUserData(response.token);
                        this.router.navigate(["/panel"]);
                    });
                },
                error: (error) => {
                    console.error('Registration failed', error);
                }
            })
        }
    }

    loadUserData(token: string) {
        this.authService.getUserByToken(token).subscribe({
            next: (res: User) => {
                this.authService.setUser(res);
            },
            error: (error) => {
                console.error('Failed to load user data', error);
            }
        });
    }

}
