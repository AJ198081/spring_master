import dayjs from "dayjs";

console.log(navigator.languages);

console.log(navigator.language);

console.log(navigator)

navigator.language

Intl.DateTimeFormat().resolvedOptions().timeZone;

let currencyName;

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

const currencyFormatter = new Intl.NumberFormat(navigator.language, {style: "currency", currency: currencyName});
currencyFormatter.format(123456.789);

console.log(currencyName);


const number = 123456.789;

console.log(
    new Intl.NumberFormat("de-DE", {style: "currency", currency: "EUR"}).format(
        number,
    ),
);

console.log(
    new Intl.NumberFormat("en-IN", {style: "currency", currency: "INR"}).format(
        number,
    ),
);

console.log(
    new Intl.NumberFormat("en-GB", {
        style: "currency",
        currency: "GBP",
        maximumFractionDigits: 2,
        currencyDisplay: 'symbol'
    }).format(number)
);


dayjs().format('DD/MM/YYYY');


