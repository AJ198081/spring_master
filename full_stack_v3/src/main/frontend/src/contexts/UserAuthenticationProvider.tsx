import {UserAuthenticationContext} from "./UserAuthenticationContext.tsx";
import {ReactNode, useLayoutEffect, useState} from "react";
import {AxiosInstance} from "../service/api-client.ts";


export const UserAuthenticationProvider = ({children}: { children: ReactNode }) => {

    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [token, setToken] = useState<string | null>(null);

    useLayoutEffect(() => {
        if (token !== null) {
            AxiosInstance.interceptors.request.use(
                requestConfig => {
                    requestConfig.headers.Authorization = `Bearer ${token}`
                    return requestConfig;
                }
            )
        }
    }, [token]);

    return (
        <UserAuthenticationContext.Provider value={{
            isAuthenticated,
            setIsAuthenticated,
            token,
            setToken,
        }}>
            {children}
        </UserAuthenticationContext.Provider>
    );
}