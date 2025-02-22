import {createContext, Dispatch, SetStateAction} from 'react';

const initialAuthenticationContext = {
    isAuthenticated: false,
    setIsAuthenticated: (): void => {
    },
    token: null,
    setToken: (): void => {
    },
};

export interface AuthenticationContext {
    isAuthenticated: boolean,
    setIsAuthenticated: Dispatch<SetStateAction<boolean>>,
    token: string | null,
    setToken: Dispatch<SetStateAction<string | null>>,
}

export const UserAuthenticationContext = createContext<AuthenticationContext>(initialAuthenticationContext);