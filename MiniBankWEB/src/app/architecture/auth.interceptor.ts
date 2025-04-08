import {HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {AuthService} from "../services/auth.service";
import {pageConfig} from "../app.component";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

    constructor(private authService: AuthService) {
    }

    intercept(req: HttpRequest<any>, next: HttpHandler) {
        const rootUrl = pageConfig.api;

        const apiRootRequest = req.clone({
            url: rootUrl + req.url
        });
        if (this.authService.isAuth) {
            const token = this.authService.loginToken;
            const modifiedReq = apiRootRequest.clone({
                headers: apiRootRequest.headers.set('Authorization', 'Bearer ' + token)
            });
            return next.handle(modifiedReq);
        }
        return next.handle(apiRootRequest);
    }
}
