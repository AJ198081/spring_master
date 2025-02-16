import {ReactNode, useMemo} from "react";
import {columnsDescription, ExpenseResponse} from "../domain/Types.ts";
import {ExpenseList} from "../components/ExpenseList.tsx";
import {MRT_ColumnDef} from "mantine-react-table";
import {useExpenses} from "../useExpenses.ts";
import {Spinner} from "../components/common/Spinner.tsx";

export const Dashboard = (): ReactNode => {

    const columns = useMemo<MRT_ColumnDef<ExpenseResponse>[]>(() => columnsDescription, []);

    const {isLoading, expenses, errors} = useExpenses();

    return <div className={'container'}>
        {
            isLoading
                ? <Spinner textColor={'text-primary'}/>
                : errors === null
                    ? <ExpenseList columns={columns} data={expenses}/>
                    : <p>`Error fetching data - ${errors.name} occurred with ${errors.message}`</p>
        }
    </div>;
}