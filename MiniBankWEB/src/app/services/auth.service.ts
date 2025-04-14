import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {Login} from "../models/login.model";
import {TokenResponse} from "../models/token.model";
import {Registration} from "../models/registration.model";
import {emptyUser, User} from "../models/user.model";
import {Subject} from "rxjs";
import {UserUpdate} from '../models/user-update.model';

@Injectable({providedIn: 'root'})
export class AuthService {
    loginToken: string = "";
    isAuth: boolean = false;
    user: User = {...emptyUser};
    alreadyChecked: boolean = false;
    userHasChanged = new Subject<User>();

    constructor(private httpService: HttpClient, private router: Router) {
    }

    checkAuth() {
        if (this.alreadyChecked) {
            return;
        }
        const token = localStorage.getItem('token');
        const user = localStorage.getItem('user');
        if (token && user) {
            this.isAuth = true;
            this.loginToken = token;
            this.alreadyChecked = true;
            this.postRefreshToken().subscribe({
                next: (res: TokenResponse) => {
                    this.setUser(JSON.parse(user));
                    this.handleLogin(res.token);
                },
                error: (error) => {
                    this.logout();
                }
            });
        }
    }

    handleLogin(token: string) {
        return new Promise<void>((resolve) => {
            localStorage.setItem('token', token);

            this.loginToken = token;
            this.isAuth = true;

            resolve();
        });
    }

    setUser(user: User) {
        this.user = user;
        localStorage.setItem('user', JSON.stringify(user));
        this.userHasChanged.next(user);
    }

    logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');

        this.loginToken = '';
        this.isAuth = false;
        this.user = {...emptyUser};

        this.router.navigate(["/login"]);
    }

    postRegistration(userForRegister: Registration) {
        return this.httpService.post<User>('auth/register', userForRegister);
    }

    postLogin(userForLogin: Login) {
        return this.httpService.post<TokenResponse>('auth/login', userForLogin);
    }

    postRefreshToken() {
        return this.httpService.post<TokenResponse>('auth/refresh', {
            token: this.loginToken
        });
    }

    getUserByToken(token: string) {
        return this.httpService.post<User>('auth/validate', {'token': token});
    }

    updateUser(userId: number, user: UserUpdate) {
        return this.httpService.put<User>('auth/update/' + userId, user);
    }

}
