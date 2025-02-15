import {MantineProvider} from "@mantine/core";
import {Dashboard} from "./pages/Dashboard.tsx";
import {IconContext} from 'react-icons';
import {Navbar} from "./components/Navbar.tsx";
import {BrowserRouter, Route, Routes} from "react-router-dom";

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
                        <Route path={'/'} element={<Dashboard/>} />
                    </Routes>
                </BrowserRouter>
            </MantineProvider>
        </IconContext.Provider>
    )
}

export default App
