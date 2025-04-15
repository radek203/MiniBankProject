export interface Client {
    id: string;
    firstName: string;
    lastName: string;
    phone: string;
    address: string;
    city: string;
    branch: number;
    accountNumber: string;
    balance: number;
    balanceReserved: number;
}

export const emptyClient: Client = {
    id: '',
    firstName: '',
    lastName: '',
    phone: '',
    address: '',
    city: '',
    branch: 0,
    accountNumber: '',
    balance: 0,
    balanceReserved: 0
}
