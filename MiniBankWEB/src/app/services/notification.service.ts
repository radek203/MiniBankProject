import {Injectable} from '@angular/core';
import {getMessages} from '../app.utils';

@Injectable({providedIn: 'root'})
export class NotificationService {

    errors: string[] = [];

    addNotification(error: any): void {
        const messages: string[] = getMessages(error);
        messages.forEach(message => {
            this.errors.push(message);
        });
    }

    removeNotification(index: number): void {
        if (index > -1) {
            this.errors.splice(index, 1);
        }
    }

    clearNotifications(): void {
        this.errors = [];
    }

}
