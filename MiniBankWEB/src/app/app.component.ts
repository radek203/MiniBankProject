import {Component, OnInit} from '@angular/core';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {Config} from './models/config.model';
import {AuthService} from './services/auth.service';
import {Title} from '@angular/platform-browser';
import {NotificationsComponent} from './notifications/notifications.component';

@Component({
    selector: 'app-root',
    imports: [RouterOutlet, RouterLink, RouterLinkActive, NotificationsComponent],
    templateUrl: './app.component.html',
    styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
    title = 'MiniBankWEB';


    constructor(protected authService: AuthService, private titleService: Title) {
        this.titleService.setTitle(pageConfig.name);
    }

    ngOnInit(): void {
        this.authService.checkAuth();
    }

    onLogout(): void {
        this.authService.logout();
    }

    protected readonly pageConfig = pageConfig;
}

export const pageConfig: Config = {
    name: "MiniBankProject",
    home: "http://localhost:4200/home",
    version: "1.0.0 Beta",
    api: "http://localhost:8060/"
}
