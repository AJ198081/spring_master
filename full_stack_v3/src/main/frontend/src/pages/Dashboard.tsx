import {ReactNode, useContext, useEffect, useMemo, useState} from "react";
import toast from 'react-hot-toast';
import {columnsDescription, CustomJwtPayload, ExpenseResponse} from "../domain/Types.ts";
import {ExpenseList} from "../components/ExpenseList.tsx";
import {MRT_ColumnDef} from "mantine-react-table";
import {Spinner} from "../components/common/Spinner.tsx";
import {useNavigate} from "react-router-dom";
import {AxiosInstance} from "../service/api-client.ts";
import {stringToDateObject} from "../utils/Formatter.ts";
import {UserAuthenticationContext} from "../contexts/UserAuthenticationContext.tsx";
import {AxiosResponse} from "axios";
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

        const abortController = new AbortController();

        if (token !== null) {
            const decodedToken = jwtDecode<CustomJwtPayload>(token);
            if (decodedToken.exp && decodedToken.exp < Date.now() / 1000) {
                toast.error('Your session has expired. Please log in again.', {
                    duration: 3000,
                });
                setUserName('Guest');
                setToken(null);
                navigateTo('/login');
            } else {
                const fullName = `${decodedToken.lastName ?? 'Guest'}, ${decodedToken.firstName ?? ''}`.trim();
                setUserName(fullName);

                AxiosInstance.get('/api/v1/expenses', {
                    signal: abortController.signal,
                })
                    .then((response: AxiosResponse<ExpenseResponse[]>) => {
                        if (response.status === 200) {

                            const parsedExpenses = response.data;

                            const enrichedExpenses = parsedExpenses.map(expense => {
                                return {
                                    ...expense,
                                    date: (typeof expense.date === 'string') ? stringToDateObject(expense.date) : expense.date
                                };
                            });
                            setExpenses(enrichedExpenses);
                        }
                    })
                    .catch(error => {
                        setErrors({name: "fetch exception", message: error.response.data});
                        toast.error(`Error whilst fetching data ${error.response.data.message}`);
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
            abortController.abort();
        }
    }, [])

    return <div className={'container'}>
        {
            isLoading
                ? <Spinner textColor={'text-primary'}/>
                : errors === null
                    ? <ExpenseList columns={columns} data={expenses} userName={userName}/>
                    : <p>`Error fetching data - ${errors.name} occurred with ${errors.message}`</p>
        }
    </div>;
}