import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
    name: 'formatAccountNumber'
})
export class FormatAccountNumberPipe implements PipeTransform {

    transform(value: string): string {
        if (!value || value.length <= 2) return value;

        const prefix = value.slice(0, 2);
        const rest = value.slice(2);

        const grouped = rest.replace(/(.{4})/g, '$1 ').trim();

        return `${prefix} ${grouped}`;
    }

}
