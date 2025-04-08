export enum UserRole {
    ADMIN = 'ADMIN',
    USER = 'USER'
}

export interface User {
    id: number;
    username: string;
    email: string;
    role: UserRole;
    createdAt: string;
}

export const emptyUser: User = {
    id: -1,
    username: '',
    email: '',
    role: UserRole.USER,
    createdAt: '',
}
