import {BottomNavigation, BottomNavigationAction} from "@mui/material";
import RestoreIcon from '@mui/icons-material/Restore';
import FavoriteIcon from '@mui/icons-material/Favorite';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import {useState} from "react";

export const FooterComponent = () => {

    const [value, setValue] = useState<number>(0);

    return (
        <BottomNavigation
            showLabels
            value={value}
            onChange={(_event, newValue: number) => {
                setValue(newValue);
            }}
        >
            <BottomNavigationAction
                label="Recents"
                icon={<RestoreIcon/>}
            />
            <BottomNavigationAction
                label="Favorites"
                icon={<FavoriteIcon/>}
            />
            <BottomNavigationAction
                label="Nearby"
                icon={<LocationOnIcon/>}
            />
        </BottomNavigation>
    )
}