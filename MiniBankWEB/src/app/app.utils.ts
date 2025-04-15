export function getBranchName(id: number): string {
    const branchNames = [
        'Krakow',
        'Warsaw'
    ];
    return branchNames[(id - 1)];
}

export function getBranchShortName(id: number): string {
    const branchShortNames = [
        'krk',
        'waw'
    ];
    return branchShortNames[(id - 1)];
}

export function getBranchUrl(id: number | undefined): string {
    const branchUrls = [
        'krakow',
        'warsaw'
    ];
    if (id === undefined) {
        return '';
    }
    return branchUrls[(id - 1)];
}

function getErrorText(error: string) {
    switch (error) {
        case "error/invalid-transfer":
            return "An error occurred while transferring the money. Please try again.";
        case "error/transfer-not-found":
            return "Transfer not found or an error occurred.";
        case "error/balance-change-not-found":
            return "Balance change not found or an error occurred.";
        case "error/invalid-amount":
            return "Invalid amount. Please enter a valid amount.";
        case "error/account-not-found":
            return "Account not found. Please check the account number.";
        case "error/insufficient-balance":
            return "Insufficient balance. Please check your account balance.";
        case "error/invalid-account":
            return "Invalid account. Please check the account number.";
        case "error/username-required":
            return "Username is required. Please enter your username.";
        case "error/password-required":
            return "Password is required. Please enter your password.";
        case "error/email-required":
            return "Email is required. Please enter your email address.";
        case "error/old-password-required":
            return "Old password is required. Please enter your old password.";
        case "error/accounts-not-found":
            return "Accounts not found. Please check your account details.";
        case "error/bank-not-found":
            return "Bank not found. Please check the bank details.";
        case "error/card-not-found":
            return "Card not found. Please check the card details.";
        case "error/invalid-cvv":
            return "Invalid CVV. Please check the CVV code.";
        case "error/invalid-expiration-date":
            return "Invalid expiration date. Please check the expiration date.";
        case "error/card-expired":
            return "Card has expired.";
        case "error/payment-failed":
            return "Payment failed. Please try again later.";
        case "error/invalid-password":
            return "Invalid password. Please check your password.";
        case "error/token-invalid":
            return "Token is invalid or expired.";
        case "error/user-already-existed":
            return "User already exists. Please use a username.";
        case "error/user-not-found":
            return "User not found. Please check your username.";
        case "error/credit-card-creation":
            return "Credit card creation failed. Please try again.";
    }
    return "Undefined error occurred";
}

export function getMessages(error: any) {
    const errors: string[] = [];
    if (error.error && error.error.message) {
        error.error.message.split(";").forEach((err: string) => {
            errors.push(getErrorText(err));
        });
    } else {
        errors.push(getErrorText(""));
    }
    return errors;
}
