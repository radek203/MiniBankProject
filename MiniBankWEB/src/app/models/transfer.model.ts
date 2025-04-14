export enum TransferStatus {
    STARTED = 'STARTED',
    COMPLETED = 'COMPLETED',
    FAILED = 'FAILED',
}

export interface Transfer {
    id: string;
    fromAccount: string;
    toAccount: string;
    amount: number;
    status: TransferStatus;
    fromBranchId: number;
    toBranchId: number;
    date: number;
}
