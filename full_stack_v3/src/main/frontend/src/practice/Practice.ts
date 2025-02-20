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


const data = [
    {
        "id": "0",
        "index": 0,
        "original": {
            "expenseId": "b86459ec-0b21-40e3-a14b-afcfcd66853d",
            "name": "Expense-KRR",
            "category": "Transportation",
            "note": "Dicta sit distinctio quia et explicabo.",
            "date": "2025-02-16T13:00:00.000Z",
            "amount": 978.68
        },
        "depth": 0,
        "_valuesCache": {
            "expenseId": "b86459ec-0b21-40e3-a14b-afcfcd66853d",
            "name": "Expense-KRR",
            "amount": 978.68,
            "category": "Transportation",
            "date": "2025-02-16T13:00:00.000Z",
            "note": "Dicta sit distinctio quia et explicabo."
        },
        "_uniqueValuesCache": {},
        "subRows": [],
        "columnFilters": {
            "amount": true,
            "date": true
        },
        "columnFiltersMeta": {},
        "_groupingValuesCache": {}
    },
    {
        "id": "1",
        "index": 1,
        "original": {
            "expenseId": "b37729cf-b41b-4177-9957-cb7fe01c3927",
            "name": "Expense-YNA",
            "category": "Utilities",
            "note": "Non nostrum distinctio quas corrupti.",
            "date": "2025-02-14T13:00:00.000Z",
            "amount": 943.89
        },
        "depth": 0,
        "_valuesCache": {
            "expenseId": "b37729cf-b41b-4177-9957-cb7fe01c3927",
            "name": "Expense-YNA",
            "amount": 943.89,
            "category": "Utilities",
            "date": "2025-02-14T13:00:00.000Z",
            "note": "Non nostrum distinctio quas corrupti."
        },
        "_uniqueValuesCache": {},
        "subRows": [],
        "columnFilters": {
            "amount": true,
            "date": true
        },
        "columnFiltersMeta": {},
        "_groupingValuesCache": {}
    }
];

data.map(row => row.original);


new Date(1740019133000);