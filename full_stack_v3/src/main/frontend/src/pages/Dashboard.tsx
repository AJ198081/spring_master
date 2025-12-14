import {ReactNode, useContext, useEffect, useMemo, useState} from "react";
import toast from 'react-hot-toast';
import {columnsDescription, ExpenseResponse, isJwtValid} from "../domain/Types.ts";
import {Expenses} from "../components/Expenses.tsx";
import {MRT_ColumnDef} from "mantine-react-table";
import {Spinner} from "../components/common/Spinner.tsx";
import {useNavigate} from "react-router-dom";
import {AxiosInstance} from "../service/api-client.ts";
import {UserAuthenticationContext} from "../contexts/user/UserAuthenticationContext.tsx";
import {AxiosError, AxiosResponse} from "axios";
import {getUserName} from "../utils/Utils.ts";

export const Dashboard = (): ReactNode => {

    const columns = useMemo<MRT_ColumnDef<ExpenseResponse>[]>(() => columnsDescription, []);

    const navigateTo = useNavigate();

    const [isLoading, setIsLoading] = useState(true);
    const [errors, setErrors] = useState<Error | null>(null);
    const [expenses, setExpenses] = useState<ExpenseResponse[]>([]);

    const {token, setToken} = useContext(UserAuthenticationContext)

    useEffect(() => {

        if (token !== null) {
            if (!isJwtValid(token)) {
                toast.error('Your session has expired. Please log in again.', {
                    duration: 3000,
                });
                setToken(null);
                navigateTo('/login');
            } else {
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
                                setToken(null);
                                navigateTo('/login');
                            }
                        }
                        setErrors({name: "fetch exception", message: error.response.data});
                    })
                    .finally(() => setIsLoading(false));
            }
        } else {
            navigateTo('/login');
        }

        return () => {
            setExpenses([]);
            setErrors(null);
            setIsLoading(true);
        }
    }, [navigateTo, setToken, token])

    return <div className={'container'}>
        {
            isLoading
                ? <Spinner textColor={'text-primary'}/>
                : errors === null
                    ? <Expenses columns={columns} data={expenses} userName={getUserName(token!)}/>
                    : <p>`Error fetching data - ${errors.name} occurred with ${errors.message}`</p>
        }
    </div>;
}