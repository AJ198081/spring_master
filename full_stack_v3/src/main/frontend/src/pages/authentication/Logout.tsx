import {ReactNode, useContext, useEffect} from "react";
import {useNavigate} from "react-router-dom";
import toast from "react-hot-toast";
import {UserAuthenticationContext} from "../../contexts/UserAuthenticationContext.tsx";
import {AxiosInstance} from "../../service/api-client.ts";
import {AxiosError} from "axios";

export const Logout = (): ReactNode => {

    const {setToken} = useContext(UserAuthenticationContext);
    const navigateTo = useNavigate();

    useEffect(() => {

        AxiosInstance.defaults.headers.common['Authorization'] = null;

        const abortController = new AbortController();

        AxiosInstance.get('/api/v1/auth/logout', {
            signal: abortController.signal
        })
            .then(response => {
                if (response.status === 200) {
                    toast.success('Successfully logged out. See you soon!', {
                        id: 'logout-success',
                        duration: 3000,
                    });
                }
            })
            .catch((error => {
                if (error instanceof AxiosError) {
                    if (error.response?.status === 401) {
                        toast.error('Your session has expired. Please log in again.', {
                            duration: 3000,
                        });
                    } else {
                        return
                    }
                }

                toast.error(`Logout failed because of - ${error.message}`);
            }))
            .finally(() => {
                    navigateTo('/login');
                    setToken(null);
                }
            );

        return () => abortController.abort();
    });

    return <div>
        Logout
    </div>;
};