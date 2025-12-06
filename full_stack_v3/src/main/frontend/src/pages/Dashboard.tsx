import {ReactNode, useContext, useEffect, useMemo, useState} from "react";
import toast from 'react-hot-toast';
import {columnsDescription, CustomJwtPayload, ExpenseResponse, isJwtValid} from "../domain/Types.ts";
import {Expenses} from "../components/Expenses.tsx";
import {MRT_ColumnDef} from "mantine-react-table";
import {Spinner} from "../components/common/Spinner.tsx";
import {useNavigate} from "react-router-dom";
import {AxiosInstance} from "../service/api-client.ts";
import {UserAuthenticationContext} from "../contexts/UserAuthenticationContext.tsx";
import {AxiosError, AxiosResponse} from "axios";
import {jwtDecode} from "jwt-decode";

export const Dashboard = (): ReactNode => {

    const columns = useMemo<MRT_ColumnDef<ExpenseResponse>[]>(() => columnsDescription, []);

    const navigateTo = useNavigate();
    const [userName, setUserName] = useState<string>('Guest');

    const [isLoading, setIsLoading] = useState(true);
    const [errors, setErrors] = useState<Error | null>(null);
    const [expenses, setExpenses] = useState<ExpenseResponse[]>([]);

    const {token, setToken} = useContext(UserAuthenticationContext)

    useEffect(() => {

        if (token !== null) {
            const decodedToken = jwtDecode<CustomJwtPayload>(token);
            if (!isJwtValid(token)) {
                toast.error('Your session has expired. Please log in again.', {
                    duration: 3000,
                });
                setUserName('Guest');
                setToken(null);
                navigateTo('/login');
            } else {
                const fullName = `${decodedToken.lastName ?? 'Guest'}, ${decodedToken.firstName ?? ''}`.trim();
                setUserName(fullName);

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
                    ? <Expenses columns={columns} data={expenses} userName={userName}/>
                    : <p>`Error fetching data - ${errors.name} occurred with ${errors.message}`</p>
        }
    </div>;
}