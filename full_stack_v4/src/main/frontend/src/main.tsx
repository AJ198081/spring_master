import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import './index.css'
import {App} from './App.tsx';
import {Provider} from "@/components/ui/provider"
import {BrowserRouter} from "react-router-dom";
import {Toaster} from "@/components/ui/toaster";

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <BrowserRouter>
            <Provider>
                <App/>
                <Toaster/>
            </Provider>
        </BrowserRouter>
    </StrictMode>,
)
