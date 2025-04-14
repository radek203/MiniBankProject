export enum BalanceChangeStatus {
    STARTED = 'STARTED',
    COMPLETED = 'COMPLETED'
}

export interface BalanceChange {
    id: string;
    account: string;
    amount: number;
    status: BalanceChangeStatus;
    branchId: number;
    date: number;
}
