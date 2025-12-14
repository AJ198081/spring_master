import {MantineProvider} from "@mantine/core";
import {Dashboard} from "./pages/Dashboard.tsx";
import {IconContext} from 'react-icons';
import {Navbar} from "./components/Navbar.tsx";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import Login from "./pages/authentication/Login.tsx";
import {Logout} from "./pages/authentication/Logout.tsx";
import {Registration} from "./pages/authentication/Registration.tsx";
import {ExpenseDetails} from "./components/ExpenseDetails.tsx";
import {ToasterComponent} from "./components/common/Toaster.tsx";
import {AddExpense} from "./components/AddExpense.tsx";
import {ProtectedRoute} from "./pages/authentication/ProtectedRoute.tsx";
import {ThemeProvider} from "@mui/material/styles";
import {ContextProviders} from "./contexts/ContextProviders.tsx";
import {theme} from "./utils/ThemeResolver.ts";


function App() {


    return (
        <ContextProviders>
            <IconContext.Provider value={{color: 'purple', size: '1.5em'}}>
                    <MantineProvider
                        theme={{
                            primaryColor: 'red',
                            primaryShade: 8
                        }}
                    >
                        <ThemeProvider theme={theme}>
                            <ToasterComponent/>

                            <BrowserRouter>
                                <Navbar/>
                                <Routes>
                                    <Route
                                        index={true}
                                        element={<Dashboard/>}
                                    />
                                    <Route
                                        path={'/login'}
                                        element={<Login/>}
                                    />
                                    <Route
                                        path={'/logout'}
                                        element={<Logout/>}
                                    />
                                    <Route
                                        path={'/register'}
                                        element={<Registration/>}
                                    />
                                    <Route
                                        path={'/new'}
                                        element={
                                            <ProtectedRoute><AddExpense/></ProtectedRoute>
                                        }
                                    />
                                    <Route
                                        path={'/view/:expenseId'}
                                        element={
                                            <ProtectedRoute><ExpenseDetails/></ProtectedRoute>}
                                    />
                                </Routes>
                            </BrowserRouter>
                        </ThemeProvider>
                    </MantineProvider>
            </IconContext.Provider>
        </ContextProviders>
    )
}

export default App
