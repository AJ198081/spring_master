import {ReactNode, useEffect, useState} from "react";
import {AxiosInstance} from "../services/api.ts";
import toast from "react-hot-toast";
import {SecurityUser} from "../types/Types.ts";
import {ContextApi} from "./ContextApi.tsx";

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



export const ContextProvider = ({children}: {children: ReactNode}) => {

    const tokenValue = localStorage.getItem('token');
    const isAdminValue = Boolean(localStorage.getItem('isAdmin'));

    const [token, setToken] = useState(tokenValue);
    const [isAdmin, setIsAdmin] = useState(isAdminValue);
    const [currentUser, setCurrentUser] = useState<SecurityUser | null>(null);
    const [openSidebar, setOpenSidebar] = useState(true);

    const fetchUser = async () => {
        const userString = localStorage.getItem("USER");
        const user: SecurityUser | null = userString !== null ? JSON.parse(userString) : null;
        if (user?.username) {
            try {
                const response = await AxiosInstance.get('/admin/user');
                if (response.status === 200) {
                    const userRoles = response.data.roles;
                    if (userRoles.includes("ROLE_ADMIN")) {
                        localStorage.setItem("IS_ADMIN", JSON.stringify(true));
                        setIsAdmin(true);
                    } else {
                        localStorage.removeItem("IS_ADMIN");
                        setIsAdmin(false);
                    }
                    setCurrentUser(response.data);
                }
            } catch (error) {
                console.error("Error fetching current user", error);
                toast.error("Error fetching current user");
            }
        }
    }

    useEffect(() => {
        if (token) {
            void fetchUser();
        }
    }, [token]);

    return (
        <ContextApi.Provider value={{
            token,
            setToken,
            currentUser,
            setCurrentUser,
            openSidebar,
            setOpenSidebar,
            isAdmin,
            setIsAdmin,
        }}>
            {children}
        </ContextApi.Provider>
    );
};
