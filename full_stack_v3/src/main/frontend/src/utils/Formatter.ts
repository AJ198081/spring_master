import dayjs from "dayjs";
import customParseFormat from "dayjs/plugin/customParseFormat";
import {dateFormat} from "../domain/Types.ts";

dayjs.extend(customParseFormat);

export function stringToDateObject(dateString: string, format = dateFormat): Date {
    return dayjs(dateString, format).toDate()
}

export function dateToString(date: Date, format = dateFormat): string {
    return dayjs(date).format(format)
}

let currencyName: string;

switch (navigator.language) {
    case 'en-GB':
        currencyName = 'GBP'
        break;
    case 'en-IN':
        currencyName = 'INR';
        break;
    case 'en-AU':
        currencyName = 'AUD'
        break;
    case 'en-US':
        currencyName = 'USD'
        break;
    default:
        currencyName = 'AUD';
}

export const currencyFormatter = new Intl.NumberFormat(navigator.language, {style: "currency", currency: currencyName});

export const getBrowserTimeZone = () => {
    return Intl.DateTimeFormat().resolvedOptions().timeZone;
}

export const dateFormatter = new Intl.DateTimeFormat(navigator.language, {
    dateStyle: 'short',
    // timeStyle: 'full',
    timeZone: getBrowserTimeZone()
});

export const numberFormatter = new Intl.NumberFormat(navigator.language, {
    style: 'decimal',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
});