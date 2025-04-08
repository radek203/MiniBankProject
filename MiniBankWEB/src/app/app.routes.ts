import {RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';
import {authGuard, authGuardNotAuth} from './architecture/auth.guard';

export const routes: Routes = [
    {'path': '', redirectTo: 'dashboard', pathMatch: 'full'},
    {
        'path': '', canActivate: [authGuard], children: [
            {'path': 'dashboard', loadComponent: () => import('./dashboard/dashboard.component').then(file => file.DashboardComponent)},
            {'path': 'cards', loadComponent: () => import('./cards/cards.component').then(file => file.CardsComponent)},
            {'path': 'settings', loadComponent: () => import('./settings/settings.component').then(file => file.SettingsComponent)}
        ]
    },
    {
        'path': '', canActivate: [authGuardNotAuth], children: [
            {'path': 'login', loadComponent: () => import('./login/login.component').then(file => file.LoginComponent)},
            {'path': 'register', loadComponent: () => import('./register/register.component').then(file => file.RegisterComponent)}
        ]
    },
    {'path': '**', redirectTo: 'dashboard'}
];

@NgModule({
    imports: [RouterModule.forRoot(routes, {scrollPositionRestoration: 'enabled'})],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
