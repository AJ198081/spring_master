import {Button, ButtonGroup, Stack} from "@mui/material";
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import IconButton from "@mui/material/IconButton";

export const ButtonComponent = () => {
    return (
        <Stack direction={"column"} spacing={2} margin={2}>
            <Stack direction={"row"}>
                <Button variant={'contained'}>Publish</Button>
                <Button variant={'outlined'}>Publish</Button>
                <Button
                    component={"a"}
                    href={"http://www.google.com"}
                    size={'medium'}
                    variant={'contained'}
                    color={'success'}
                    disabled={true}
                    sx={{
                        '&.Mui-disabled': {
                            backgroundColor: 'rgba(5, 255, 0, 0.12)',
                            color: 'rgba(0, 255, 0, 0.26)'
                        }
                    }}
                    endIcon={
                        <IconButton
                            color={'secondary'}
                            sx={{padding: 0, margin: 0, bgcolor: 'black'}}
                        >
                            <ArrowForwardIcon/>
                        </IconButton>
                    }
                >
                    Publish
                </Button>
                <Button
                    variant={'outlined'}
                    sx={{
                        bgcolor: 'orange'
                    }}
                >
                    Publish
                </Button>
            </Stack>
            <Stack direction={"row"}>
                <Button
                    variant={'contained'}
                    color={'error'}
                    sx={{
                        width: '95px',
                        padding: 2,
                        fontSize: '1.2em',
                        fontWeight: 'bold',
                        borderRadius: '10px',
                        whiteSpace: 'nowrap',
                        border: '1px solid black',
                        '&:hover': {
                            backgroundColor: 'black',
                            color: 'white',
                            border: '1px solid white',
                        }
                    }}
                >
                    2 Row
                </Button>
            </Stack>
            <Stack direction={"row"} spacing={2}>
                <ButtonGroup
                    variant={'contained'}
                    color={'info'}
                    aria-label="outlined primary button group"
                    size={'large'}
                    orientation={'vertical'}
                    onBlur={(e) => {console.log(e.target.innerHTML)}}
                    sx={{
                        '& .MuiButtonGroup-middleButton': {
                            borderColor: 'white',
                        },
                        '& > button': {
                            bgcolor: 'black',
                            color: 'white',
                            border: '1px solid white',
                            '&:hover': {
                                backgroundColor: 'white',
                                color: 'black',
                                border: '1px solid black',
                            }
                        }
                    }}
                >
                    <Button>One</Button>
                    <Button>Two</Button>
                    <Button>Three</Button>
                </ButtonGroup>
            </Stack>
        </Stack>
    )
}
