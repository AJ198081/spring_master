import {createContext, Dispatch, SetStateAction} from 'react';

const initialAuthenticationContext = {
    token: null,
    setToken: (): void => {
    },
};

export interface AuthenticationContext {
    token: string | null,
    setToken: Dispatch<SetStateAction<string | null>>,
}

export const UserAuthenticationContext = createContext<AuthenticationContext>(initialAuthenticationContext);