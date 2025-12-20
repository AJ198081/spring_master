import {Box, Typography} from "@mui/material";
import {Spinner} from "./common/Spinner.tsx";
import {useNumberOfCustomers} from "../hooks/useNumberOfCustomers.ts";

export type Customer = {
    id: string;
    name: string;
    email: string;
};

export const Footer = () => {

    const {isLoading, numberOfCustomers} = useNumberOfCustomers();

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
                    {numberOfCustomers > 0
                        ? `Have ${numberOfCustomers} customers registered`
                        : 'No customers found'
                    }
                </Typography>
            }
        </Box>
    )
}