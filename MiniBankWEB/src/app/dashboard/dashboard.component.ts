import {Component} from '@angular/core';
import {AccountsComponent} from './accounts/accounts.component';
import {TransferComponent} from './transfer/transfer.component';
import {AuthService} from '../services/auth.service';
import {DepositComponent} from './deposit/deposit.component';
import {WithdrawComponent} from './withdraw/withdraw.component';

@Component({
    selector: 'app-dashboard',
    imports: [
        AccountsComponent,
        TransferComponent,
        DepositComponent,
        WithdrawComponent
    ],
    templateUrl: './dashboard.component.html',
    styleUrl: './dashboard.component.scss'
})
export class DashboardComponent {

    constructor(protected authService: AuthService) {
    }

}
