import {Avatar, Button, Menu, MenuItem, Zoom} from '@mui/material';
import {useState} from "react";
import AppleIcon from "@mui/icons-material/Apple"

export const MenuComponent = () => {

    const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);
    const [selected, setSelected] = useState<string | null>(null);

    const handleSelect = (option: string) => {
        setSelected(option === selected ? null : option);
        handleClose();
    }

    const open = Boolean(anchorEl);

    const handleClick: (event: any) => void = (event) => {
        setAnchorEl(event.currentTarget);
    }

    const handleClose = () => setAnchorEl(null);

    console.log(selected);

    return (
        <>
            <Button
                variant={'outlined'}
                onClick={handleClick}
                // onMouseOver={(event) => console.log(event)}
            >
                Open Menu
            </Button>
            <Menu
                open={open}
                anchorEl={anchorEl}
                onClose={handleClose}
                onSelect={(event) => console.log(event)}
                onClick={handleClose}
                anchorOrigin={{vertical: 'bottom', horizontal: 'center'}}
                TransitionComponent={Zoom}
            >
                <MenuItem divider={true} onClick={() => handleSelect('Apple')}>
                    <Avatar><AppleIcon  /></Avatar>
                </MenuItem>
                <MenuItem value={'option 1'}>Option 1</MenuItem>
                <MenuItem>Option 2</MenuItem>
                <MenuItem>Option 3</MenuItem>
                <MenuItem>Option 4</MenuItem>

            </Menu>
        </>
    )
}