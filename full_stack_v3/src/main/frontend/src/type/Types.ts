export interface ExpenseResponse {
    expenseId: string;
    name: string;
    note: string;
    category: string;
    date: string;
    amount: number;
}

export const columnsDescription = [
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