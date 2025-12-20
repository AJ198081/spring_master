import {useEffect, useState} from "react";
import {getTotalNumberOfCustomers} from "../service/graphql-client.ts";

export const useNumberOfCustomers = () => {

    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [numberOfCustomers, setNumberOfCustomers] = useState<number>(0);

    useEffect(() => {
        getTotalNumberOfCustomers({id: true, lastName: true})
            .then(response => {
                return setNumberOfCustomers(response)
            })
            .finally(() => setIsLoading(false));
    }, [])

    return {isLoading, numberOfCustomers}
}