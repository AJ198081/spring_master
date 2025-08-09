import { toast } from "react-toastify";
import {clearSessionAuthentication, useAuthStore} from "../../store/AuthStore.ts";
import {useEffect} from "react";
import {useNavigate} from "react-router-dom";
import { Spinner } from "react-bootstrap";
import {useCustomerStore} from "../../store/CustomerStore.ts";

export const LogoutComponent = () => {

    const setCurrentAuthentication = useAuthStore(state => state.setAuthState);
    const setCustomer = useCustomerStore(state => state.setCustomer);
    const navigate = useNavigate();

    useEffect(() => {
        setCurrentAuthentication(clearSessionAuthentication);
        setCustomer(null);
        toast.success('Logged out successfully');
        navigate('/login', {replace: true});
    });

    return (
       <div className={"d-flex justify-content-center align-items-center"} style={{height: '100vh', width: '100vw'}}>
           <Spinner className={`mx-4`}/> Logging out....
       </div>
    )
}