import {createContext} from "react";
import {SecurityUser} from "../types/Types.ts";

export interface ContextApiInterface {
    token: string | null;
    setToken: (token: string | null) => void;
    currentUser: SecurityUser | null;
    setCurrentUser: (user: SecurityUser | null) => void;
    openSidebar: boolean;
    setOpenSidebar: (openSidebar: boolean) => void;
    isAdmin: boolean;
    setIsAdmin: (isAdmin: boolean) => void;
}

const initialContextApiValue: ContextApiInterface = {
    token: null,
    setToken: () => {},
    currentUser: null,
    setCurrentUser: () => {},
    openSidebar: false,
    setOpenSidebar: () => {},
    isAdmin: false,
    setIsAdmin: () => {},
};

export const ContextApi = createContext<ContextApiInterface>(initialContextApiValue);