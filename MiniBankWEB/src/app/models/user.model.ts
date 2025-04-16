export enum UserRole {
    ADMIN = 'ADMIN',
    USER = 'USER'
}

export interface User {
    id: number;
    username: string;
    email: string;
    avatar: string;
    role: UserRole;
    createdAt: string;
}

export const emptyUser: User = {
    id: -1,
    username: '',
    email: '',
    avatar: '',
    role: UserRole.USER,
    createdAt: '',
}

export interface UserUpdate {
    username: string;
    email: string;
    avatar: string;
    oldPassword: string;
    password: string;
}
