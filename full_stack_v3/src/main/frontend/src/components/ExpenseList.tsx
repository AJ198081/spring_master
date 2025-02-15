import {MantineReactTable, MRT_ColumnDef, useMantineReactTable} from "mantine-react-table";
import {ExpenseResponse} from "../type/Types.ts";

export interface ExpenseListProps {
    columns: MRT_ColumnDef<ExpenseResponse>[];
    data: ExpenseResponse[];
}

export const ExpenseList = ({columns, data}: ExpenseListProps) => {

    const table = useMantineReactTable({columns, data});

    return <div className={'my-2'}><MantineReactTable table={table}/></div>;

}