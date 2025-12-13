import {createTheme} from "@mui/material/styles";
import {alpha, getContrastRatio} from "@mui/material";

// Augment the palette to include a violet color
declare module '@mui/material/styles' {
    interface Palette {
        violet: Palette['primary'];
    }

    interface PaletteOptions {
        violet?: PaletteOptions['primary'];
    }
}

// Update the Button's color options to include a violet option
declare module '@mui/material/Button' {
    interface ButtonPropsColorOverrides {
        violet: true;
    }
}

const violetBase = '#7F00FF';
const violetMain = alpha(violetBase, 0.7);

export const theme = createTheme({
    palette: {
        violet: {
            main: violetMain,
            light: alpha(violetBase, 0.5),
            dark: alpha(violetBase, 0.9),
            contrastText: getContrastRatio(violetMain, '#fff') > 4.5 ? '#fff' : '#111',
        },
    },
    transitions: {
        duration: {
            shortest: 150,
            shorter: 200,
            short: 250,
            // most basic recommended timing
            standard: 300,
            // this is to be used in complex animations
            complex: 375,
            // recommended when something is entering the screen
            enteringScreen: 225,
            // recommended when something is leaving the screen
            leavingScreen: 195,
        }
    }
});

export const lightTheme = createTheme({
    palette: {
        mode: 'light',
        primary: {
            main: "#1976d2"
        },
        secondary: {
            main: "#dc004e"
        }
    },
    typography: {
        fontFamily: ["Roboto", "Helvetica", "Arial", "sans-serif"].join(','),
    },
})

export const darkTheme = createTheme({
    palette: {
        mode: 'dark',
        primary: {
            main: "#90caf9"
        },
        secondary: {
            main: "#f48fb1"
        }
    },
    typography: {
        fontFamily: ["Roboto", "Helvetica", "Arial", "sans-serif"].join(','),
    },
})