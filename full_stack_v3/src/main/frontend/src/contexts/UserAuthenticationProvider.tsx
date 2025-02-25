import {UserAuthenticationContext} from "./UserAuthenticationContext.tsx";
import {ReactNode, useLayoutEffect, useState} from "react";
import {AxiosInstance} from "../service/api-client.ts";
import {isJwtValid} from "../domain/Types.ts";


export const UserAuthenticationProvider = ({children}: { children: ReactNode }) => {

    const [token, setToken] = useState<string | null>(null);

    useLayoutEffect(() => {
        if (isJwtValid(token)) {
            AxiosInstance.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        } else {
            delete AxiosInstance.defaults.headers.common['Authorization'];
        }
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