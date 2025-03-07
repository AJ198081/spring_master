import {UserAuthenticationContext} from "./UserAuthenticationContext.tsx";
import {ReactNode, useLayoutEffect, useState} from "react";
import {AxiosInstance} from "../service/api-client.ts";
import {isJwtValid} from "../domain/Types.ts";
import toast from "react-hot-toast";
import {jwtDecode, JwtPayload} from "jwt-decode";


export const UserAuthenticationProvider = ({children}: { children: ReactNode }) => {

    const [token, setToken] = useState<string | null>(null);

    let timeout: number;
    let timeUntilTokenExpiry: number | null | undefined;

    useLayoutEffect(() => {
        if (token && isJwtValid(token)) {
            AxiosInstance.defaults.headers.common['Authorization'] = `Bearer ${token}`;
            const jwtPayload = token ? jwtDecode<JwtPayload>(token) : null;
            timeUntilTokenExpiry = jwtPayload?.exp ? (jwtPayload.exp * 1000 - Date.now() - 1500) : null;
        } else {
            delete AxiosInstance.defaults.headers.common['Authorization'];
        }

        if (timeUntilTokenExpiry) {
            timeout = setTimeout(() => {
                delete AxiosInstance.defaults.headers.common['Authorization'];
                AxiosInstance.get('/refresh-token')
                    .then(response => {
                        if (response.status === 200) {
                            setToken(response.data.token);
                            toast.success('Token refreshed');
                        } else {
                            setToken(null);
                            toast.error('Token refresh failed');
                        }
                    })
            }, timeUntilTokenExpiry);
        }

        return () => {
            clearTimeout(timeout);
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