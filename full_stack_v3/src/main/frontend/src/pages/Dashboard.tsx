import {ReactNode, useEffect, useMemo, useState} from "react";
import {columnsDescription, ExpenseResponse} from "../domain/Types.ts";
import {ExpenseList} from "../components/ExpenseList.tsx";
import {MRT_ColumnDef} from "mantine-react-table";
import {AxiosInstance} from "../service/api-client.ts";

export const Dashboard = (): ReactNode => {

    const columns = useMemo<MRT_ColumnDef<ExpenseResponse>[]>(() => columnsDescription, []);

    const [expenses, setExpenses] = useState<ExpenseResponse[]>([]);
    const [errors, setErrors] = useState<Error | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        setIsLoading(true);
        AxiosInstance.get('/api/v1/expenses')
            .then(response => {
                if (response.status === 200) {
                    setExpenses(response.data);
                }
            })
            .catch(error => setErrors({name: "fetch exception",message: error.response.data}))
            .finally(() => setIsLoading(false));
    }, []);

    return <div className={'my-2'}>
        {
            isLoading
                ? <p>Awaiting .....</p>
                : errors === null
                    ? <ExpenseList columns={columns} data={expenses}/>
                    : <p>`Error fetching data - ${errors.name} occurred with ${errors.message}`</p>
        }
    </div>;
}