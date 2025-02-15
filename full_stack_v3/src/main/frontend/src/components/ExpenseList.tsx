import {MantineReactTable, MRT_ColumnDef, MRT_TableInstance, useMantineReactTable} from "mantine-react-table";
import {ExpenseResponse} from "../domain/Types.ts";

export interface ExpenseListProps {
    columns: MRT_ColumnDef<ExpenseResponse>[];
    data: ExpenseResponse[];
}

export const ExpenseList = ({columns, data}: ExpenseListProps) => {

    const table: MRT_TableInstance<ExpenseResponse> = useMantineReactTable({columns, data});

    return <div>
        <MantineReactTable
            table={table}
        />
    </div>;
}