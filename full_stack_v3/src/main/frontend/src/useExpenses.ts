import {useEffect, useState} from "react";
import {ExpenseResponse} from "./domain/Types.ts";
import {AxiosInstance} from "./service/api-client.ts";
import dayjs from "dayjs";
import customParseFormat from "dayjs/plugin/customParseFormat";

dayjs.extend(customParseFormat);

export const useExpenses = () => {
    const [expenses, setExpenses] = useState<ExpenseResponse[]>([]);
    const [errors, setErrors] = useState<Error | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        setIsLoading(true);
        AxiosInstance.get('/api/v1/expenses')
            .then(response => {
                if (response.status === 200) {

                    const parsedExpenses = response.data as ExpenseResponse[];

                    const enrichedExpenses= parsedExpenses.map(expense => {
                        return {
                            ...expense,
                            date: expense.date
                        };
                    });

                    setExpenses(enrichedExpenses);
                }
            })
            .catch(error => setErrors({name: "fetch exception", message: error.response.data}))
            .finally(() => setIsLoading(false));
    }, []);

    return {expenses, errors, isLoading};
}