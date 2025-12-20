import {useEffect, useState} from "react";
import {getAllCustomer} from "../service/graphql-client.ts";
import {Box, Typography} from "@mui/material";
import {Spinner} from "./common/Spinner.tsx";

type Customer = {
    id: string;
    name: string;
    email: string;
};

export const Footer = () => {

    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [customers, setCustomers] = useState<Customer[]>([]);

    useEffect(() => {
        getAllCustomer()
            .then(response => setCustomers(response))
            .finally(() => setIsLoading(false));
    }, [])


    return (
        <Box
            component="footer"
            sx={{
                p: 2,
                mt: 'auto',
                color: (theme) =>
                    theme.palette.mode === 'light'
                        ? 'white'
                        : 'black',
                backgroundColor: (theme) =>
                    theme.palette.mode === 'light'
                        ? theme.palette.grey[900]
                        : theme.palette.grey[200],
                textAlign: 'center',
                position: 'fixed',
                bottom: 0,
                width: '100%'
            }}
        >
            {isLoading
                ? <Spinner height={'100%'} spinnerSize={'2rem'}/>
                : <Typography variant="h6">
                    {customers.length > 0
                        ? `Have ${customers.length} customers registered`
                        : 'No customers found'
                    }
                </Typography>
            }
        </Box>
    )
}