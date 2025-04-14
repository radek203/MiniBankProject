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
