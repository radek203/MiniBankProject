import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgIf} from '@angular/common';
import {User} from '../models/user.model';
import {AuthService} from '../services/auth.service';
import {TokenResponse} from '../models/token.model';
import {Router} from '@angular/router';
import {NotificationService} from '../services/notification.service';

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

    constructor(private fb: FormBuilder, private authService: AuthService, private router: Router, private notificationService: NotificationService) {
    }

    ngOnInit(): void {
        this.loginForm = this.fb.group({
            username: ['', [Validators.required]],
            password: ['', [Validators.required]],
        });
    }

    onSubmit(): void {
        if (this.loginForm.valid) {
            this.authService.postLogin(this.loginForm.value).subscribe({
                next: (response: TokenResponse) => {
                    this.authService.handleLogin(response.token).then(() => {
                        this.loadUserData(response.token);
                        this.router.navigate(["/panel"]);
                    });
                },
                error: (error) => {
                    this.notificationService.addNotification(error);
                }
            });
        } else {
            this.loginForm.markAllAsTouched();
        }
    }

    loadUserData(token: string) {
        this.authService.getUserByToken(token).subscribe({
            next: (res: User) => {
                this.authService.setUser(res);
                this.notificationService.clearNotifications();
            },
            error: (error) => {
                this.notificationService.addNotification(error);
            }
        });
    }

}
