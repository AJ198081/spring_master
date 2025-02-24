import {UserAuthenticationContext} from "./UserAuthenticationContext.tsx";
import {ReactNode, useLayoutEffect, useState} from "react";
import {AxiosInstance} from "../service/api-client.ts";


export const UserAuthenticationProvider = ({children}: { children: ReactNode }) => {

    const [token, setToken] = useState<string | null>(null);

    useLayoutEffect(() => {
        AxiosInstance.defaults.headers.common['Authorization'] = token ? `Bearer ${token}` : null;
    }, [token]);

    return (
        <UserAuthenticationContext.Provider value={{
            token,
            setToken,
        }}>
            {children}
        </UserAuthenticationContext.Provider>
    );
}