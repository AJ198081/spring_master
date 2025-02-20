import {createContext, Dispatch, SetStateAction} from 'react';
import {CustomJwtPayload} from "../domain/Types.ts";

const initialAuthenticationContext = {
    isAuthenticated: false,
    setIsAuthenticated: (): void => {
    },
    token: null,
    setToken: (): void => {
    },
};

interface AuthenticationContext {
    isAuthenticated: boolean,
    setIsAuthenticated: Dispatch<SetStateAction<boolean>>,
    token: CustomJwtPayload | null,
    setToken: Dispatch<SetStateAction<CustomJwtPayload | null>>,
}

export const UserAuthenticationContext = createContext<AuthenticationContext>(initialAuthenticationContext);