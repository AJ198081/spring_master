import {ApolloProvider} from "@apollo/client/react";
import {apolloClient} from "./ApolloClient.ts";
import {ReactNode} from "react";

export const ApolloClientProvider = ({children}: { children: ReactNode }) => {

    return <ApolloProvider client={apolloClient}>{children}</ApolloProvider>;
};