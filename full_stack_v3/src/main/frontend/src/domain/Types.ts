import {MRT_ColumnDef} from "mantine-react-table";

export interface ExpenseResponse {
    expenseId: string;
    name: string;
    note: string;
    category: string;
    date: string;
    amount: number;
}

export interface Error {
    message: string;
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
        header: 'Note'
    },
    {
        accessorKey: 'category',
        header: 'Category'
    },
    {
        accessorKey: 'date',
        header: 'Date'
    },
    {
        accessorKey: 'amount',
        header: 'Amount'
    }
];