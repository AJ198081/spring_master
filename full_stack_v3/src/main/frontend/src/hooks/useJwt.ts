import {isJwtValid} from "../domain/Types.ts";
import toast from "react-hot-toast";
import {useContext, useEffect} from "react";
import {UserAuthenticationContext} from "../contexts/user/UserAuthenticationContext.tsx";

export const useJwt = () => {

    const {token, setToken} = useContext(UserAuthenticationContext)
    
    useEffect(() => {
        if (token !== null) {
            if (!isJwtValid(token)) {
                toast.error('Your session has expired. Please log in again.', {
                    duration: 5000,
                    toasterId: 'jwt-expired',
                });
                setToken(null);
            }
        }

    }, [setToken, token])

    return {
        token
    };
}