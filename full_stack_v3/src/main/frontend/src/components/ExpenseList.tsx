import {MantineReactTable, MRT_ColumnDef, MRT_TableInstance, useMantineReactTable} from "mantine-react-table";
import {ExpenseResponse} from "../domain/Types.ts";
import {DashboardStatus} from "./DashboardStatus.tsx";
import {MouseEvent} from "react";
import {useNavigate} from "react-router-dom";

export interface ExpenseListProps {
    columns: MRT_ColumnDef<ExpenseResponse>[];
    data: ExpenseResponse[];
}

export const ExpenseList = ({columns, data}: ExpenseListProps) => {

    const navigateTo = useNavigate();
    const priorityColumns = ['ID', 'Name', 'Amount'];

    columns
        .sort((a, b) => {
            const comparisonValue = a.header.localeCompare(b.header);
            return priorityColumns.includes(a.header) && priorityColumns.includes(b.header)
                ? priorityColumns.indexOf(a.header) - priorityColumns.indexOf(b.header)
                : priorityColumns.includes(a.header)
                    ? -1
                    : priorityColumns.includes(b.header)
                        ? 1
                        : comparisonValue;
        });

    const table: MRT_TableInstance<ExpenseResponse> = useMantineReactTable({
        columns,
        data,
        mantineTableBodyRowProps: ({row}) => ({
            onClick: (_event: MouseEvent<HTMLTableRowElement>) => (
                navigateTo(`/view/${row.original.expenseId}`, {
                    state: {expense: row.original}
                })
            ),
            sx: {cursor: "pointer"}
        }),
        initialState: {
            columnOrder: [
                ...columns.map(column => column.header),
                'mrt-row-select'
            ],
        },
        enableRowSelection: true,
        enableStickyHeader: true,
    });

    const totalExpenses = table.getFilteredRowModel().rows
        .map(row => parseFloat(row.original.amount))
        .reduce((acc, curr) => acc + curr, 0);

    return <div>
        <DashboardStatus totalExpenses={totalExpenses}/>
        <MantineReactTable
            table={table}
        />
    </div>;
}