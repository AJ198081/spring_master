import dayjs from "dayjs";
import customParseFormat from "dayjs/plugin/customParseFormat";
import utc from "dayjs/plugin/utc";
import {dateFormat} from "../domain/Types.ts";

dayjs.extend(customParseFormat);
dayjs.extend(utc);

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
    dateStyle: 'full',
    // timeStyle: 'full',
    timeZone: getBrowserTimeZone()
});

export const numberFormatter = new Intl.NumberFormat(navigator.language, {
    style: 'decimal',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
});

const date = dayjs('2021-12-13', 'YYYY-MM-DD', true).toDate();
dayjs(date).format();
dayjs(date).startOf('day').toDate();
const midnightUtcDate = dayjs(date).utc(false).startOf('day').toDate();
midnightUtcDate.toString();
// dayjs(date).utcOffset()
dateFormatter.format(dayjs(date).startOf('day').utc(true).toDate());

dateFormatter.format(date);