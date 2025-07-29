import { toast } from "react-toastify";
import {useAuthStore} from "../../store/AuthStore.ts";
import {useEffect} from "react";
import {useNavigate} from "react-router-dom";
import { Spinner } from "react-bootstrap";

export const LogoutComponent = () => {

    const setCurrentAuthentication = useAuthStore(state => state.setAuthState);
    const navigate = useNavigate();

    useEffect(() => {
        setCurrentAuthentication(null);
        toast.success('Logged out successfully');
        navigate('/login', {replace: true});
    });

    return (
       <div>
           <Spinner/> Logging out....
       </div>
    )
}