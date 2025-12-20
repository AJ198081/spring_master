import {Box, Typography} from "@mui/material";
import {Spinner} from "./common/Spinner.tsx";
import {useNumberOfCustomers} from "../hooks/useNumberOfCustomers.ts";

export const Footer = () => {

    const {isLoading, numberOfCustomers} = useNumberOfCustomers();

    return (
        <Box
            component="footer"
            sx={{
                p: 2,
                textAlign: 'center',

                width: '100%',
                position: 'fixed',
                bottom: 0,

                color: (theme) =>
                    theme.palette.mode === 'light'
                        ? 'white'
                        : 'black',

                backgroundColor: (theme) =>
                    theme.palette.mode === 'light'
                        ? theme.palette.grey[900]
                        : theme.palette.grey[200]
            }}
        >
            {isLoading
                ? <Spinner height={'100%'} spinnerSize={'2rem'}/>
                : <Typography variant="h6">
                    {numberOfCustomers > 0
                        ? `Have ${numberOfCustomers} registered customer${numberOfCustomers > 1 ? 's' : ''}`
                        : 'Currently, there are no registered customers'
                    }
                </Typography>
            }
        </Box>
    )
}