import {Posts} from "./components/Posts.tsx";
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import {Navbar} from "./components/Navbar.tsx";
import {ReactQueryDevtools} from "@tanstack/react-query-devtools";

const queryClient = new QueryClient();

function App() {

    return (
        <QueryClientProvider client={queryClient}>
            <Navbar/>
            <Posts/>
            <ReactQueryDevtools initialIsOpen={false}/>
        </QueryClientProvider>
    )
}

export default App
