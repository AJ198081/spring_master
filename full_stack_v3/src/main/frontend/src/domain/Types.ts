import {MRT_ColumnDef} from "mantine-react-table";
import {currencyFormatter, dateFormatter} from "../utils/Formatter.ts";
import {date, number, object, string} from "yup";
import dayjs from "dayjs";


export interface ExpenseRequest {
    name: string;
    note: string;
    category: string;
    date: string | Date;
    amount: string;
}

export interface ExpenseResponse extends ExpenseRequest {
    expenseId: string;
}

export const categoryOptions = [
    'Food',
    'Transportation',
    'Entertainment',
    'Shopping',
    'Health',
    'Other'
];

export const expenseSchemaValidations = object({
    name: string()
        .min(2, 'Expense name can not be less than 2 character')
        .max(255, 'Expense name can not be more than 255 character')
        .required('Expense name is required'),

    note: string()
        .nullable(),

    category: string()
        .required('Category is required'),

    amount: number()
        .required('Expense amount is required'),

    date: date()
        .default(() => dayjs().toDate())
        .max(dayjs().add(1, 'year').toDate(), 'Expense date can not be more than a year in future')
        .required('Expense Date is required')
});

export const columnsDescription: MRT_ColumnDef<ExpenseResponse>[] = [
    {
        accessorKey: 'expenseId',
        header: 'ID',
    },
    {
        accessorKey: 'name',
        header: 'Name'
    },
    {
        accessorKey: 'note',
        header: 'Note / Description '
    },
    {
        accessorKey: 'category',
        header: 'Category',
        filterVariant: "text",
        sortingFn: "textCaseSensitive",
    },
    {
        accessorKey: 'date',
        header: 'Date',
        filterVariant: "date-range",
        sortingFn: "datetime",
        enableColumnFilterModes: false,
        Cell: ({cell}) => {
            return dateFormatter.format(cell.getValue<Date>());
        }
    },
    {
        accessorKey: 'amount',
        header: 'Amount',
        filterVariant: "range",
        sortingFn: "currency",
        Cell: ({cell}) => {
            return currencyFormatter.format(cell.getValue<number>());
        }
    }
];