import {MantineProvider} from "@mantine/core";
import {Dashboard} from "./pages/Dashboard.tsx";

function App() {

  return (
    <MantineProvider>
        <div className={"container"}>
            <Dashboard/>
        </div>
    </MantineProvider>
  )
}

export default App
