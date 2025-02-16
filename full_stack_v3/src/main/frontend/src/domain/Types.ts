import {MRT_ColumnDef} from "mantine-react-table";
import {currencyFormatter, dateFormatter} from "../utils/Formatter.ts";


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


export const columnsDescription: MRT_ColumnDef<ExpenseResponse>[] = [
    {
        accessorKey: 'expenseId',
        header: 'ID'
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
        filterVariant: "multi-select",
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