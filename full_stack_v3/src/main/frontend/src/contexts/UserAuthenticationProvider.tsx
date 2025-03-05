import {UserAuthenticationContext} from "./UserAuthenticationContext.tsx";
import {ReactNode, useLayoutEffect, useState} from "react";
import {AxiosInstance} from "../service/api-client.ts";
import {isJwtValid} from "../domain/Types.ts";
import toast from "react-hot-toast";


export const UserAuthenticationProvider = ({children}: { children: ReactNode }) => {

    const [token, setToken] = useState<string | null>(null);

    let timeout: number;

    useLayoutEffect(() => {
        if (isJwtValid(token)) {
            AxiosInstance.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        } else {
            delete AxiosInstance.defaults.headers.common['Authorization'];
        }

        if (token !== null) {
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
            }, 115000);
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