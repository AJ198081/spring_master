import {useState} from "react";
import {Button, Checkbox, Stack, Typography} from "@mui/material";

export const PracticeComponent = () => {

    const [open, setOpen] = useState<boolean>(false);

    const buttonStyles = {
        bgcolor: open ? 'primary.main' : 'secondary.main',
        marginTop: 6,
        padding: 2,
        borderRadius: 2,
        color: 'whitesmoke',
        boxShadow: (theme: { shadows: string[] }) => theme.shadows[6],
        // fontSize: 'h2.fontSize',
        // fontWeight: 'h3.fontWeight',
        // & - is just the parent selector, so :hover psuedo-class will be applied to the parent element
        /*"&:hover": {
            bgcolor: theme => theme.palette.primary.dark,
        },*/
        "&>p": {
            color: 'red',
        },
        "&:disabled": {
            bgcolor: 'grey',
        }
        // boxShadow: 6,
    };
    return (
        <Stack direction={"column"} spacing={2} margin={2}>
            <Typography variant={"h1"}>
                Hello
            </Typography>
            <Stack spacing={6} direction={"row"} sx={{margin: 2}}>
                <Button
                    component="button"
                    variant="contained"
                    color="secondary"
                    type="button"
                    onClick={() => {
                        setOpen(prev => !prev)
                    }}
                    // disabled={true}
                    sx={buttonStyles}
                >
                    My Button
                    <p>Thanks</p>
                </Button>
                <Checkbox
                    disableRipple={true}
                />
            </Stack>
        </Stack>


    )
}