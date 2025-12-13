import {ReactNode} from "react";
import {UserAuthenticationProvider} from "./user/UserAuthenticationProvider.tsx";

export const ContextProviders = ({children}: { children: ReactNode }) => {

    return (
        <UserAuthenticationProvider>
            {children}
        </UserAuthenticationProvider>
    )
};
