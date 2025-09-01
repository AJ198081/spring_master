import {useLocation, useNavigate, useSearchParams} from "react-router-dom";
import {useEffect} from "react";
import {updateAuthStateWithJwt} from "../../services/Api.ts";
import {LoadSpinner} from "../common/LoadSpinner.tsx";

export const AdminDashboard = () => {

    const [searchParams] = useSearchParams();
    const navigateTo = useNavigate();
    const state = useLocation().state;

    useEffect(() => {
        updateAuthStateWithJwt(searchParams.get('token'));
        
        navigateTo(state?.from || '/', {replace: true});
        
    }, [navigateTo, searchParams, state]);

    return (
        <LoadSpinner />
    )
}
