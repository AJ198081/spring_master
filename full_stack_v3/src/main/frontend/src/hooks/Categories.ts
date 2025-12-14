import {AxiosInstance} from "../service/api-client.ts";
import {AxiosError, AxiosResponse} from "axios";
import {defaultCategories, ExpenseResponse} from "../domain/Types.ts";
import toast from "react-hot-toast";
import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";

export const useCategories = () => {

    const navigate = useNavigate();
    const [categories, setCategories] = useState<string[]>(defaultCategories);

    const getBrowserLocale = (): string => {
        return navigator.language || navigator.languages?.[0] || 'en-AU';
    };


    useEffect(() => {
        AxiosInstance.get('/api/v1/expenses')
            .then((response: AxiosResponse<ExpenseResponse[]>) => {
                if (response.status === 200) {
                    const uniqueCategories = new Set(response.data.map(expense => expense.category));
                    setCategories(uniqueCategories.size === 0 ? defaultCategories : [...uniqueCategories]
                        .sort((a, b) => a.localeCompare(b, getBrowserLocale(), {sensitivity: 'base'})));
                }
            })
            .catch(error => {
                if (error instanceof AxiosError) {
                    toast.error((error as AxiosError).message);
                    if ((error as AxiosError).status === 401) {
                        navigate('/login');
                        return
                    }
                }
            })
    }, [navigate]);

    return {
        categories
    };
}