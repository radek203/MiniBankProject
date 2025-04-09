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

export function getBranchUrl(id: number): string {
    const branchUrls = [
        'krakow',
        'warsaw'
    ];
    return branchUrls[(id - 1)];
}
