import {MantineReactTable, MRT_ColumnDef, MRT_TableInstance, useMantineReactTable} from "mantine-react-table";
import {ExpenseResponse} from "../domain/Types.ts";
import {DashboardStatus} from "./DashboardStatus.tsx";
import {MouseEvent} from "react";
import {useNavigate} from "react-router-dom";
import {Button} from "@mantine/core";
import toast from "react-hot-toast";

export interface ExpenseListProps {
    columns: MRT_ColumnDef<ExpenseResponse>[];
    data: ExpenseResponse[];
    userName: string;
}

export const ExpenseList = ({columns, data, userName}: ExpenseListProps) => {

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
        renderTopToolbarCustomActions: ({table}) => (
            <Button
                className={'bg-success-subtle text-dark'}
                onClick={() => {
                    const selectedRows = table.getSelectedRowModel().rows;
                    const extractedRows = selectedRows.map(row => row.original);
                    navigator.clipboard.writeText(JSON.stringify(extractedRows))
                        .then(() => toast.success('Selected rows copied to clipboard!', {
                            position: 'top-right',
                            duration: 4000
                        }));
                }}
            >
                {'Copy selected row/s to clipboard'}
            </Button>
        )
    });

    const totalExpenses = table.getFilteredRowModel().rows
        .map(row => row.original.amount)
        .reduce((acc, curr) => acc + curr, 0);

    return <div>
        <DashboardStatus userName={userName} totalExpenses={totalExpenses}/>
        <MantineReactTable
            table={table}
        />
    </div>;
}