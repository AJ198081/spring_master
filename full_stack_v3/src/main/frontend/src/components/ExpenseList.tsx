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

    const table: MRT_TableInstance<ExpenseResponse> = useMantineReactTable({
        columns,
        data,
        mantineTableBodyRowProps: ({row}) => ({
            onClick: (_event: MouseEvent<HTMLTableRowElement>) => (
                /*<Link to={{
                    pathname: `/expenses/${row.original.expenseId}`,
                    search: `?name=${row.original.name}`,
                }}
                />*/
                navigateTo(`/view/${row.original.expenseId}`, {
                    state: {expense: row.original}
                })
            ),
            sx: {cursor: "pointer"}
        }),
    });

    const totalExpenses = table.getFilteredRowModel().rows
        .map(row => row.original.amount)
        .reduce((acc, curr) => acc + curr, 0);

    return <div>
        <DashboardStatus totalExpenses={totalExpenses}/>
        <MantineReactTable
            table={table}
        />
    </div>;
}