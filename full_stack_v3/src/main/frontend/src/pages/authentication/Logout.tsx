import {ReactNode, useContext, useEffect} from "react";
import {useNavigate} from "react-router-dom";
import toast from "react-hot-toast";
import {UserAuthenticationContext} from "../../contexts/UserAuthenticationContext.tsx";

export const Logout = (): ReactNode => {

    const {setIsAuthenticated, setToken} = useContext(UserAuthenticationContext);
    const navigateTo = useNavigate();

    useEffect(() => {
        setIsAuthenticated(false);
        setToken(null);
        navigateTo('/login')
        toast.success('You have been logged out successfully.');
    })

    return <div>
        Logout
    </div>;
};