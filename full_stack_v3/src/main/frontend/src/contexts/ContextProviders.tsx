import {ReactNode} from "react";
import {UserAuthenticationProvider} from "./user/UserAuthenticationProvider.tsx";
import {ApolloClientProvider} from "./apollo/ApolloClientProvider.tsx";

export const ContextProviders = ({children}: { children: ReactNode }) => {

    return (
        <ApolloClientProvider>
            <UserAuthenticationProvider>
                {children}
            </UserAuthenticationProvider>
        </ApolloClientProvider>
    )
};
