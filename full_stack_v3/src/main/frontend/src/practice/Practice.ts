import dayjs from "dayjs";

console.log(navigator)
console.log(navigator.languages);
console.log(navigator.language);

const timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;
console.log(timeZone);

const currencyName = {
    'en-GB': 'GBP',
    'en-IN': 'INR',
    'en-AU': 'AUD',
    'en-US': 'USD',
    'hi': 'INR'
}[navigator.language] || 'AUD';

const currencyFormatter = new Intl.NumberFormat(navigator.language, {style: "currency", currency: currencyName});
console.log(currencyFormatter.format(123456.789));

console.log(currencyName);

const number = 123456.789565;

console.log(
    new Intl.NumberFormat("de-DE",
        {
            style: "currency",
            currency: "EUR"
        }).format(number)
);

console.log(
    new Intl.NumberFormat("en-IN",
        {
            style: "currency",
            currency: "INR"
        }
    ).format(number)
);

console.log(
    new Intl.NumberFormat("en-GB", {
        style: "currency",
        currency: "GBP",
        maximumFractionDigits: 2,
        currencyDisplay: 'symbol'
    }).format(number)
);


console.log(dayjs().format('DD/MM/YYYY'));

const formattedDateUsingBrowserLocale = Intl.DateTimeFormat().format();
console.log(formattedDateUsingBrowserLocale);

const formattedDateInIndia = new Date(1740019133000).toLocaleDateString('en-IN');
console.log(formattedDateInIndia);

const formattedDateInUS = new Date(1740019133000).toLocaleDateString('en-US');
console.log(formattedDateInUS);

const getUniqueElementsFromArray = (inputArray: Array<number | string>) => {
    return inputArray.reduce((acc, curr) => {
        return acc.includes(curr)
            ? acc
            : acc.concat(curr);

    }, [] as Array<number | string>)
}

console.log(getUniqueElementsFromArray([2, 3, 3, 5, 15, 3, 6, 6, 5, 3, 2, 5]));