import {
    MantineReactTable,
    MRT_ColumnDef,
    MRT_TableInstance,
    MRT_VisibilityState,
    useMantineReactTable
} from "mantine-react-table";
import {ExpenseResponse} from "../domain/Types.ts";
import {DashboardStatus} from "./DashboardStatus.tsx";
import {MouseEvent, useState} from "react";
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
    const [visibleColumns, _] = useState<MRT_VisibilityState>(
        Object.fromEntries(columns.map(column => [column.header, true]))
    );
    const priorityColumns = ['ID', 'Name', 'Amount'];

    console.log(JSON.stringify(visibleColumns, null, 4));

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
        enableHiding: true,
        initialState: {
            columnOrder: [
                ...columns.map(column => column.header),
            ],
        },
            /*columnVisibility: visibleColumns
        },
        state:{
            columnVisibility: visibleColumns
        },
        onColumnVisibilityChange: setVisibleColumns,
        enableHiding: true,*/
        mantineTableBodyRowProps: ({row}) => ({
            onClick: (_event: MouseEvent<HTMLTableRowElement>) => (
                navigateTo(`/view/${row.original.expenseId}`, {
                    state: {expense: row.original}
                })
            ),
            sx: {cursor: "pointer"}
        }),
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