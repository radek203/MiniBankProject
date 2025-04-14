import {Component} from '@angular/core';
import {NotificationService} from '../services/notification.service';

@Component({
    selector: 'app-notifications',
    imports: [],
    templateUrl: './notifications.component.html',
    styleUrl: './notifications.component.scss'
})
export class NotificationsComponent {

    constructor(protected notificationService: NotificationService) {
    }

}
