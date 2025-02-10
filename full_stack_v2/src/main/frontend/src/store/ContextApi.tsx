import {createContext} from "react";
import {ContextApiInterface} from "./ApiContext.tsx";

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