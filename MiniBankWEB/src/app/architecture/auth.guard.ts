import {ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot} from "@angular/router";
import {inject} from "@angular/core";
import {AuthService} from "../services/auth.service";

export const authGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
    const authService = inject(AuthService);
    if (authService.isAuth) {
        return true;
    }
    inject(Router).navigate(["/login"]);
    return false;
};

export const authGuardNotAuth: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
    const authService = inject(AuthService);
    if (!authService.isAuth) {
        return true;
    }
    inject(Router).navigate(["/dashboard"]);
    return false;
};
