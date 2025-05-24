import {Fab} from "@mui/material"
import AddIcon from '@mui/icons-material/Add';
import {useState} from "react";

export const FloatingActionButtonComponent = () => {

    const [loggedIn, setLoggedIn] = useState(false)
    return (
        <Fab
            color="info"
            variant={'extended'}
            aria-label="add"
            sx={{position: 'fixed', bottom: 16, right: 16}}
            onClick={() => {setLoggedIn(prevState => !prevState)}}
            disabled={loggedIn}
        >
            <AddIcon/>
            New User
        </Fab>
    )
}