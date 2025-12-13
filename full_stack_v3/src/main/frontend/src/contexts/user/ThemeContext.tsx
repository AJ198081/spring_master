import {createContext, ReactNode, useContext, useState} from "react";
import {ThemeProvider} from "@mui/material/styles";

const ThemeContext = createContext({
    themeMode: 'light',
    setThemeMode: (_themeMode: string) => {
    }
});

export const ThemeContextProvider = ({children}: { children: ReactNode }) => {

    const [themeMode, setThemeMode] = useState('light');

    const theme = themeMode === 'light'
        ? 'light'
        : 'dark';

    return (
        <ThemeContext.Provider
            value={{
                themeMode, setThemeMode
            }}
        >
            <ThemeProvider theme={theme}> {children}</ThemeProvider>
        </ThemeContext.Provider>
    )
};

export const useThemeContext = () => {
    const context = useContext(ThemeContext);

    if (!context) {
        throw new Error('useThemeContext must be used within a ThemeContextProvider');
    }

    return context;
}