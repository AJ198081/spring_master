import {ReactNode, useMemo} from "react";

import {expensesList} from '../test-data/ExpensesList'
import {MRT_ColumnDef} from "mantine-react-table";
import {columnsDescription, ExpenseResponse} from "../type/Types.ts";
import {ExpenseList} from "../components/ExpenseList.tsx";

export const Dashboard = (): ReactNode => {

    const data = useMemo(() => expensesList, []);
    const columns = useMemo<MRT_ColumnDef<ExpenseResponse>[]>(() => columnsDescription, []);

    return <div className={'my-2'}>
        <ExpenseList columns={columns} data={data} />
    </div>;
}