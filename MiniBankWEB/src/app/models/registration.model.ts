export interface Registration {
    username: string;
    password: string;
    email: string;
}

export const emptyRegistration: Registration = {
    username: '',
    password: '',
    email: ''
}
