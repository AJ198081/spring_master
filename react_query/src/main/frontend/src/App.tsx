import {Posts} from "./components/Posts.tsx";
import {Heading} from "@chakra-ui/react";
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";

const queryClient = new QueryClient();

function App() {

    return (
        <QueryClientProvider client={queryClient}>
            <Heading size={"lg"}>Blog Posts</Heading>
            <Posts/>
        </QueryClientProvider>
    )
}

export default App
