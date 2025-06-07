import {AppBar, Box, styled, type Theme, Toolbar, Typography} from "@mui/material";
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import StyleIcon from '@mui/icons-material/Style';
import IconButton from "@mui/material/IconButton";

export const AppBarComponent = () => {

    // Recommended way to offset the AppBar and push the content down
    const Offset = styled('div')(({theme}: { theme: Theme }) => theme.mixins.toolbar);

    return (
        <>
            <AppBar
                color={'secondary'}
                elevation={4}
                sx={{fontFamily: 'Lobster, Helvetica, sans-serif',}}
                // position={'absolute'}
                position={'fixed'}
                // sx={{top: 'auto', bottom: 0, position: 'fixed'}}
            >
                <Toolbar
                    variant={'dense'}
                    sx={{justifyContent: 'space-between'}}
                >
                    <IconButton
                        color={'warning'}
                        aria-label="menu"
                        size={'medium'}
                    >
                        <StyleIcon
                            fontSize={'large'}
                            color={'inherit'}
                        />
                        <div>e-Commerce</div>
                    </IconButton>
                    <Box sx={{
                        display: 'flex',
                        justifyContent: 'space-around',
                        width: '300px',
                        color: theme => theme.palette.info.main,
                    }}>
                        <Typography>User</Typography>
                        <div>Settings</div>
                        <div>Logout</div>
                        <IconButton
                            sx={{padding: 0, margin: 0}}
                        >
                            <ShoppingCartIcon
                                sx={{
                                    color: (theme) => theme.palette.common.white,
                                    '&:hover': {
                                        color: (theme) => theme.palette.primary.main,
                                    }
                                }}
                                fontSize={'medium'}
                            />
                        </IconButton>
                    </Box>
                </Toolbar>
            </AppBar>
            <Offset/>
        </>
    )
}