import {MantineProvider} from "@mantine/core";
import {Dashboard} from "./pages/Dashboard.tsx";
import {IconContext} from 'react-icons';
import {Navbar} from "./components/Navbar.tsx";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {Login} from "./pages/authentication/Login.tsx";
import {Logout} from "./pages/authentication/Logout.tsx";
import {Registration} from "./pages/authentication/Registration.tsx";
import {NewExpense} from "./components/NewExpense.tsx";
import {ExpenseDetails} from "./components/ExpenseDetails.tsx";

function App() {

    return (
        <IconContext.Provider value={{color: 'purple', size: '1.5em'}}>
            <MantineProvider
                theme={{
                    primaryColor: 'red',
                    primaryShade: 8
                }}>
                <BrowserRouter>
                    <Navbar/>
                    <Routes>
                        <Route path={'/'} index={true} element={<Dashboard/>}/>
                        <Route path={'/login'} element={<Login/>}/>
                        <Route path={'/logout'} element={<Logout/>}/>
                        <Route path={'/register'} element={<Registration/>}/>
                        <Route path={'/new'} element={<NewExpense/>}/>
                        <Route path={'/view/:expenseId'} element={<ExpenseDetails />} />
                    </Routes>
                </BrowserRouter>
            </MantineProvider>
        </IconContext.Provider>
    )
}

export default App
