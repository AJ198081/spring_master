import {MantineProvider} from "@mantine/core";
import {Dashboard} from "./pages/Dashboard.tsx";
import {IconContext} from 'react-icons';
import {Navbar} from "./components/Navbar.tsx";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {Login} from "./pages/authentication/Login.tsx";
import {Logout} from "./pages/authentication/Logout.tsx";
import {Registration} from "./pages/authentication/Registration.tsx";
import {ExpenseDetails} from "./components/ExpenseDetails.tsx";
import {ToasterComponent} from "./components/common/Toaster.tsx";
import {AddExpense} from "./components/AddExpense.tsx";
import {UserAuthenticationProvider} from "./contexts/UserAuthenticationProvider.tsx";
import {ProtectedRoute} from "./pages/authentication/ProtectedRoute.tsx";
import {createTheme, ThemeProvider} from "@mui/material/styles";
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

function App() {

    const theme = createTheme({
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

    return (
        <UserAuthenticationProvider>
            <IconContext.Provider value={{color: 'purple', size: '1.5em'}}>

                <MantineProvider
                    theme={{
                        primaryColor: 'red',
                        primaryShade: 8
                    }}>
                    <ThemeProvider theme={theme}>
                    <ToasterComponent/>

                    <BrowserRouter>
                        <Navbar/>
                        <Routes>
                            <Route path={'/'} index={true} element={<Dashboard/>}/>
                            <Route path={'/login'} element={<Login/>}/>
                            <Route path={'/logout'} element={<Logout/>}/>
                            <Route path={'/register'} element={<Registration/>}/>
                                <Route path={'/new'} element={
                                    <ProtectedRoute><AddExpense/></ProtectedRoute>
                                }/>
                                <Route path={'/view/:expenseId'} element={
                                    <ProtectedRoute><ExpenseDetails/></ProtectedRoute>}/>
                        </Routes>
                    </BrowserRouter>
                    </ThemeProvider>
                </MantineProvider>
            </IconContext.Provider>
        </UserAuthenticationProvider>
    )
}

export default App
