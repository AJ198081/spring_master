import {ReactNode, useEffect, useMemo, useState} from "react";
import toast from 'react-hot-toast';
import {columnsDescription, ExpenseResponse} from "../domain/Types.ts";
import {Expenses} from "../components/Expenses.tsx";
import {MRT_ColumnDef} from "mantine-react-table";
import {Spinner} from "../components/common/Spinner.tsx";
import {useNavigate} from "react-router-dom";
import {AxiosInstance} from "../service/api-client.ts";
import {AxiosError, AxiosResponse} from "axios";
import {getUserName} from "../utils/Utils.ts";
import {useValidatedJwt} from "../hooks/useValidatedJwt.ts";

export const Dashboard = (): ReactNode => {

    const columns = useMemo<MRT_ColumnDef<ExpenseResponse>[]>(() => columnsDescription, []);

    const navigateTo = useNavigate();

    const [isLoading, setIsLoading] = useState(true);
    const [errors, setErrors] = useState<Error | null>(null);
    const [expenses, setExpenses] = useState<ExpenseResponse[]>([]);

    const {token} = useValidatedJwt()

    useEffect(() => {

        if (token !== null) {
            AxiosInstance.get('/api/v1/expenses')
                    .then((response: AxiosResponse<ExpenseResponse[]>) => {
                        if (response.status === 200) {
                            const parsedExpenses = response.data;
                            setExpenses(parsedExpenses);
                        }
                    })
                    .catch(error => {
                        if (error instanceof AxiosError) {
                            toast.error((error as AxiosError).message);
                            if ((error as AxiosError).status === 401) {
                                // navigateTo('/login');
                            }
                        }
                        setErrors({name: "fetch exception", message: error.response.data});
                    })
                    .finally(() => setIsLoading(false));
        } else {
            navigateTo('/login');
        }

        return () => {
            setExpenses([]);
            setErrors(null);
            setIsLoading(true);
        }
    }, [navigateTo, token])

    return <div className={'container'}>
        {
            isLoading
                ? <Spinner textColor={'text-primary'}/>
                : errors === null
                    ? <Expenses
                        columns={columns}
                        data={expenses}
                        userName={getUserName(token!)}
                    />
                    : <p>`Error fetching data - ${errors.name} occurred with ${errors.message}`</p>
        }
    </div>;
};