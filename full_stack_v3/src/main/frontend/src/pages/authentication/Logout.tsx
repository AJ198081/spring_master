import {ReactNode, useContext, useEffect} from "react";
import {useNavigate} from "react-router-dom";
import toast from "react-hot-toast";
import {UserAuthenticationContext} from "../../contexts/UserAuthenticationContext.tsx";
import {AxiosInstance} from "../../service/api-client.ts";

export const Logout = (): ReactNode => {

    const {setToken} = useContext(UserAuthenticationContext);
    const navigateTo = useNavigate();

    useEffect(() => {

        AxiosInstance.defaults.headers.common['Authorization'] = null;

        AxiosInstance.get('/api/v1/auth/logout')
            .then(response => {
                if (response.status === 200) {
                    toast.success('Logout successful', {
                        duration: 3000,
                    });
                }
            })
            .catch((reason => {
                toast.error(`Logout failed because of - ${reason.message}`);
            }))
            .finally(() => {
                    navigateTo('/login');
                    setToken(null);
                }
            );
    })

    return <div>
        Logout
    </div>;
};