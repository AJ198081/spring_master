import {UserAuthenticationContext} from "./UserAuthenticationContext.tsx";
import {ReactNode, useState} from "react";
import {CustomJwtPayload} from "../domain/Types.ts";


export const UserAuthenticationProvider = ({children}: {children: ReactNode}) => {

    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [token, setToken] = useState<CustomJwtPayload | null>(null);

    return (
        <UserAuthenticationContext.Provider value={{
            isAuthenticated,
            setIsAuthenticated,
            token,
            setToken
        }}>
            {children}
        </UserAuthenticationContext.Provider>
    );
}