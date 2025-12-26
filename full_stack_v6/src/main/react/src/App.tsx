import './App.css'
import {Toaster} from "react-hot-toast";
import 'sweetalert2/themes/material-ui.css'
import {CounterComponent} from "./components/counter/CounterComponent.tsx";

function App() {

    return (
        <div className={"d-flex flex-column min-vh-100"}>

            <CounterComponent />

            <Toaster/>
        </div>
    );
}

export default App
