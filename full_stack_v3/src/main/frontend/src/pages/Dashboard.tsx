import {ReactNode, useMemo} from "react";
import {columnsDescription, ExpenseResponse} from "../domain/Types.ts";
import {ExpenseList} from "../components/ExpenseList.tsx";
import {MRT_ColumnDef} from "mantine-react-table";
import {useExpenses} from "../useExpenses.ts";

export const Dashboard = (): ReactNode => {

    const columns = useMemo<MRT_ColumnDef<ExpenseResponse>[]>(() => columnsDescription, []);

    const {isLoading, expenses, errors} = useExpenses();

    return <div className={'container'}>
        {
            isLoading
                ? <p>Awaiting .....</p>
                : errors === null
                    ? <ExpenseList columns={columns} data={expenses}/>
                    : <p>`Error fetching data - ${errors.name} occurred with ${errors.message}`</p>
        }
    </div>;
}