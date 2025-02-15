import {MRT_ColumnDef} from "mantine-react-table";

export interface ExpenseResponse {
    expenseId: string;
    name: string;
    note: string;
    category: string;
    date: string | Date;
    amount: number;
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
        header: 'Category'
    },
    {
        accessorKey: 'date',
        header: 'Date',
        filterVariant: "date-range",
        sortingFn: "datetime",
        enableColumnFilterModes: false,
        Cell: ({cell}) => cell.getValue<Date>()?.toLocaleDateString()
    },
    {
        accessorKey: 'amount',
        header: 'Amount'
    }
];