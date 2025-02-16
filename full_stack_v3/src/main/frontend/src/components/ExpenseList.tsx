import {MantineReactTable, MRT_ColumnDef, MRT_TableInstance, useMantineReactTable} from "mantine-react-table";
import {ExpenseResponse} from "../domain/Types.ts";
import {DashboardStatus} from "./DashboardStatus.tsx";

export interface ExpenseListProps {
    columns: MRT_ColumnDef<ExpenseResponse>[];
    data: ExpenseResponse[];
}

export const ExpenseList = ({columns, data}: ExpenseListProps) => {

    const table: MRT_TableInstance<ExpenseResponse> = useMantineReactTable({columns, data});

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