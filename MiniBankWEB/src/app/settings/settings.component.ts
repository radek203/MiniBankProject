import {Component} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgIf} from '@angular/common';
import {AuthService} from '../services/auth.service';
import {NotificationService} from '../services/notification.service';

@Component({
    selector: 'app-settings',
    imports: [
        ReactiveFormsModule,
        NgIf
    ],
    templateUrl: './settings.component.html',
    styleUrl: './settings.component.scss'
})
export class SettingsComponent {
    userForm!: FormGroup;

    constructor(private fb: FormBuilder, private authService: AuthService, private notificationService: NotificationService) {
    }

    ngOnInit(): void {
        this.userForm = this.fb.group({
            email: [this.authService.user.email, [Validators.required, Validators.email]],
            username: [this.authService.user.username, [Validators.required, Validators.minLength(3)]],
            oldPassword: ['', [Validators.required, Validators.minLength(6)]],
            password: ['', [Validators.required, Validators.minLength(6)]]
        });
    }

    // Submit form
    submitForm(): void {
        if (this.userForm.valid) {
            this.authService.updateUser(this.authService.user.id, {
                username: this.userForm.value['username'], email: this.userForm.value['email'],
                oldPassword: this.userForm.value['oldPassword'], password: this.userForm.value['password']
            }).subscribe({
                next: (response) => {
                    this.authService.logout();
                },
                error: (error) => {
                    this.notificationService.addNotification(error);
                }
            });
        } else {
            this.userForm.markAllAsTouched(); // To show errors
        }
    }
}
